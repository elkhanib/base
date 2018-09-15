package com.bosch.inst.base.querydsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.security.InvalidParameterException;
import java.util.HashMap;

public class SortBinding {

    private static final Logger log = LoggerFactory.getLogger(SortBinding.class);

    private final HashMap<String, ComparableExpressionBase> selectorPathBinding;

    public SortBinding() {
        this.selectorPathBinding = new HashMap<>();
    }

    <T extends ComparableExpressionBase<S> & Path<S>, S extends Comparable> PathBinder<T, S> newBinding(T path) {
        return new PathBinder<>(path);
    }

    @Getter
    public final class PathBinder<T extends ComparableExpressionBase<S> & Path<S>, S extends Comparable> {
        private T path;

        public PathBinder(T path) {
            Assert.notNull(path, "path cannot be null");
            this.path = path;
        }

        public void selector(String selector) {
            Assert.hasText(selector, "selector cannot be empty");
            checkIndexAnnotationPresentWhenDocument();
            selectorPathBinding.put(selector, path);
        }

        private void checkIndexAnnotationPresentWhenDocument() {
            if (isMongoDocument()) {
                Path<?> parent = path.getMetadata().getParent();

                String fieldName = this.path.toString();
                if (fieldName.contains(".")) {
                    String[] split = fieldName.split("\\.", -1);
                    fieldName = split[split.length - 1];
                }
                boolean atIndexedPresent = false;
                try {
                    atIndexedPresent = ReflectionUtils.findField(parent.getType(), fieldName).isAnnotationPresent(getIndexedClass());
                    if (!atIndexedPresent) {
                        throw new InvalidParameterException(String.format("field: '%s' in class: '%s' must be annotated with '%s'",
                                fieldName, parent.getType(), getIndexedClass().getName() + "(background = true)"));
                    }
                } catch (ClassNotFoundException e) {
                    Assert.state(Boolean.FALSE, "Missing MongoDB dependencies " + e);
                }
            }
        }

        private boolean isMongoDocument() {
            try {
                return path.getRoot().getType().isAnnotationPresent(getDocumentClass());
            } catch (ClassNotFoundException e) {
                //MongoDB Document class is not in the class path thus the service does not use MongoDB to
                //persist Pojos and the validation that member variables are indexed is not necessary.
                log.info("MongoDB Document class is not in the class path. Indexed annotation test not necessary " + e);
                return false;
            }
        }

        private Class getIndexedClass() throws ClassNotFoundException {
            return Class.forName("org.springframework.data.mongodb.core.index.Indexed");
        }

        private Class getDocumentClass() throws ClassNotFoundException {
            return Class.forName("org.springframework.data.mongodb.core.mapping.Document");
        }
    }

    public OrderSpecifier invokeBinding(String selector, Order direction) {
        Assert.hasText(selector, "selector cannot be null");
        Assert.notNull(direction, "direction cannot be null");

        ComparableExpressionBase path = selectorPathBinding.get(selector);
        if (path == null) {
            throw new UnknownRequestParameterException(String.format(
                    "No sort-binding found for '%s'", selector));
        }

        if (direction == Order.ASC) {
            return path.asc();
        }

        if (direction == Order.DESC) {
            return path.desc();
        }

        throw new IllegalArgumentException(String.format(
                "Could not invoke binding for %s and %s", selector, direction));
    }
}
