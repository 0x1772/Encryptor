package com.transempiric.Encryptor.property;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.env.PropertySource;

/**
 */
public class EncryptorMutablePropertySourcesInterceptor implements MethodInterceptor {
    private final EncryptorInterceptionMode interceptionMode;
    private final EncryptorPropertyResolver resolver;

    public EncryptorMutablePropertySourcesInterceptor(EncryptorInterceptionMode interceptionMode, EncryptorPropertyResolver resolver) {
        this.interceptionMode = interceptionMode;
        this.resolver = resolver;
    }

    private Object makeEncryptable(Object propertySource) {
        return EncryptorPropertySourceConverter.makeEncryptable(interceptionMode, resolver, (PropertySource<?>) propertySource);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String method = invocation.getMethod().getName();
        Object[] arguments = invocation.getArguments();
        switch (method) {
            case "addFirst":
                return invocation.getMethod().invoke(invocation.getThis(), makeEncryptable(arguments[0]));
            case "addLast":
                return invocation.getMethod().invoke(invocation.getThis(), makeEncryptable(arguments[0]));
            case "addBefore":
                return invocation.getMethod().invoke(invocation.getThis(), arguments[0], makeEncryptable(arguments[1]));
            case "addAfter":
                return invocation.getMethod().invoke(invocation.getThis(), arguments[0], makeEncryptable(arguments[1]));
            case "replace":
                return invocation.getMethod().invoke(invocation.getThis(), arguments[0], makeEncryptable(arguments[1]));
            default:
                return invocation.proceed();
        }
    }
}
