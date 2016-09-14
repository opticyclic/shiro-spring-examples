# Shiro Examples - Spring Java Config With LDAP

This example is configured with Spring Java config instead of xml.

It connects to a [free online LDAP service][1] to demonstrate the functionality. See below for a copy of the notes from their site.

[LDAP Admin][2] is a nice GUI for browsing the LDAP server. Connect in the following way:

* New
* Type a connection name e.g. forumsys
* Host = `ldap.forumsys.com`
* Port = `389` (default anyway)
* Leave Anonymous connection ticked
* Click Fetch DNs
  * This should populate with `dc=example,dc=com`
* Click Test Connection
* Click OK
* Browse around (not much there compared to a proper AD forest)

## LDAP Server Information (read-only access):

    Server: ldap.forumsys.com  
    Port: 389
    
    Bind DN: cn=read-only-admin,dc=example,dc=com
    Bind Password: password

All user passwords are `password`.

You may also bind to individual Users (uid) or the two Groups (ou) that include:

    ou=mathematicians,dc=example,dc=com

* riemann
* gauss
* euler
* euclid


    ou=scientists,dc=example,dc=com

* einstein
* newton
* galieleo
* tesla

  [1]: http://www.forumsys.com/en/tutorials/integration-how-to/ldap/online-ldap-test-server/
  [2]: http://www.ldapadmin.org/index.html
