package com.github.opticyclic.shiro.hash;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.crypto.hash.ConfigurableHashService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.PropertiesRealm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class AppConfig {

  @Bean
  public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
    return new LifecycleBeanPostProcessor();
  }

  @Bean
  @DependsOn("lifecycleBeanPostProcessor")
  public PropertiesRealm realm(PasswordMatcher passwordMatcher) {
    PropertiesRealm realm = new PropertiesRealm();
    realm.setCredentialsMatcher(passwordMatcher);
    return realm;
  }

  @Bean
  public SecurityManager securityManager(Realm realm) {
    return new DefaultSecurityManager(realm);
  }

  @Bean
  public ConfigurableHashService hashService() {
    DefaultHashService hashService = new DefaultHashService();
    hashService.setHashIterations(500000);
    hashService.setGeneratePublicSalt(true);
    return hashService;
  }

  @Bean
  public DefaultPasswordService passwordService(ConfigurableHashService hashService) {
    DefaultPasswordService passwordService = new DefaultPasswordService();
    passwordService.setHashService(hashService);
    return passwordService;
  }

  @Bean
  public PasswordMatcher passwordMatcher(DefaultPasswordService passwordService) {
    PasswordMatcher passwordMatcher = new PasswordMatcher();
    passwordMatcher.setPasswordService(passwordService);
    return passwordMatcher;
  }
}