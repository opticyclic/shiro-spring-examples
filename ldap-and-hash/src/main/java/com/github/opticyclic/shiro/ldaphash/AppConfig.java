package com.github.opticyclic.shiro.ldaphash;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.crypto.hash.ConfigurableHashService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.ldap.DefaultLdapRealm;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.realm.text.PropertiesRealm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@PropertySource("classpath:application.properties")
@Configuration
public class AppConfig {

  @Value("${ldap.url}")
  private String ldapUrl;

  @Value("${ldap.userDNTemplate}")
  private String ldapUserDNTemplate;

  @Value("${ldap.systemUsername}")
  private String systemUsername;

  @Value("${ldap.systemPassword}")
  private String systemPassword;

  @Bean
  public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
    return new LifecycleBeanPostProcessor();
  }

  @Bean
  public LdapContextFactory contextFactory() {
    JndiLdapContextFactory contextFactory = new JndiLdapContextFactory();
    contextFactory.setUrl(ldapUrl);
    //This works on forumsys without a username/password too
    contextFactory.setSystemUsername(systemUsername);
    contextFactory.setSystemPassword(systemPassword);
    return contextFactory;
  }

  @Bean
  @DependsOn("lifecycleBeanPostProcessor")
  public DefaultLdapRealm ldapRealm(LdapContextFactory contextFactory) {
    DefaultLdapRealm realm = new DefaultLdapRealm();
    realm.setContextFactory(contextFactory);
    realm.setUserDnTemplate(ldapUserDNTemplate);
    return realm;
  }

  @Bean
  @DependsOn("lifecycleBeanPostProcessor")
  public PropertiesRealm propertiesRealm(PasswordMatcher passwordMatcher) {
    PropertiesRealm realm = new PropertiesRealm();
    realm.setCredentialsMatcher(passwordMatcher);
    return realm;
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

  @Bean
  public SecurityManager securityManager(DefaultLdapRealm defaultLdapRealm, PropertiesRealm propertiesRealm) {
    //Order is important here so be explicit rather than depending on Spring getting the List right and passing in List<Realm>
    List<Realm> realms = new ArrayList<>();
    realms.add(defaultLdapRealm);
    realms.add(propertiesRealm);
    return new DefaultSecurityManager(realms);
  }
}