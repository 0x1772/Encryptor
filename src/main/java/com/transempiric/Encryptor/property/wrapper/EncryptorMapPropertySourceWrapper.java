package com.transempiric.Encryptor.property.wrapper;

import com.transempiric.Encryptor.property.EncryptorPropertyResolver;
import com.transempiric.Encryptor.property.EncryptorPropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;

import java.util.Map;

/**
 */
public class EncryptorMapPropertySourceWrapper extends MapPropertySource implements EncryptorPropertySource<Map<String, Object>> {
    EncryptorPropertyResolver resolver;
    private MapPropertySource delegate;

    public EncryptorMapPropertySourceWrapper(MapPropertySource delegate, EncryptorPropertyResolver resolver) {
        super(delegate.getName(), delegate.getSource());
        Assert.notNull(delegate, "PropertySource delegate cannot be null");
        Assert.notNull(resolver, "EncryptorPropertyResolver cannot be null");
        this.resolver = resolver;
        this.delegate = delegate;
    }

    @Override
    public Object getProperty(String name) {
        return getProperty(resolver, delegate, name);
    }

    @Override
    public PropertySource<Map<String, Object>> getDelegate() {
        return delegate;
    }

    @Override
    public boolean containsProperty(String name) {
        return delegate.containsProperty(name);
    }

    @Override
    public String[] getPropertyNames() {
        return delegate.getPropertyNames();
    }
} 
