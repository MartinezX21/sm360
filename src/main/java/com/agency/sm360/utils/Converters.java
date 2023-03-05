package com.agency.sm360.utils;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.UUID;

public class Converters {
    public static Converter<UUID, String> uuidToStringConverter = new Converter<UUID, String>() {
        public String convert(MappingContext<UUID, String> context) {
            return context.getSource() == null ? null : context.getSource().toString();
        }
    };

    public static Converter<String, UUID> stringToUuidConverter = new Converter<String, UUID>() {
        public UUID convert(MappingContext<String, UUID> context) {
            return context.getSource() == null ? null : UUID.fromString(context.getSource());
        }
    };
}
