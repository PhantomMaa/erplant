package com.hellocorp.example.kv.domain.service;

import com.hellocorp.example.kv.domain.model.ItemKeyVO;
import com.hellocorp.example.kv.domain.model.ItemValueVO;
import com.hellocorp.example.kv.domain.model.param.ConfigItemParam;

public interface ConfigService {

    /**
     * get app-settings with identifier
     *
     * @param itemKeyVO
     * @return
     */
    ItemValueVO get(ItemKeyVO itemKeyVO);

    /**
     * create or update an app-settings
     *
     * @param param
     * @return
     */
    void save(ConfigItemParam param);

    /**
     * delete app-settings with identifier
     *
     * @param itemKeyVO
     * @return
     */
    void delete(ItemKeyVO itemKeyVO);

}
