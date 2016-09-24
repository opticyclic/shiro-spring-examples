package com.github.opticyclic.shiro.msad;

import java.util.Set;

/**
 * Simple class for holding the role names on a principal
 */
public class UserPrincipal {

  private String principalName;
  private Set<String> roleNames;

  public String getPrincipalName() {
    return principalName;
  }

  public void setPrincipalName(String principalName) {
    this.principalName = principalName;
  }

  public Set<String> getRoleNames() {
    return roleNames;
  }

  public void setRoleNames(Set<String> roleNames) {
    this.roleNames = roleNames;
  }
}