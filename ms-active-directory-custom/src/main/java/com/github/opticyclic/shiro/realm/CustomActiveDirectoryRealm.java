package com.github.opticyclic.shiro.realm;

import java.util.*;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;

import com.github.opticyclic.shiro.msad.UserPrincipal;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.activedirectory.ActiveDirectoryRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomActiveDirectoryRealm extends ActiveDirectoryRealm {

  private static final Logger log = LoggerFactory.getLogger(CustomActiveDirectoryRealm.class);

  /**
   * This is called during the log in process.
   * Authenticate but also store the roles/groups on a custom principal
   */
  @Override
  protected AuthenticationInfo queryForAuthenticationInfo(AuthenticationToken token, LdapContextFactory ldapContextFactory) throws NamingException {
    SimpleAuthenticationInfo authenticationInfo = (SimpleAuthenticationInfo)super.queryForAuthenticationInfo(token, ldapContextFactory);
    PrincipalCollection principals = authenticationInfo.getPrincipals();

    UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)token;
    String username = usernamePasswordToken.getUsername();
    String userPrincipalName = getUserPrincipalName(token);

    Set<String> roleNames;
    // Binds using the username and password provided by the user.
    LdapContext ldapContext = null;
    try {
      ldapContext = ldapContextFactory.getLdapContext(userPrincipalName, (usernamePasswordToken.getPassword()));
      roleNames = getRoleNamesForUser(username, ldapContext);
    } finally {
      LdapUtils.closeContext(ldapContext);
    }

    List<UserPrincipal> customPrincipals = getCustomPrincipals(userPrincipalName, roleNames);

    //Merge the custom principals and the main principals
    SimplePrincipalCollection principalCollection = new SimplePrincipalCollection(customPrincipals, CustomActiveDirectoryRealm.class.getSimpleName());
    principalCollection.addAll(principals);

    authenticationInfo.setPrincipals(principalCollection);

    return authenticationInfo;
  }

  private List<UserPrincipal> getCustomPrincipals(String userPrincipalName, Set<String> roleNames) {
    UserPrincipal userPrincipal = new UserPrincipal();
    userPrincipal.setPrincipalName(userPrincipalName);
    userPrincipal.setRoleNames(roleNames);

    List<UserPrincipal> customPrincipals = new ArrayList<>();
    customPrincipals.add(userPrincipal);
    return customPrincipals;
  }

  /**
   * This is called during checks for hasRole.
   * Use the roles that we found on login
   */
  @Override
  protected AuthorizationInfo queryForAuthorizationInfo(PrincipalCollection principals, LdapContextFactory ldapContextFactory) throws NamingException {
    UserPrincipal availablePrincipal = (UserPrincipal)getAvailablePrincipal(principals);
    Set<String> roleNames = availablePrincipal.getRoleNames();

    return buildAuthorizationInfo(roleNames);
  }

  /**
   * Copied from the super class until the method is made protected
   * https://github.com/apache/shiro/pull/38
   */
  private Set<String> getRoleNamesForUser(String username, LdapContext ldapContext) throws NamingException {
    Set<String> roleNames;
    roleNames = new LinkedHashSet<String>();

    SearchControls searchCtls = new SearchControls();
    searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    String userPrincipalName = username;
    if(principalSuffix != null) {
      userPrincipalName += principalSuffix;
    }

    //SHIRO-115 - prevent potential code injection:
    String searchFilter = "(&(objectClass=*)(userPrincipalName={0}))";
    Object[] searchArguments = new Object[]{userPrincipalName};

    NamingEnumeration answer = ldapContext.search(searchBase, searchFilter, searchArguments, searchCtls);

    while(answer.hasMoreElements()) {
      SearchResult sr = (SearchResult)answer.next();

      if(log.isDebugEnabled()) {
        log.debug("Retrieving group names for user [" + sr.getName() + "]");
      }

      Attributes attrs = sr.getAttributes();

      if(attrs != null) {
        NamingEnumeration ae = attrs.getAll();
        while(ae.hasMore()) {
          Attribute attr = (Attribute)ae.next();

          if(attr.getID().equals("memberOf")) {

            Collection<String> groupNames = LdapUtils.getAllAttributeValues(attr);

            if(log.isDebugEnabled()) {
              log.debug("Groups found for user [" + username + "]: " + groupNames);
            }

            Collection<String> rolesForGroups = getRoleNamesForGroups(groupNames);
            roleNames.addAll(rolesForGroups);
          }
        }
      }
    }
    return roleNames;
  }

  /**
   * Helper method to convert from AuthenticationToken to a username + suffix if exists
   */
  private String getUserPrincipalName(AuthenticationToken token) {
    String userPrincipalName = String.valueOf(token.getPrincipal());
    if(principalSuffix != null) {
      userPrincipalName += principalSuffix;
    }
    return userPrincipalName;
  }

}
