package com.transempiric.Encryptor.property.wrapper;

import com.transempiric.Encryptor.property.EncryptorPropertyResolver;
import com.transempiric.Encryptor.property.EncryptorPropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;

/**
 */
public class EncryptorEnumerablePropertySourceWrapper<T> extends EnumerablePropertySource<T> implements EncryptorPropertySource<T> {
    private final EnumerablePropertySource<T> delegate;
    private final EncryptorPropertyResolver resolver;

    public EncryptorEnumerablePropertySourceWrapper(EnumerablePropertySource<T> delegate, EncryptorPropertyResolver resolver) {
        super(delegate.getName(), delegate.getSource());
        Assert.notNull(delegate, "PropertySource delegate cannot be null");
        Assert.notNull(resolver, "EncryptorPropertyResolver cannot be null");
        this.delegate = delegate;
        this.resolver = resolver;
    }

    @Override
    public Object getProperty(String name) {
        return getProperty(resolver, delegate, name);
    }

    @Override
    public String[] getPropertyNames() {
        return delegate.getPropertyNames();
    }

    @Override
    public PropertySource<T> getDelegate() {
        return delegate;
    }
} 
