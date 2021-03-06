package io.github.xbeeant.spring.mybatis.pagehelper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.github.xbeeant.antdesign.Pagination;
import io.github.xbeeant.core.ApiResponse;
import io.github.xbeeant.core.BaseModelObject;
import io.github.xbeeant.core.ErrorCodeConstant;
import io.github.xbeeant.core.service.AbstractServiceImpl;
import io.github.xbeeant.spring.mybatis.antdesign.PageResponse;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础服务实现
 *
 * @author xiaobiao
 * @version 2020/2/3
 */
public abstract class AbstractMybatisPageHelperServiceImpl<T extends BaseModelObject<K>, K> extends AbstractServiceImpl<T, K, PageBounds, PageResponse<T>, Page<T>> {

    @Override
    public ApiResponse<PageResponse<T>> fuzzySearch(T example) {
        ApiResponse<PageResponse<T>> apiResponse = new ApiResponse<>();
        ApiResponse<PageResponse<T>> pageListApiResponse = new ApiResponse<>();
        List<T> list = new ArrayList<>();
        int limit = 5000;
        int offset = 0;
        PageBounds pageBounds = new PageBounds(offset, limit);
        boolean doLoop = true;
        int total = 0;
        while (pageListApiResponse.getSuccess() && doLoop) {
            pageListApiResponse = fuzzySearchByPager(example, pageBounds);
            if (pageListApiResponse.getSuccess()) {
                doLoop = pageListApiResponse.getData().size() >= limit;
                list.addAll(pageListApiResponse.getData());
                total += pageListApiResponse.getData().getPagination().getTotal();
            }
            offset = offset + limit;
            pageBounds = new PageBounds(offset, limit);

        }

        if (list.isEmpty()) {
            apiResponse.setResult(ErrorCodeConstant.NO_MATCH, ErrorCodeConstant.NO_MATCH_MSG);
            return apiResponse;
        }
        PageResponse<T> pageList = new PageResponse<>(list, total, pageBounds.getPage());
        apiResponse.setData(pageList);
        return apiResponse;
    }

    @Override
    public ApiResponse<PageResponse<T>> fuzzySearchByPager(T example, PageBounds pageBounds) {
        ApiResponse<PageResponse<T>> apiResponse = new ApiResponse<>();

        PageHelper.orderBy(pageBounds.getOrders());
        PageHelper.startPage(pageBounds.getPage(), pageBounds.getLimit());
        Page<T> result = getRepositoryDao().fuzzySearch(example);
        if (result.isEmpty()) {
            apiResponse.setResult(ErrorCodeConstant.NO_MATCH, ErrorCodeConstant.NO_MATCH_MSG);
            return apiResponse;
        }
        PageResponse<T> pageList = new PageResponse<>(result, result.getTotal(), pageBounds.getPage());
        apiResponse.setData(pageList);

        return apiResponse;
    }

    @Override
    public ApiResponse<List<T>> selectAllByExample(T example) {
        List<String> orders = new ArrayList<>();
        orders.add("create_at desc");
        return selectAllByExample(example, orders);
    }

