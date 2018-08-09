[← back to index](../index.md)

## Initializing project

1. Clone repository from GitHub:
    ```bash
    git clone https://github.com/ShookTea/VMSM.git
    ```
1. Import project to IDE, i.e. IntelliJ
1. Solve dependencies from Maven:
    * `org.hibernate:hibernate-core:5.3.4.Final`
    * `org.json:json:20180130`
    * `org.reactfx:reactfx:2.0-M5`
    * `com.jcraft:jsch:0.1.54`
    * `mysql:mysql-connector-java:8.0.11`
1. Compile and run

### Run and build configuration

Main class is `eu.shooktea.vmsm.Start`. VMSM doesn't use any command line arguments.

To create a correct .JAR file, artifact configuration should extract all dependencies to destination file. *NOTE*: you
probably don't need to do it if you want to involve in expanding VMSM project. Building .JAR file is done only once,
during release of new version, and there is no need of doing so during development of application, except if your
doing something that can potentially work different way when application is launched from .JAR.