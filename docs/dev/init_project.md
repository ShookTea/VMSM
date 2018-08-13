[← back to index](../index.md)

## Initializing project

1. Clone repository from GitHub:
    ```bash
    git clone https://github.com/ShookTea/VMSM.git
    ```
1. Import project to IDE, i.e. IntelliJ
1. Solve dependencies from Maven:
    * `org.json:json:20180130` - parsing and saving JSON configuration files
    * `org.reactfx:reactfx:2.0-M5` - small improvements for working with JavaFX bindings
    * `com.jcraft:jsch:0.1.54` - support for SSH protocol
    * `mysql:mysql-connector-java:8.0.12` - MySQL connection
    * `com.amihaiemil.web:camel:1.0.2` - parsing and saving YAML configuration files
1. Download newest release of [SqlFormatter.jar](https://github.com/ShookTea/SqlFormatter/releases) and add it as external library
1. Compile and run

### Run and build configuration

Main class is `eu.shooktea.vmsm.Start`. VMSM doesn't use any command line arguments.

To create a correct .JAR file, artifact configuration should extract all dependencies to destination file. *NOTE*: you
probably don't need to do it if you want to involve in expanding VMSM project. Building .JAR file is done only once,
during release of new version, and there is no need of doing so during development of application, except if your
doing something that can potentially work different way when application is launched from .JAR.

### About SqlFormatter (LGPL licence problems)

When SQL formatting was introduced, it firstly used whole Hibernate Core module, which uses LGPL licence. Formatting
requires only one class from that module. It was decided that we will remove most of the code from Hibernate and
leave only that one specific class. Three problems arrived:
* Modifying software licensed under LGPL requires to publish it under LGPL. That's why that one class, after extraction
    and small changes, was published under LGPL v.2.1 in [its very own repository](https://github.com/ShookTea/SqlFormatter).
* Software that uses library licensed under LGPL are required to give a possibility of replacing that library with its
    newer version. While using newest version of VMSM automatically implies using newest version of SqlFormatter (as
    they're actually being developed together), you can still replace that library by opening `VMSM.jar` in any application
    supporting `.zip` files and replace content of `eu/shooktea/sqlformatter` directory with your own. Be aware that there
    is no guarantee that VMSM will still work after replacing library, but that's not required from LGPL licence - you're
    doing that on your own responsibility.
* Software that uses library licensed under LGPL are required to give a possibility of reverse engineering. VMSM is
    licensed under MIT and is published on [its own repository](https://github.com/ShookTea/VMSM), thus effectively
    satisfying that requirement. 
