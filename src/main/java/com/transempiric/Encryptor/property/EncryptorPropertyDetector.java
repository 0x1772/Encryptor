package com.transempiric.Encryptor.property;

import org.springframework.util.Assert;

/**
 * Default property detector that detects encrypted property values with the format "$prefix$encrypted_value$suffix"
 * Default values are "ENC(" and ")" respectively.
 */
public class EncryptorPropertyDetector {
    private String prefix = "ENC(";
    private String suffix = ")";

    public EncryptorPropertyDetector() {
    }

    public EncryptorPropertyDetector(String prefix, String suffix) {
        Assert.notNull(prefix, "Prefix can't be null");
        Assert.notNull(suffix, "Suffix can't be null");
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public boolean isEncrypted(String property) {
        if (property == null) {
            return false;
        }
        final String trimmedValue = property.trim();
        return (trimmedValue.startsWith(prefix) &&
                trimmedValue.endsWith(suffix));
    }

    public String unwrapEncryptedValue(String property) {
        return property.substring(
                prefix.length(),
                (property.length() - suffix.length()));
    }
}
