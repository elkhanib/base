package com.bosch.inst.esurvey.base.querydsl.web;

import com.querydsl.core.types.OrderSpecifier;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;

@EqualsAndHashCode(callSuper = true)
public class PlcsSort extends QSort {

    private final Sort originalSort;

    public PlcsSort(Sort originalSort, OrderSpecifier<?>... orderSpecifiers) {
        super(orderSpecifiers);
        this.originalSort = originalSort;
    }

    public Sort getOriginalSort() {
        return originalSort;
    }
}
