package com.transempiric.Encryptor.property;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.env.PropertySource;

/**
 * @author Inenarratus
 */
public class EncryptorPropertySourceMethodInterceptor<T> implements MethodInterceptor, EncryptorPropertySource<T> {

    private final EncryptorPropertyResolver resolver;
    private final PropertySource<T> delegate;

    public EncryptorPropertySourceMethodInterceptor(PropertySource<T> delegate, SimpleEncryptorPropertyResolver resolver) {
        this.resolver = resolver;
        this.delegate = delegate;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (isGetDelegateCall(invocation)) {
            return getDelegate();
        }
        Object returnValue = invocation.proceed();
        if (isGetPropertyCall(invocation)) {
            return getProperty(resolver, getPropertySource(invocation), getNameArgument(invocation));
        }
        return returnValue;
    }

    @SuppressWarnings("unchecked")
    private PropertySource<T> getPropertySource(MethodInvocation invocation) {
        return (PropertySource<T>) invocation.getThis();
    }

    private String getNameArgument(MethodInvocation invocation) {
        return (String) invocation.getArguments()[0];
    }

    private boolean isGetDelegateCall(MethodInvocation invocation) {
        return invocation.getMethod().getName().equals("getDelegate");
    }

    private boolean isGetPropertyCall(MethodInvocation invocation) {
        return invocation.getMethod().getName().equals("getProperty")
                && invocation.getMethod().getParameters().length == 1
                && invocation.getMethod().getParameters()[0].getType() == String.class;
    }

    @Override
    public PropertySource<T> getDelegate() {
        return delegate;
    }
}
