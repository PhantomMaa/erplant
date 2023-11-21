package com.hellocorp.example.iam.domain.converter;

import com.hellocorp.example.iam.application.model.UserVO;
import com.hellocorp.example.iam.application.model.param.RegisterUserParam;
import com.hellocorp.example.iam.domain.entity.ExtUser;
import com.hellocorp.example.iam.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring"
)
public interface ExtUserConverter extends UserConverter {
    UserVO toUserVO(User user);

    ExtUser toUser(RegisterUserParam param);

    UserVO toUserDTO(User user);
}
