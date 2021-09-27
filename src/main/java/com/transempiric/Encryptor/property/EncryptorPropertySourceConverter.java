package com.transempiric.Encryptor.property;

import com.transempiric.Encryptor.property.wrapper.EncryptorEnumerablePropertySourceWrapper;
import com.transempiric.Encryptor.property.wrapper.EncryptorMapPropertySourceWrapper;
import com.transempiric.Encryptor.property.wrapper.EncryptorPropertySourceWrapper;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.env.*;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 */
public class EncryptorPropertySourceConverter {

    public static void convertPropertySources(EncryptorInterceptionMode interceptionMode, EncryptorPropertyResolver propertyResolver, MutablePropertySources propSources) {
        StreamSupport.stream(propSources.spliterator(), false)
                .filter(ps -> !(ps instanceof EncryptorPropertySource))
                .map(ps -> makeEncryptable(interceptionMode, propertyResolver, ps))
                .collect(toList())
                .forEach(ps -> propSources.replace(ps.getName(), ps));
    }

    @SuppressWarnings("unchecked")
    public static <T> PropertySource<T> makeEncryptable(EncryptorInterceptionMode interceptionMode, EncryptorPropertyResolver propertyResolver, PropertySource<T> propertySource) {
        if (propertySource instanceof EncryptorPropertySource) {
            return propertySource;
        }
        PropertySource<T> encryptablePropertySource = convertPropertySource(interceptionMode, propertyResolver, propertySource);
        // System.out.println("Converting PropertySource {} [{}] to {}", propertySource.getName(), propertySource.getClass().getName(), AopUtils.isAopProxy(encryptablePropertySource) ? "AOP Proxy" : encryptablePropertySource.getClass().getName());
        return encryptablePropertySource;
    }


    private static <T> PropertySource<T> convertPropertySource(EncryptorInterceptionMode interceptionMode, EncryptorPropertyResolver propertyResolver, PropertySource<T> propertySource) {
        return interceptionMode == EncryptorInterceptionMode.PROXY
                ? proxyPropertySource(propertySource, propertyResolver) : instantiatePropertySource(propertySource, propertyResolver);
    }

    public static MutablePropertySources proxyPropertySources(EncryptorInterceptionMode interceptionMode, EncryptorPropertyResolver propertyResolver, MutablePropertySources propertySources) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(MutablePropertySources.class);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addInterface(PropertySources.class);
        proxyFactory.setTarget(propertySources);
        proxyFactory.addAdvice(new EncryptorMutablePropertySourcesInterceptor(interceptionMode, propertyResolver));
        return (MutablePropertySources) proxyFactory.getProxy();
    }

    @SuppressWarnings("unchecked")
    public static <T> PropertySource<T> proxyPropertySource(PropertySource<T> propertySource, EncryptorPropertyResolver resolver) {
        if (CommandLinePropertySource.class.isAssignableFrom(propertySource.getClass())) {
            return instantiatePropertySource(propertySource, resolver);
        }
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(propertySource.getClass());
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addInterface(EncryptorPropertySource.class);
        proxyFactory.setTarget(propertySource);
        proxyFactory.addAdvice(new EncryptorPropertySourceMethodInterceptor<>(propertySource, resolver));
        return (PropertySource<T>) proxyFactory.getProxy();
    }

    @SuppressWarnings("unchecked")
    public static <T> PropertySource<T> instantiatePropertySource(PropertySource<T> propertySource, EncryptorPropertyResolver resolver) {
        PropertySource<T> encryptablePropertySource;
        if (needsProxyAnyway(propertySource)) {
            encryptablePropertySource = proxyPropertySource(propertySource, resolver);
        } else if (propertySource instanceof MapPropertySource) {
            encryptablePropertySource = (PropertySource<T>) new EncryptorMapPropertySourceWrapper((MapPropertySource) propertySource, resolver);
        } else if (propertySource instanceof EnumerablePropertySource) {
            encryptablePropertySource = new EncryptorEnumerablePropertySourceWrapper<>((EnumerablePropertySource) propertySource, resolver);
        } else {
            encryptablePropertySource = new EncryptorPropertySourceWrapper<>(propertySource, resolver);
        }
        return encryptablePropertySource;
    }

    @SuppressWarnings("unchecked")
    private static boolean needsProxyAnyway(PropertySource<?> ps) {
        return needsProxyAnyway((Class<? extends PropertySource<?>>) ps.getClass());
    }

    private static boolean needsProxyAnyway(Class<? extends PropertySource<?>> psClass) {
        return needsProxyAnyway(psClass.getName());
    }

    /**
     * Some Spring Boot code actually casts property sources to this specific type so must be proxied.
     */
    private static boolean needsProxyAnyway(String className) {
        return Stream.of(
                "org.springframework.boot.context.config.ConfigFileApplicationListener$ConfigurationPropertySources",
                "org.springframework.boot.context.properties.source.ConfigurationPropertySourcesPropertySource"
        ).anyMatch(className::equals);
    }
}
