# Shiro Examples - LDAP And Hash Fallback

This example is configured with Spring Java config instead of xml.

This builds on the previous two examples where it will initially try to authenticate with LDAP and when it fails it will fallback and authenticate with the hashed passwords.

Due to the nature of Shiro checking all Realms and not exiting as soon as there is a success you get a warning with a stacktrace on LDAP failure even when the hashed password is a success.

Therefore, we raise the level of the the logging to error in that class.

      <logger name="org.apache.shiro.authc.pam.ModularRealmAuthenticator" level="ERROR" additivity="false">
        <appender-ref ref="STDOUT" />
      </logger>
