package com.bosch.inst.base.querydsl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.*;

@Value
public class DslDocumentation {

    @JsonUnwrapped
    private final Map<String, Entry> filters = new HashMap<>();

    void documentFilter(String selector, String operator, String type) {
        documentFilter(selector, operator, type, new ArrayList<>());
    }

    void documentFilter(String selector, String operator, String type, Collection<String> values) {
        if (!filters.containsKey(selector)) {
            createNewFilterEntry(selector, operator, type, values);
        } else {
            reuseExistingFilterEntry(selector, operator, type, values);
        }
    }

    void documentSort(String selector, String type) {
        if (!filters.containsKey(selector)) {
            createNewSortEntry(selector, type);
        } else {
            reuseExistingSortEntry(selector);
        }
    }

    private void reuseExistingFilterEntry(String selector, String operator, String type, Collection<String> values) {
        Entry entry = filters.get(selector);

        if (operator != null && !operator.isEmpty()) {
            entry.operators.add(operator);
        }

        entry.type = type;
        entry.values.addAll(values);
    }

    private void createNewFilterEntry(String selector, String operator, String type, Collection<String> values) {
        Set<String> operators = new HashSet<>();
        operators.add(operator);
        filters.put(selector, new Entry(operators, type, false, new HashSet<>(values)));
    }


    private void createNewSortEntry(String selector, String type) {
        filters.put(selector, new Entry(new HashSet<>(), type, true, new HashSet<>()));
    }

    private void reuseExistingSortEntry(String selector) {
        Entry entry = filters.get(selector);
        entry.sortable = true;
    }

    @Data
    @AllArgsConstructor
    private class Entry {
        @JsonInclude(Include.NON_EMPTY)
        private final Collection<String> operators;
        private String type;
        boolean sortable;
        @JsonInclude(Include.NON_EMPTY)
        private final Collection<String> values;
    }

    public static class Types {
        public static final String BOOLEAN = "boolean";
        public static final String STRING = "string";
        public static final String INTEGER = "integer";
        public static final String FLOAT = "float";
        public static final String TIMESTAMP = "timestamp";
        public static final String ENUM = "enum";
    }
}