    private String getOrder(List<?> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object order : orders) {
            sb.append(order);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public ApiResponse<List<T>> selectAllByExample(T example, List<?> orders) {
        ApiResponse<List<T>> apiResponse = new ApiResponse<>();
        ApiResponse<PageResponse<T>> pageListApiResponse = new ApiResponse<>();
        List<T> list = new ArrayList<>();
        int limit = 5000;
        PageBounds pageBounds = new PageBounds(1, limit);
        int total = 0;
        pageBounds.setOrders(getOrder(orders));
        boolean doLoop = true;
        Pagination pagination = new Pagination();
        while (pageListApiResponse.getSuccess() && doLoop) {
            if (isWithBlobs()) {
                pageListApiResponse = selectByExampleWithBlobsByPager(example, pageBounds);
            } else {
                pageListApiResponse = selectByExampleByPager(example, pageBounds);
            }
            if (pageListApiResponse.getSuccess()) {
                doLoop = pageListApiResponse.getData().size() >= limit;
                list.addAll(pageListApiResponse.getData());

                pageBounds.setPage(pageBounds.getPage() + 1);
                total += pageListApiResponse.getData().getPagination().getTotal();
            }
        }

        if (list.isEmpty()) {
            apiResponse.setResult(ErrorCodeConstant.NO_MATCH, ErrorCodeConstant.NO_MATCH_MSG);
            return apiResponse;
        }
        PageResponse<T> pageList = new PageResponse<>(list, total, 1);
        apiResponse.setData(pageList);
        return apiResponse;
    }

    @Override
    public ApiResponse<PageResponse<T>> selectByExampleByPager(T example, PageBounds pageBounds) {
        ApiResponse<PageResponse<T>> apiResponse = new ApiResponse<>();
        PageHelper.orderBy(pageBounds.getOrders());
        PageHelper.startPage(pageBounds.getPage(), pageBounds.getLimit());
        Page<T> result = getRepositoryDao().selectByExample(example, pageBounds);
        if (result.isEmpty()) {
            apiResponse.setResult(ErrorCodeConstant.NO_MATCH, ErrorCodeConstant.NO_MATCH_MSG);
            return apiResponse;
        }

        PageResponse<T> pageList = new PageResponse<>(result, pageBounds.getTotal(), pageBounds.getPage());
        apiResponse.setData(pageList);
        return apiResponse;
    }

    @Override
    public ApiResponse<PageResponse<T>> selectByExampleWithBlobsByPager(T example, PageBounds pageBounds) {
        ApiResponse<PageResponse<T>> apiResponse = new ApiResponse<>();

        PageHelper.orderBy(pageBounds.getOrders());
        PageHelper.startPage(pageBounds.getPage(), pageBounds.getLimit());
        Page<T> result = getRepositoryDao().selectByExampleWithBLOBs(example, pageBounds);
        if (result.isEmpty()) {
            apiResponse.setResult(ErrorCodeConstant.NO_MATCH, ErrorCodeConstant.NO_MATCH_MSG);
            return apiResponse;
        }
        PageResponse<T> pageList = new PageResponse<>(result, result.getTotal(), pageBounds.getPage());
        apiResponse.setData(pageList);
        return apiResponse;
    }

    @Override
    public ApiResponse<T> selectOneByExample(T example, List<?> orders) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        Page<T> result;
        PageBounds pageBounds = new PageBounds(0, 2);
        pageBounds.setOrders(getOrder(orders));

        try {
            result = getRepositoryDao().selectByExampleWithBLOBs(example, pageBounds);
        } catch (Exception e) {
            result = getRepositoryDao().selectByExample(example, pageBounds);
        }
        if (null == result || result.isEmpty()) {
            apiResponse.setResult(ErrorCodeConstant.NO_MATCH, ErrorCodeConstant.NO_MATCH_MSG);
            return apiResponse;
        }

        if (result.size() > 1) {
            apiResponse.setResult(ErrorCodeConstant.CONFLICT, ErrorCodeConstant.CONFLICT_MSG);
            return apiResponse;
        }

        apiResponse.setData(result.get(0));
        return apiResponse;
    }

    @Override
    public ApiResponse<T> selectOneByExample(T example) {
        List<?> orders = new ArrayList<>();
        return selectOneByExample(example, orders);
    }

    @Override
    public ApiResponse<T> selectOneByExampleWithBlobs(T example, List<?> orders) {
        ApiResponse<T> apiResponse = new ApiResponse<>();

        PageBounds pageBounds = new PageBounds(0, 2);
        pageBounds.setOrders(getOrder(orders));

        Page<T> result = getRepositoryDao().selectByExampleWithBLOBs(example, pageBounds);

        if (CollectionUtils.isEmpty(result)) {
            apiResponse.setResult(ErrorCodeConstant.NO_MATCH, ErrorCodeConstant.NO_MATCH_MSG);
            return apiResponse;
        }

        if (result.size() > 1) {
            apiResponse.setResult(ErrorCodeConstant.CONFLICT, ErrorCodeConstant.CONFLICT_MSG);
            return apiResponse;
        }

        apiResponse.setData(result.get(0));
        return apiResponse;
    }


    @Override
    public ApiResponse<String> uniqueValid(T record) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        Object key = record.getKey();
        if (null == key) {
            Long existNumber = getRepositoryDao().countByExample(record);
            if (existNumber > 0) {
                apiResponse.setResult(ErrorCodeConstant.CONFLICT, ErrorCodeConstant.CONFLICT_MSG);
                return apiResponse;
            }

            return apiResponse;
        }

        /* 主键存在 参数获取的对象主键不一致时 返回错误 */
        record.assignKeyValue(null);

        PageBounds pageBounds = new PageBounds(0, 2);

        Page<T> dbRecord = getRepositoryDao().selectByExample(record, pageBounds);
        if (null != dbRecord && !dbRecord.isEmpty() && !key.equals(dbRecord.get(0).getKey())) {
            apiResponse.setResult(ErrorCodeConstant.CONFLICT, ErrorCodeConstant.CONFLICT_MSG);
            return apiResponse;
        }

        return apiResponse;
    }
}
