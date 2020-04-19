package com.xstudio.core;

import com.alibaba.fastjson.annotation.JSONField;
import com.xstudio.core.date.DateTime;
import lombok.Data;

import java.io.Serializable;

/**
 * 基础model对象
 *
 * @author xiaobiao
 * @version 2020/2/2
 */
@Data
public class BaseModelObject<K> implements Serializable {
    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private DateTime createAt;

    /**
     * 创建人ID
     */
    private String createBy;

    /**
     * 更新时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private DateTime updateAt;

    /**
     * 更新人ID
     */
    private String updateBy;

    /**
     * 获取主键
     *
     * @return K
     */
    public K valueOfKey() {
        return null;
    }


    /**
     * 设置主键值
     *
     * @param value 主键值
     */
    public void assignKeyValue(K value) {
    }

    /**
     * 清楚铭感信息
     * 创建人ID
     * 更新人ID
     * 创建时间
     * 更新时间
     * 租户ID
     */
    public void emptySensitiveInfo() {
        setCreateBy(null);
        setCreateAt(null);
        setUpdateBy(null);
        setUpdateAt(null);
    }

}