package com.bosch.inst.base.querydsl.web;

import com.querydsl.core.types.OrderSpecifier;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;

@EqualsAndHashCode(callSuper = true)
public class QuerySort extends QSort {

    private final Sort originalSort;

    public QuerySort(Sort originalSort, OrderSpecifier<?>... orderSpecifiers) {
        super(orderSpecifiers);
        this.originalSort = originalSort;
    }

    public Sort getOriginalSort() {
        return originalSort;
    }
}
