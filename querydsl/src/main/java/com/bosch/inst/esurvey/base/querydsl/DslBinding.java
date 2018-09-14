package com.bosch.inst.esurvey.base.querydsl;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DslBinding {

    private final HashMap<String, PathInformation> typeOperatorBinding;

    DslBinding() {
        typeOperatorBinding = new HashMap<>();
    }

    <T extends Path<S>, S> PathBinder<T, S> newBinding(T path) {
        return new PathBinder<>(path);
    }

    @Getter
    public final class PathBinder<T extends Path, S> {
        private T path;
        private ComparisonOperator operator;
        private String selector;
        private DslConverter converter;

        public PathBinder(T path) {
            Assert.notNull(path, "path cannot be null");
            this.path = path;
        }

        public PathBinder<T, S> operator(ComparisonOperator operator) {
            Assert.notNull(operator, "operator cannot be null");
            this.operator = operator;
            return this;
        }

        public PathBinder<T, S> selector(String selector) {
            Assert.hasText(selector, "selector cannot be empty");
            this.selector = selector;
            return this;
        }

        public PathBinder<T, S> converter(DslConverter<S> converter) {
            Assert.notNull(converter, "converter cannot be null");
            this.converter = converter;
            return this;
        }

        @SuppressWarnings("squid:S1172")
        public <U> PathBinder<T, U> converter(Class<U> targetType, DslConverter<U> converter) {
            Assert.notNull(converter, "converter cannot be null");
            Assert.hasText(selector, "set 'selector' before calling 'binding'");
            Assert.notNull(operator, "set 'operator' before calling 'binding'");
            PathBinder<T, U> binder = new PathBinder<>(path);
            binder.selector = selector;
            binder.operator = operator;
            binder.converter = converter;
            return binder;
        }

        public void binding(PathBinding<T, S> binding) {
            Assert.notNull(binding, "binding cannot be null");
            Assert.hasText(selector, "set 'selector' before calling 'binding'");
            Assert.notNull(operator, "set 'operator' before calling 'binding'");

            PathInformation information = typeOperatorBinding.get(selector);
            if (information == null) {
                information = new PathInformation(path, new HashMap<>());
                information.getOperations().put(operator, new PathInformation.OperatorInformation(binding, converter));
                typeOperatorBinding.put(selector, information);
            } else {
                information.getOperations().put(operator, new PathInformation.OperatorInformation(binding, converter));
            }
        }
    }

    public interface PathBinding<T, S> {

        Predicate bind(T path, S value);
    }

    Predicate invokeBinding(String selector, ComparisonOperator operator, String value) {
        Assert.hasText(selector, "selector cannot be empty");
        Assert.notNull(operator, "operator cannot be null");
        Assert.notNull(value, "value cannot be null");

        PathInformation pathInfo = typeOperatorBinding.get(selector);
        if (pathInfo == null) {
            throw new UnknownRequestParameterException(String.format(
                    "No filter-binding found for '%s' and operator '%s'", selector, operator));
        }

        PathInformation.OperatorInformation operatorInfo = pathInfo.getOperations().get(operator);
        if (operatorInfo == null) {
            throw new UnknownRequestParameterException(String.format(
                    "No filter-binding found for '%s' and operator '%s'", selector, operator));
        }

        Path<?> path = typeOperatorBinding.get(selector).getPath();
        Class<?> pathType = path.getType();

        Object convert;
        try {
            convert = operatorInfo.getConverter().convert(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Cannot parse '%s' to '%s'", value, pathType.getSimpleName()), e);
        }

        return operatorInfo.getBinding().bind(path, convert);
    }

    @Value
    private static class PathInformation {
        @NonNull
        private Path path;
        @NonNull
        private Map<ComparisonOperator, OperatorInformation> operations;

        @AllArgsConstructor
        @Value
        private static class OperatorInformation {
            @NonNull
            PathBinding binding;
            @NonNull
            DslConverter converter;
        }
    }
}
