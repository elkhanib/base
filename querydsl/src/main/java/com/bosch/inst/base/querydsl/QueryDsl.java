package com.bosch.inst.base.querydsl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.ast.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class QueryDsl implements Filtering, Sorting {

    private final DslBinding dslBinding;
    private final SortBinding sortBinding;
    private final DslDocumentation documentation;
    private RSQLParser parser;
    private final Set<ComparisonOperator> operators = RSQLOperators.defaultOperators();
    private final DslConverters dslConverters;

    public interface Customizer {
        void configureFilter(Filtering filter);

        void configureSorting(Sorting sorting);
    }

    public final class MediaType {
        public static final String APPLICATION_FILTERS_JSON_VALUE = "application/vnd.filters+json";
    }

    public QueryDsl() {
        dslBinding = new DslBinding();
        sortBinding = new SortBinding();
        documentation = new DslDocumentation();
        parser = new RSQLParser();
        dslConverters = new DslConverters();
    }

    @Override
    public <T extends Path<S>, S> Binding<T, S> createFilter(T propertyInQClass) {
        return new Binding<>(dslBinding.newBinding(propertyInQClass).selector(defaultSelector(propertyInQClass)));
    }

    @Override
    public <T extends Path<S>, S> Binding<T, S> createFilter(T propertyInQClass, String dtoProperty) {
        return new Binding<>(dslBinding.newBinding(propertyInQClass).selector(dtoProperty));
    }

    @Override
    public <T extends ComparableExpressionBase<S> & Path<S>, S extends Comparable> void createSort(T propertyInQClass) {
        String selector = defaultSelector(propertyInQClass);
        createSort(propertyInQClass, selector);
    }

    private <T extends Expression> String defaultSelector(T propertyInQClass) {
        return propertyInQClass.toString().split("\\.", 2)[1];
    }

    @Override
    public <T extends ComparableExpressionBase<S> & Path<S>, S extends Comparable> void createSort(T propertyInQClass, String dtoProperty) {
        sortBinding.newBinding(propertyInQClass).selector(dtoProperty);
        DslConverters.NamedConverter<? extends S> converter = dslConverters.getConverter(propertyInQClass.getType());
        documentation.documentSort(dtoProperty, converter.getStringType());
    }

    @RequiredArgsConstructor
    public class Binding<T extends Path<S>, S> {
        private final DslBinding.PathBinder<T, S> pathbinder;

        public Action withOperator(String operator) {
            ComparisonOperator op = new ComparisonOperator(operator);
            addComparisionOperator(op);
            return new Action();
        }

        public Action withOperator(ComparisonOperator operator) {
            addComparisionOperator(operator);
            return new Action();
        }

        private void addComparisionOperator(ComparisonOperator operator) {
            pathbinder.operator(operator);
            if (!operators.contains(operator)) {
                operators.add(operator);
                parser = new RSQLParser(operators);
            }
        }

        public class Action {

            public <U> ConvertAction<U> andConvertParameter(String documentationType, Class<U> targetType, DslConverter<U> converter) {
                dslConverters.addConverter(targetType, new DslConverters.NamedConverter<>(documentationType, converter));
                return new ConvertAction<>(documentationType, targetType);
            }

            public <U> ConvertAction<U> andConvertParameter(String documentationType, Class<U> targetType, DslConverter<U> converter, Collection<String> possibleValues) {
                dslConverters.addConverter(targetType, new DslConverters.NamedConverter<>(documentationType, converter));
                return new ConvertAction<>(documentationType, targetType, possibleValues);
            }

            public Binding<T, S> andOperation(DslBinding.PathBinding<T, S> binding) {
                DslConverters.NamedConverter<S> converter = dslConverters.getConverter(pathbinder.getPath().getType());
                pathbinder.converter(converter.getDslConverter()).binding(binding);

                documentation.documentFilter(pathbinder.getSelector(), pathbinder.getOperator().toString(), converter.getStringType(), getPossibleEnumValues());

                return createFilter(pathbinder.getPath(), pathbinder.getSelector());
            }

        }

        @RequiredArgsConstructor
        @AllArgsConstructor
        public class ConvertAction<U> {
            private final String documentationType;
            private final Class<U> targetClass;
            private Collection<String> customValues;

            public Binding<T, S> andOperation(DslBinding.PathBinding<T, U> binding) {
                DslConverters.NamedConverter<U> converter = dslConverters.getConverter(targetClass);
                pathbinder.converter(targetClass, converter.getDslConverter()).binding(binding);

                Collection<String> values = customValues == null ? getPossibleEnumValues() : customValues;
                documentation.documentFilter(pathbinder.getSelector(), pathbinder.getOperator().toString(), documentationType, values);

                return createFilter(pathbinder.getPath(), pathbinder.getSelector());
            }
        }

        private Collection<String> getPossibleEnumValues() {
            Class<? extends S> type = pathbinder.getPath().getType();
            if (type.isEnum()) {
                return Stream.of(type.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        }
    }

    @SneakyThrows
    public String printFilters() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(documentation.getFilters());
    }

    /**
     * parses the query and converts it into a {@link Predicate}
     * @param query query to be converted
     * @return converted {@link Predicate} or null if no query is specified
     */
    public Predicate processFilter(String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        Node parse;
        try {
            parse = parser.parse(query);
        } catch (RSQLParserException e) {
            throw new UnknownRequestParameterException(String.format("%s ist not a valid query", query), e);
        }
        return parse.accept(new VisitorAdapter());
    }

    public OrderSpecifier processSort(String selector, Order direction) {
        Assert.hasText(selector, "sort cannot be null");
        return sortBinding.invokeBinding(selector, direction);
    }

    private class VisitorAdapter extends NoArgRSQLVisitorAdapter<Predicate> {

        @Override
        public Predicate visit(AndNode node) {
            return combineChildren(node.getChildren(), BooleanBuilder::and);
        }

        @Override
        public Predicate visit(OrNode node) {
            return combineChildren(node.getChildren(), BooleanBuilder::or);
        }

        private Predicate combineChildren(Iterable<Node> iterable, BiFunction<BooleanBuilder, Predicate, Predicate> function) {
            BooleanBuilder builder = new BooleanBuilder();
            for (Node n : iterable) {
                if (n instanceof ComparisonNode) {
                    function.apply(builder, visit((ComparisonNode) n));
                } else if (n instanceof AndNode) {
                    function.apply(builder, visit((AndNode) n));
                } else if (n instanceof OrNode) {
                    function.apply(builder, visit((OrNode) n));
                }
            }
            return builder.getValue();
        }

        @Override
        public Predicate visit(ComparisonNode node) {
            return convert(node);
        }

        private Predicate convert(ComparisonNode node) {
            ComparisonOperator operator = node.getOperator();
            String selector = node.getSelector();
            String argument = node.getArguments().get(0);

            return dslBinding.invokeBinding(selector, operator, argument);
        }
    }
}
