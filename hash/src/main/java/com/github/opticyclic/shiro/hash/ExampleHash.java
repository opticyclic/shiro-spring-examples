package com.github.opticyclic.shiro.hash;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ExampleHash {

  private static final Logger log = LoggerFactory.getLogger(ExampleHash.class);

  public static void main(String[] args) {
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
      UsernamePasswordToken token = new UsernamePasswordToken("jsmith", "password");
      token.setRememberMe(true);
      try {
        currentUser.login(token);
      } catch(UnknownAccountException e) {
        log.error("There is no user with username of " + token.getPrincipal(), e);
      } catch(IncorrectCredentialsException e) {
        log.error("Password for account " + token.getPrincipal() + " was incorrect!", e);
      } catch(LockedAccountException e) {
        log.error("The account for username " + token.getPrincipal() + " is locked.", e);
      }catch(AuthenticationException e) {
        log.error("Unknown auth error", e);
      }
    }

    //print their identifying principal (in this case, a username):
    log.info("User [" + currentUser.getPrincipal() + "] logged in successfully.");

    //test a role:
    if(currentUser.hasRole("manager")) {
      log.info("Do some real work instead of reading emails all day!");
    } else {
      log.info("Keep kissing your way up!");
    }

    //test a typed permission (not instance-level)
    if(currentUser.isPermitted("application:use:wiki")) {
      log.info("You may edit the wiki.");
    } else {
      log.info("Sorry, read-only.");
    }

    //all done - log out!
    currentUser.logout();
  }

}
