package com.transempiric.simpleEncryptor;

/**
 * @author SlowBurner
 */
public class EncryptorException extends RuntimeException {
    public EncryptorException(String msg) {
        super(msg);
    }

    public EncryptorException(Exception innerException) {
        super(innerException.getMessage(), innerException);
    }

    public EncryptorException(String msg, Exception innerException) {
        super(msg, innerException);
    }
}
