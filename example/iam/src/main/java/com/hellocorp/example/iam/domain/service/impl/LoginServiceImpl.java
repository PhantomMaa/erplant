package com.hellocorp.example.iam.domain.service.impl;

import com.hellocorp.example.iam.application.model.DecodeRoleVO;
import com.hellocorp.example.iam.application.model.UserLoginVO;
import com.hellocorp.example.iam.application.model.VerifyNormalCache;
import com.hellocorp.example.iam.application.model.param.RegisterUserParam;
import com.hellocorp.example.iam.application.model.param.UserLoginParam;
import com.hellocorp.example.iam.domain.constant.ErrorCode;
import com.hellocorp.example.iam.domain.constant.IamServiceConstant;
import com.hellocorp.example.iam.domain.constant.RoleBizType;
import com.hellocorp.example.iam.domain.converter.ExtUserConverter;
import com.hellocorp.example.iam.domain.entity.ExtUser;
import com.hellocorp.example.iam.domain.service.LoginService;
import com.hellocorp.example.iam.domain.util.MD5;
import com.hellocorp.example.iam.infra.client.CacheClient;
import com.hellocorp.example.iam.infra.client.LoginSessionClient;
import com.hellocorp.example.iam.infra.client.NotifyClient;
import com.hellocorp.example.iam.infra.client.model.CacheInputParam;
import com.hellocorp.example.iam.infra.client.model.JwtUserInfo;
import com.hellocorp.example.iam.infra.client.model.UserToken;
import com.hellocorp.automq.ddd.util.JacksonUtil;
import com.hellocorp.example.common.model.BaseErrorCode;
import com.hellocorp.example.common.model.Result;
import com.hellocorp.example.common.model.ServiceException;
import com.hellocorp.example.iam.domain.entity.Role;
import com.hellocorp.example.iam.domain.entity.User;
import com.hellocorp.example.iam.domain.entity.UserRoleRel;
import com.hellocorp.example.iam.domain.repository.RoleRepository;
import com.hellocorp.example.iam.domain.repository.UserRepository;
import com.hellocorp.example.iam.domain.repository.UserRoleRelRepository;
import com.hellocorp.example.iam.infra.persist.query.UserRoleRelPageNumQuery;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Resource
    UserRepository userRepository;

    @Resource
    NotifyClient notifyClient;

    @Resource
    LoginSessionClient loginSessionClient;

    @Resource
    CacheClient cacheClient;

    @Resource
    ExtUserConverter extUserConverter;

    @Resource
    UserRoleRelRepository roleRelRepository;

    @Resource
    RoleRepository roleRepository;

    @Override
    public String register(RegisterUserParam param) {
        ExtUser user = extUserConverter.toUser(param);
        String verifyToken = String.valueOf(UUID.randomUUID());
        // Temporarily, username, nick is specified as the email
        String email = user.getEmail();
        user.setNick(email);
        // check username unique
        User gotUser = userRepository.getByEmail(email);
        if (gotUser != null) {
            notifyClient.sendAccountAlreadyExistNotification(email);
            return verifyToken;
        }

        boolean optSuccess = userRepository.save(user);
        if (!optSuccess) {
            log.warn("register userRepository.save fail, user : {}", user);
            throw new RuntimeException("configItemRepository.save failure");
        }

        // send verify link email
        log.info("verifyToken : {}", verifyToken);

        long ttl = LocalDateTime.now().plusDays(3).toInstant(ZoneOffset.UTC).toEpochMilli();
        VerifyNormalCache verifyNormalCache = new VerifyNormalCache(param.getEmail(), param.getPassword());
        CacheInputParam itemParam = CacheInputParam.builder().itemKey(IamServiceConstant.USER_VERIFY_TOKEN_KEY_NOTATION_WITH_PASSWORD + verifyToken).itemValue(JacksonUtil.toJsonString(verifyNormalCache).getBytes()).ttl(ttl).build();
        cacheClient.put(itemParam);

        notifyClient.userSignUpVerifyTokenNotification(email, verifyToken);

        return verifyToken;
    }

    @Override
    public String decodeToken(String token) {
        JwtUserInfo userInfo = loginSessionClient.tokenDecode(token);
        String email = userInfo.getUsername();
        User user = userRepository.getByEmail(email);
        if (user == null) {
            log.info("decodeToken user not exist, token not valid");
            throw new ServiceException(BaseErrorCode.UNAUTHENTICATED);
        }

        return String.valueOf(user.getId());
    }

    @Override
    public DecodeRoleVO decodeTokenRole(String token, String tenantCode) {
        JwtUserInfo userInfo = loginSessionClient.tokenDecode(token);
        String email = userInfo.getUsername();
        User user = userRepository.getByEmail(email);
        if (user == null) {
            log.info("decodeToken user not exist, token not valid");
            throw new ServiceException(BaseErrorCode.UNAUTHENTICATED);
        }

        Long uid = user.getId();
        DecodeRoleVO decodeRoleVO = new DecodeRoleVO();
        decodeRoleVO.setUid(uid);
        if (StringUtils.isBlank(tenantCode)) {
            return decodeRoleVO;
        }

        UserRoleRelPageNumQuery query = UserRoleRelPageNumQuery.builder().uid(uid).bizType(RoleBizType.TENANT.name()).build();
        List<UserRoleRel> relList = roleRelRepository.pageNumQuery(query);
        for (UserRoleRel rel : relList) {
            if (tenantCode.equals(rel.getBizId())) {
                Role role = roleRepository.get(rel.getRoleId());
                if (role != null) {
                    decodeRoleVO.setRole(role.getName());
                }
            }
        }
        return decodeRoleVO;
    }

    @Override
    public UserLoginVO verifyNewNormalUser(String verifyToken) {
        Result<byte[]> cacheData = cacheClient.get(IamServiceConstant.USER_VERIFY_TOKEN_KEY_NOTATION_WITH_PASSWORD + verifyToken);
        if (!cacheData.isSuccess() || cacheData.getData() == null) {
            throw new ServiceException(BaseErrorCode.EXPIRED_CODE);
        }

        // disable token
        cacheClient.delete(IamServiceConstant.USER_VERIFY_TOKEN_KEY_NOTATION_WITH_PASSWORD + verifyToken);

        VerifyNormalCache verifyNormalCache = JacksonUtil.toObj(cacheData.getData(), VerifyNormalCache.class);
        String email = verifyNormalCache.getEmail();
        String password = verifyNormalCache.getPassword();
        User user = userRepository.getByEmail(email);
        // TODO 更改邮箱认证状态

        return loginByPassword(email, password, user);
    }

    @Override
    public UserLoginVO login(UserLoginParam userLoginParam) {
        String email = userLoginParam.getEmail();
        User user = userRepository.getByEmail(email);
        if (!user.getEmailVerified()) {
            log.info("email has not been verified, email : {}", email);
            throw new ServiceException(ErrorCode.INVALID_LOGIN_INPUT);
        }

        return loginByPassword(email, userLoginParam.getPassword(), user);
    }


    private UserLoginVO loginByPassword(String username, String password, User user) {
        UserToken token;
        try {
            token = loginSessionClient.login(username, password);
        } catch (IllegalArgumentException e) {
            throw new ServiceException(ErrorCode.INVALID_LOGIN_INPUT);
        }
        if (token == null) {
            log.warn("user login fail , check user login credential....");
            throw new ServiceException(ErrorCode.INVALID_LOGIN_INPUT);
        }

        return new UserLoginVO(token, extUserConverter.toUserVO(user));
    }

    @Override
    public void logout(Long uid) {
        User gotUser = userRepository.get(uid);
        if (gotUser == null) {
            throw new ServiceException(BaseErrorCode.INVALID_PARAM);
        }

        loginSessionClient.logout(uid);
    }

    @Override
    public UserLoginVO resetPassword(String token, String password) {
        // check token
        Result<byte[]> cacheData = cacheClient.get(IamServiceConstant.USER_RESET_PASSWORD_KEY_NOTATION + token);
        if (!cacheData.isSuccess() || cacheData.getData() == null) {
            throw new ServiceException(BaseErrorCode.EXPIRED_CODE);
        }

        // disable token
        cacheClient.delete(IamServiceConstant.USER_RESET_PASSWORD_KEY_NOTATION + token);

        // set password
        String email = new String(cacheData.getData(), StandardCharsets.UTF_8);
        User user = userRepository.getByEmail(email);
        user.setPasswd(MD5.encrypt(password));
        userRepository.save(user);

        return loginByPassword(email, password, user);

    }

    @Override
    public void sendResetPasswordLink(String email) {
        String changePWToken = String.valueOf(UUID.randomUUID());
        User user = userRepository.getByEmail(email);
        if (user == null) {
            return;
        }

        // send link to guide user to reset password
        long ttl = LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.UTC).toEpochMilli();
        CacheInputParam itemParam = CacheInputParam.builder().itemKey(IamServiceConstant.USER_RESET_PASSWORD_KEY_NOTATION + changePWToken).itemValue(email.getBytes()).ttl(ttl).build();
        cacheClient.put(itemParam);
        notifyClient.sendNotifyEmail(email, IamServiceConstant.RESET_PASSWORD_SUBJECT, "reset password email content...");
    }

}
