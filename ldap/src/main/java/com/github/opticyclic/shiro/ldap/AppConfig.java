package com.github.opticyclic.shiro.ldap;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.ldap.DefaultLdapRealm;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.apache.shiro.realm.ldap.LdapContextFactory;
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
  public DefaultLdapRealm realm(LdapContextFactory contextFactory) {
    DefaultLdapRealm realm = new DefaultLdapRealm();
    realm.setContextFactory(contextFactory);
    realm.setUserDnTemplate(ldapUserDNTemplate);
    return realm;
  }

  @Bean
  public SecurityManager securityManager(Realm realm) {
    return new DefaultSecurityManager(realm);
  }
}