package com.transempiric.Encryptor.property.wrapper;

import com.transempiric.Encryptor.property.EncryptorPropertyResolver;
import com.transempiric.Encryptor.property.EncryptorPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.Assert;

/**
 * <p>Wrapper for {@link PropertySource} instances that simply delegates the {@link #getProperty} method
 * to the {@link PropertySource} delegate instance to retrieve properties, while checking if the resulting
 * property is encrypted or not using the convention of surrounding encrypted values with "ENC()".</p>
 * <p>When an encrypted property is detected, it is decrypted using the provided {@link TextEncryptor}</p>
 */
public class EncryptorPropertySourceWrapper<T> extends PropertySource<T> implements EncryptorPropertySource<T> {
    private final PropertySource<T> delegate;
    EncryptorPropertyResolver resolver;

    public EncryptorPropertySourceWrapper(PropertySource<T> delegate, EncryptorPropertyResolver resolver) {
        super(delegate.getName(), delegate.getSource());
        Assert.notNull(delegate, "PropertySource delegate cannot be null");
        Assert.notNull(resolver, "EncryptablePropertyResolver cannot be null");
        this.delegate = delegate;
        this.resolver = resolver;
    }

    @Override
    public Object getProperty(String name) {
        return getProperty(resolver, delegate, name);
    }

    @Override
    public PropertySource<T> getDelegate() {
        return delegate;
    }
}
