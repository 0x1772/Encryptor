package com.transempiric.Encryptor.property;

import com.transempiric.Encryptor.EncryptorException;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 */
public class EncryptorPropertyResolver {
    private TextEncryptor encryptor;
    private EncryptorPropertyDetector detector;

    public EncryptorPropertyResolver(TextEncryptor encryptor) {
        this.encryptor = encryptor;
        this.detector = new EncryptorPropertyDetector();
    }

    public String resolvePropertyValue(String value) {
        String actualValue = value;
        if (detector.isEncrypted(value)) {
            try {
                actualValue = encryptor.decrypt(detector.unwrapEncryptedValue(value.trim()));
            } catch (Exception e) {
                throw new EncryptorException("Decryption of Properties failed.", e);
            }
        }
        return actualValue;
    }
} 
