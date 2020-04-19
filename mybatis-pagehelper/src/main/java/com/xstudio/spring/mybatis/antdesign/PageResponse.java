package com.xstudio.spring.mybatis.antdesign;

import com.xstudio.antdesign.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author xiaobiao
 * @version 2020/2/3
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PageResponse<E> extends ArrayList<E> {
    private static final long serialVersionUID = 1412759446332294208L;

    private Pagination pagination;

    public PageResponse() {
    }

    public PageResponse(Collection<? extends E> c) {
        super(c);
    }

    public PageResponse(Collection<? extends E> c, long total, int page) {
        super(c);
        this.pagination = new Pagination(Math.toIntExact(total), page);
    }
}