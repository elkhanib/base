package com.bosch.inst.base.querydsl.docs;

import com.bosch.inst.base.querydsl.web.QueryDslConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysema.commons.lang.Assert;
import lombok.SneakyThrows;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.snippet.TemplatedSnippet;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class QuerydslParameterSnippet extends TemplatedSnippet {

    private static final String VALUES = "values";
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<Entry> filtersToDocument;

    private QuerydslParameterSnippet(List<Entry> filtersToDocument) {
        super("querydsl-parameter", new HashMap<>());
        Assert.notEmpty(filtersToDocument, "Your test did not provide any filter to document");
        this.filtersToDocument = filtersToDocument;
    }

    public static Entry queryParam(String selector) {
        return new SimpleEntry(selector);
    }

    public static Entry queryParam(String selector, String... values) {
        return new ValueEntry(selector, Arrays.asList(values));
    }

    public static Entry queryParam(String selector, Iterable<String> values) {
        return new ValueEntry(selector, values);
    }

    public static <T> Entry queryParam(String selector, T[] values) {
        return new ValueEntry(selector, Stream.of(values).map(Object::toString).collect(Collectors.toList()));
    }

    public static QuerydslParameterSnippet document(Entry... entries) {
        return new QuerydslParameterSnippet(Arrays.asList(entries));
    }

    public static QuerydslParameterSnippet documentSearch(String... selectors) {
        List<Entry> list = Stream.of(selectors).map(SimpleEntry::new).collect(Collectors.toList());
        return new QuerydslParameterSnippet(list);
    }

    @Override
    public Map<String, Object> createModel(Operation operation) {
        Map<String, Object> model = new HashMap<>();

        List<HashMap<String, String>> documentationEntries = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> filtersJson = readFilterJson(operation);

        List<String> documentedFilters = new ArrayList<>();

        while (filtersJson.hasNext()) {
            Map.Entry<String, JsonNode> filterInJson = filtersJson.next();
            String selector = filterInJson.getKey();
            Entry filterInDocumentation = getFilterInDocumentation(selector);

            HashMap<String, String> filterEntry = new HashMap<>();
            JsonNode value = filterInJson.getValue();
            filterEntry.put("selector", selector);
            filterEntry.put("operation", operators(value));
            filterEntry.put("type", value.get("type").asText());
            filterEntry.put("sortable", value.get("sortable").asBoolean() ? "yes" : "no");

            if (filterInDocumentation instanceof ValueEntry) {
                Iterable<String> values = ((ValueEntry) filterInDocumentation).getValues();
                filterEntry.put(VALUES, String.join(",", values));
            } else {
                filterEntry.put(VALUES, value.get(VALUES) == null ? "" : printString(value.get(VALUES).iterator()));
            }

            documentationEntries.add(filterEntry);
            documentedFilters.add(filterInJson.getKey());
        }

        if (documentedFilters.isEmpty()) {
            throw new InvalidParameterException(String.format("The %s got no filters to document. Did you add %s to your TestConfiguration?",
                    QuerydslParameterSnippet.class.getSimpleName(), QueryDslConfiguration.class
            ));
        }

        validateFiltersDocumented(documentedFilters);

        Function<HashMap<String, String>, String> keyExtractor = key -> key.get("selector");
        documentationEntries.sort(Comparator.comparing(keyExtractor));
        model.put("parameters", documentationEntries);
        return model;
    }

    private Entry getFilterInDocumentation(String selector) {
        Optional<Entry> first = filtersToDocument.stream().filter(e -> e.selector().equals(selector)).findFirst();
        return first.orElseThrow(() -> new SnippetException(
                String.format("Filter %s available at REST-Controller but not documented", selector)));
    }

    private String operators(JsonNode node) {
        JsonNode operators = node.get("operators");
        if (operators == null) {
            return "";
        }
        return printString(operators.iterator());
    }

    private void validateFiltersDocumented(List<String> documentedFilters) {
        for (Entry filterToDocument : filtersToDocument) {
            if (!documentedFilters.contains(filterToDocument.selector())) {
                throw new SnippetException(
                        String.format("Filter %s not available at REST-Controller", filterToDocument));
            }
        }
    }

    private String printString(Iterator<JsonNode> iterator) {
        List<String> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next().asText());
        }
        return String.join(",", list);
    }

    @SneakyThrows
    private Iterator<Map.Entry<String, JsonNode>> readFilterJson(Operation operation) {
        return mapper.readTree(operation.getResponse().getContentAsString()).fields();
    }
}
