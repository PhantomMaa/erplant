package com.hellocorp.example.kv.domain.service;

import com.hellocorp.example.kv.domain.model.BizGroupVO;
import com.hellocorp.example.kv.domain.model.ItemKeyVO;
import com.hellocorp.example.kv.domain.model.param.CacheItemParam;
import java.util.List;

public interface CacheService {

    /**
     * put cache
     *
     * @param param
     * @return
     */
    boolean put(CacheItemParam param);

    /**
     * get cache
     *
     * @param itemKeyVO
     * @return
     */
    byte[] get(ItemKeyVO itemKeyVO);

    /**
     * delete cache
     *
     * @param itemKeyVO
     * @return
     */
    boolean delete(ItemKeyVO itemKeyVO);

    /**
     * get cache, then delete
     *
     * @param itemKeyVO
     * @return
     */
    byte[] getAndDelete(ItemKeyVO itemKeyVO);

    /**
     * Get cache key-value pairs
     */
    List<byte[]> getList(BizGroupVO bizGroupVO);

}
