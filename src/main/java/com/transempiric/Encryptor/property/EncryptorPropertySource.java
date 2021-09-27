package com.transempiric.Encryptor.property;

import org.springframework.core.env.PropertySource;

public interface EncryptorPropertySource<T> {

    PropertySource<T> getDelegate();

    default Object getProperty(EncryptorPropertyResolver resolver, PropertySource<T> source, String name) {
        Object value = source.getProperty(name);
        if (value instanceof String) {
            String stringValue = String.valueOf(value);
            return resolver.resolvePropertyValue(stringValue);
        }
        return value;
    }
} 
