package com.github.opticyclic.shiro.msad;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleActiveDirectoryCustom {

  private static final Logger log = LoggerFactory.getLogger(ExampleActiveDirectoryCustom.class);

  @Parameter(names = "--username", required = true)
  String username;

  @Parameter(names = "--password", required = true)
  String password;

  public static void main(String[] args) {
    ExampleActiveDirectoryCustom main = new ExampleActiveDirectoryCustom();
    new JCommander(main, args);
    main.run();
  }

  private void run() {
    Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
    SecurityManager securityManager = factory.getInstance();
    SecurityUtils.setSecurityManager(securityManager);

    // Get the currently executing user
    Subject currentUser = SecurityUtils.getSubject();

    // Login the current user so we can check against roles and permissions:
    if(!currentUser.isAuthenticated()) {
      UsernamePasswordToken token = new UsernamePasswordToken(username, password);
      token.setRememberMe(true);
      try {
        currentUser.login(token);
      } catch(UnknownAccountException e) {
        log.error("There is no user with username of " + token.getPrincipal(), e);
      } catch(IncorrectCredentialsException e) {
        log.error("Password for account " + token.getPrincipal() + " was incorrect!", e);
      } catch(LockedAccountException e) {
        log.error("The account for username " + token.getPrincipal() + " is locked.", e);
      } catch(AuthenticationException e) {
        log.error("Unknown auth error", e);
      }
    }

    //print their identifying principal (in this case, a username):
    log.info("User [" + currentUser.getPrincipal() + "] logged in successfully.");

    //Test a role - Currently broken unless you add a system user to shiro.ini
    //https://issues.apache.org/jira/browse/SHIRO-586
    if(currentUser.hasRole("sysadmin")) {
      log.info("Found group/role");
    } else {
      log.info("Either not found or not mapped");
    }

    //all done - log out!
    currentUser.logout();
  }

}
