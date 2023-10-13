package com.vialink.atlantis.authorizationserver.configuration;

import com.vialink.atlantis.authorizationserver.apikey.ApiKey;
import com.vialink.atlantis.authorizationserver.apikey.ApiKeyRepository;
import com.vialink.atlantis.authorizationserver.user.User;
import com.vialink.atlantis.authorizationserver.user.UserRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = {User.class, ApiKey.class})
@EnableJpaRepositories(basePackageClasses = {UserRepository.class, ApiKeyRepository.class})
public class JPAConfiguration {
}
