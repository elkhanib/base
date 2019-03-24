package com.bosch.inst.base.querydsl;

import lombok.Value;
import org.springframework.core.convert.support.DefaultConversionService;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class DslConverters {

    private final Map<Class, NamedConverter> converters = new HashMap<>();
    private DefaultConversionService conversionService;

    public DslConverters() {
        conversionService = new DefaultConversionService();
        conversionService.addConverter(String.class, Instant.class, Instant::parse);

        converters.put(String.class, new NamedConverter<>(DslDocumentation.Types.STRING, source -> source));
        converters.put(Boolean.class, new NamedConverter<>(DslDocumentation.Types.BOOLEAN, source -> conversionService.convert(source, Boolean.class)));
        converters.put(Integer.class, new NamedConverter<>(DslDocumentation.Types.INTEGER, source -> conversionService.convert(source, Integer.class)));
        converters.put(Long.class, new NamedConverter<>(DslDocumentation.Types.LONG, source -> conversionService.convert(source, Long.class)));
        converters.put(Float.class, new NamedConverter<>(DslDocumentation.Types.FLOAT, source -> conversionService.convert(source, Float.class)));
        converters.put(Double.class, new NamedConverter<>(DslDocumentation.Types.DOUBLE, source -> conversionService.convert(source, Double.class)));
        converters.put(Instant.class, new NamedConverter<>(DslDocumentation.Types.TIMESTAMP, source -> conversionService.convert(source, Instant.class)));
        converters.put(Date.class, new NamedConverter<>(DslDocumentation.Types.DATE, source -> conversionService.convert(source, Date.class)));


    }

    <T> void addConverter(final Class<? extends T> targetType, NamedConverter<T> converter) {
        converters.put(targetType, converter);
    }

    <T> NamedConverter<T> getConverter(final Class<? extends T> targetType) {
        if (targetType.isEnum()) {
            return new NamedConverter<>(DslDocumentation.Types.ENUM, source -> conversionService.convert(source, targetType));
        }

        if (!converters.containsKey(targetType)) {
            throw new IllegalArgumentException(String.format("No converter defined for class '%s'. " +
                    "Call converter() first and define the converter for your custom type.", targetType.getSimpleName()));
        }

        return converters.get(targetType);
    }

    @Value
    static class NamedConverter<T> {
        String stringType;
        DslConverter<T> dslConverter;
    }
}
