package com.github.opticyclic.shiro.ldaphash;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ExampleLdapAndHash {

  private static final Logger log = LoggerFactory.getLogger(ExampleLdapAndHash.class);

  public static void main(String[] args) {
    ExampleLdapAndHash ldapAndHash = new ExampleLdapAndHash();
    ldapAndHash.run();
  }

  private void run() {
    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

    // For simplest integration, so that all SecurityUtils.* methods work in all cases,
    // make the securityManager bean a static singleton.
    // DO NOT do this in web applications
    SecurityManager securityManager = context.getBean(SecurityManager.class);
    SecurityUtils.setSecurityManager(securityManager);

    // Get the currently executing user
    Subject currentUser = SecurityUtils.getSubject();

    // Login the current user so we can check against roles and permissions:
    if(!currentUser.isAuthenticated()) {
      loginAndLogout(currentUser, "euler", "password");
      loginAndLogout(currentUser, "jsmith", "password");
      //We expect an error in the logs for this
      loginAndLogout(currentUser, "invaliduser", "password");
    }
  }

  private void loginAndLogout(Subject currentUser, String username, String password) {
    UsernamePasswordToken token = new UsernamePasswordToken(username, password);
    token.setRememberMe(true);
    try {
      currentUser.login(token);
    } catch(UnknownAccountException e) {
      log.error("There is no user with username of " + token.getPrincipal(), e);
      return;
    } catch(IncorrectCredentialsException e) {
      log.error("Password for account " + token.getPrincipal() + " was incorrect!", e);
      return;
    } catch(LockedAccountException e) {
      log.error("The account for username " + token.getPrincipal() + " is locked.", e);
      return;
    } catch(AuthenticationException e) {
      log.error("Unable to authenticate " + token.getPrincipal(), e);
      return;
    }

    //print their identifying principal (in this case, a username):
    log.info("User [" + currentUser.getPrincipal() + "] logged in successfully.");

    //all done - log out!
    currentUser.logout();
  }
}
