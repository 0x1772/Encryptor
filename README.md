## Encryptor
Spring Framework encryption extension.
SimpleEncryptor supports property encryption via TextEncryptor, with optional use of [spring-security-rsa](https://github.com/dsyer/spring-security-rsa) RSA (PUBLIC and PRIVATE keys).
RSA (PUBLIC and PRIVATE keys) can be deleted or cleared after TextEncryptor instantiation using the SimpleEncryptorFactoryBean.

SimpleEncryptor provides encryption support for property sources in Spring Boot Applications and plain old Spring.<br/>

## How to get use.
1.  Add the encryptor dependency to your project :
	```groovy
	 compile("com.transempiric:encryptor:1.0.0")
	```
	```xml
    <dependency>
      <groupId>com.transempiric</groupId>
      <artifactId>encryptor</artifactId>
      <version>1.0.0</version>
      <scope>compile</scope>
    </dependency>
    ```
2.  Spring Boot property example:
	```java
    @SpringBootApplication
    public class WebTemplateApplication {
        public static void main(String[] args) throws Exception {
    
            TextEncryptor rsaDecryptor = new EncryptorFactoryBean()
                    .rsaDecryptor("classPath:local_enc_private_key.pem")
                    .createInstance();
            
            //TODO: Clean up and make use of the eEncryptorFactoryBean.
            EncryptorPropertyResolver resolver =  new EncryptorPropertyResolver(rsaDecryptor);
            EncryptorEnvironment env =  new EncryptorEnvironment(EncryptorInterceptionMode.WRAPPER, resolver);
            
            new SpringApplicationBuilder()
                    .environment(env)
                    .sources(WebTemplateApplication.class)
                    .run(args);
    
        }
    }
	```
	
    Encryptable properties will be enabled across the entire Spring Environment (This means any system property, environment property, command line argument, application.properties, yaml properties, and any other custom property sources can contain encrypted properties)

2.  Spring Bean example for encryptors:
	```java
    import com.transempiric.Encryptor.EncryptorFactoryBean;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.crypto.encrypt.TextEncryptor;
    
    import static com.transempiric.Encryptor.EncryptorFactoryBean.SIMPLE_ENCRYPTOR_SALT_PROPERTY_NAME;
    import static com.transempiric.Encryptor.EncryptorFactoryBean.SIMPLE_ENCRYPTOR_SECRET_PROPERTY_NAME;
    
    @Configuration
    public class EncryptorConfigExample {
    
        @Bean
        public TextEncryptor rsaEncryptor() throws Exception {
            return new EncryptorFactoryBean()
                    .rsaEncryptor("classPath:local_enc_public_key.pem")
                    // .clearKeyFileContents(false)
                    // .deleteKeyFiles(false)
                    .createInstance();
        }
    
        @Bean
        public TextEncryptor rsaDecryptor() throws Exception {
            return new EncryptorFactoryBean()
                    .rsaDecryptor("classPath:local_enc_private_key.pem")
                    // .clearKeyFileContents(true)
                    // .deleteKeyFiles(true)
                    .createInstance();
        }
    
        // Required (Hex-encoded string): Inject System property -Dsimple.encryptor.secret=497349744150726F626C656D466F72596F75546F41736B
        // Optional (Hex-encoded string): Inject System property -Dsimple.encryptor.salt=456E63727970746F7273
        @Bean
            public TextEncryptor hexEncodingSimpleEncryptor() throws Exception {
                return new EncryptorFactoryBean()
                                .hexEncodingTextEncryptor(
                                        System.getProperty(ENCRYPTOR_SECRET_PROPERTY_NAME),
                                        System.getProperty(ENCRYPTOR_SALT_PROPERTY_NAME)
                                )
                                .createInstance();
            }
    
        @Bean
        public String spaceMonkey(
                TextEncryptor hexEncodingSimpleEncryptor,
                TextEncryptor rsaEncryptor,
                TextEncryptor rsaDecryptor
        ) {
            /*
            
            System.out.println("**************** EncryptorConfigExample Test *************************");
            System.out.println(rsaEncryptor.encrypt("rupertDurden"));
            System.out.println(rsaEncryptor.encrypt("rupertDurden"));
    
            System.out.println(rsaDecryptor.decrypt(rsaEncryptor.encrypt("rupert")));
            System.out.println(rsaDecryptor.decrypt(rsaEncryptor.encrypt("durden")));
    
            System.out.println(hexEncodingEncryptor.encrypt("rupert"));
            System.out.println(hexEncodingEncryptor.encrypt("durden"));
    
            System.out.println(hexEncodingEncryptor.decrypt(hexEncodingEncryptor.encrypt("rupert")));
            System.out.println(hexEncodingEncryptor.decrypt(hexEncodingEncryptor.encrypt("durden")));
    
            System.out.println("**************** EncryptorConfigExample Test *************************");
    
            */
    
            return "SpaceMonkey";
        }
    }
	```
