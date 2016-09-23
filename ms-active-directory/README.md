# Shiro Examples - Microsoft Active Directory

This example is slightly different to the others.

It uses the ini format so you can compare and contrast the different methods.

You have to change the following two settings in the ini file to match your company in order to log in.

    adRealm.searchBase="CN=Users,DC=example,DC=com"
    adRealm.principalSuffix=@example.com
    
Logging is set to DEBUG for the shiro packages to help diagnose connection issues you might have.

See [this page][1] for help about bind errors and LDAP codes.

It is designed to connect to Active Directory, however, there are no free online test servers so it is configured with localhost.
Most likely this will fail if you do nothing.

Either change the ini file to a valid server or use putty to forward port 3389 to your company server. e.g.

## Putty

Add the IP/host name on the main session tab then add the tunnels.

### SSH - Tunnels

* Source port = 3389
* Destination = my.ad.hostname.com:389
* Add

Note: ad.hostname.com is unlikely to be the same as the host you are connecting to from the main session tab.

  [1]: https://www-01.ibm.com/support/docview.wss?uid=swg21290631
