package com.xstudio.spring.mybatis.pagehelper;

import com.xstudio.core.BaseModelObject;
import com.xstudio.core.service.IAbstractService;
import com.xstudio.spring.mybatis.antdesign.PageResponse;

import java.util.List;

/**
 * 基础服务
 *
 * @author xiaobiao
 * @version 2020/2/3
 */
public interface IMybatisPageHelperService<T extends BaseModelObject<K>, K> extends IAbstractService<T, K, PageBounds, PageResponse<T>, List<T>> {
}