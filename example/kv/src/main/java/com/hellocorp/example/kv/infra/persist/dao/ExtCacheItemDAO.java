package com.hellocorp.example.kv.infra.persist.dao;

import com.hellocorp.example.kv.infra.persist.bk.CacheItemBK;
import com.hellocorp.example.kv.infra.persist.dataobject.CacheItemDO;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExtCacheItemDAO extends CacheItemDAO {

    /**
     * get cache item considering ttl
     */
    @Select("""
            SELECT *
            FROM kv_cache_item
            WHERE 1 = 1
                AND group_id = #{bk.groupId}
                AND item_key = #{bk.itemKey}
                AND (ttl is null OR ttl > #{timestamp})
            """)
    CacheItemDO getUnexpired(@Param("bk") CacheItemBK bk, @Param("timestamp") long timestamp);

    @Select("""
            SELECT *
            FROM kv_cache_item
            WHERE 1 = 1
                AND group_id = #{groupId}
                AND (ttl is null OR ttl > #{timestamp})
            """)
    List<CacheItemDO> queryUnexpired(@Param("groupId") Long groupId, @Param("timestamp") long timestamp);

    @Delete("""
            DELETE
            FROM kv_cache_item
            WHERE ttl is not null AND ttl < #{timestamp}
            """)
    int deleteExpired(@Param("timestamp") long timestamp);

}
