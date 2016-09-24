# Shiro Examples - Custom Microsoft Active Directory

This builds on the previous Active Directory example but adds a Custom Realm.

There is a [bug in Shiro][1] that means you can't search for users without a system user.

This example demonstrates using a Custom Realm to search and store the roles on login, then use them later.

The difference is that the core code explicitly looks for the system user and this new Realm uses the log in credentials to bind to Active Directory to do the search. 

You will need to change the following lines in the shiro.ini to make it work with your setup. 

    adRealm.principalSuffix = @mycompany.com
    adRealm.searchBase = "OU=User Accounts,DC=mycompany,DC=com"
    adRealm.groupRolesMap = "CN=managers,CN=Users,DC=mycompany,DC=com":"sysadmin"


  [1]: https://issues.apache.org/jira/browse/SHIRO-586
  