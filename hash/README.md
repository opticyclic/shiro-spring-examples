# Shiro Examples - Spring Java Config With Hashes

This example is configured with Spring Java config instead of xml.

It uses a shiro-users.properties file instead of an ini file.

In order to generate a password you need to download the [shiro-tools-hasher-1.3.1-cli.jar file][1] then run:

    java -jar shiro-tools-hasher-1.3.1-cli.jar -p -a SHA-512

In this example, each username has a password of `password`. The passwords are salted by default which is why they look different. 
The salt is hidden/stored as part of the hash in Base64 encoding.

  [1]: https://shiro.apache.org/command-line-hasher.html