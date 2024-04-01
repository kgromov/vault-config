package org.kgromov;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

@Slf4j
@EnableConfigurationProperties({VaultConfigApplication.Credentials.class, VaultConfigApplication.CreditCardPrivateInfo.class})
@SpringBootApplication
public class VaultConfigApplication {
    @Value("${id}")
    private String id;
    @Value("${name}")
    private String name;

    public static void main(String[] args) {
        SpringApplication.run(VaultConfigApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(VaultTemplate vaultTemplate,
                                        Credentials credentials,
                                        CreditCardPrivateInfo privateInfo) {
        return args -> {
            log.info("###################### Loaded via VaultTemplate ######################");
            VaultResponse response = vaultTemplate.opsForKeyValue("admin/kv", KeyValueBackend.KV_2).get("default");
            log.info("{}", response.getData());
            log.info("{}", response.getData().get("id"));
            log.info("{}", response.getData().get("name"));
            log.info("###################### Loaded via config import ######################");
            log.info("Data: id={}, name={}", id, name);
            log.info("###################### Loaded via config import grouped keys ######################");
            log.info("Credentials = {}", credentials);
            log.info("###################### Loaded via config import grouped keys ######################");
            log.info("CreditCardPrivateInfo = {}", privateInfo);
        };
    }

    @ConfigurationProperties(prefix = "credentials")
    record Credentials(String login, String password) {}

    @ConfigurationProperties("credit.card")
    record CreditCardPrivateInfo(String cvv, String owner, Secret secret) {}

    record Secret(String phrase, String answer) {}
}
