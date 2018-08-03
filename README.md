# VMSM

[![Newest version](https://img.shields.io/github/tag/ShookTea/VMSM.svg?style=for-the-badge)](https://github.com/ShookTea/VMSM/releases/latest)[![Stable branch: master](https://img.shields.io/badge/stable%20branch-master-brightgreen.svg?longCache=true&style=for-the-badge)](https://github.com/ShookTea/VMSM/tree/master)[![Develop branch: develop](https://img.shields.io/badge/dev%20branch-develop-red.svg?longCache=true&style=for-the-badge)](https://github.com/ShookTea/VMSM/tree/develop)

Virtual Machine Server Manager

**index**
1. [Installation](#installation)
    * [.JAR file](#jar-file)
    * [Download and compile](#download-and-compile)
    * [(Windows) pin VMSM to task bar / start screen](#windows-pin-vmsm-to-task-bar--start-screen)
    
JavaDoc documentation can be found at [shooktea.github.io/VMSM](https://shooktea.github.io/VMSM)

## Installation
**System requirements:**
* Newest version of Java Runtime Environment

You can either download ready-to-use .JAR file or download source code and compile it.

### .JAR file

Download [latest release of VMSM.jar](https://github.com/ShookTea/VMSM/releases/latest)
and run it either by double click (if your OS support it) or by command:

```
java -jar VMSM.jar
```

### Download and compile

1. Download source code (`git clone https://github.com/ShookTea/VMSM.git`),
1. Import to IntelliJ IDEA,
1. Solve all dependencies from Maven:
    * `org.json:json:20180130`
    * `org.reactfx:reactfx:2.0-M5`
    * `com.jcraft:jsch:0.1.54`
    * `mysql:mysql-connector-java:8.0.11`
1. Compile and run.

### (Windows) pin VMSM to task bar / start screen

*Note: tested on Windows 10 only*
1. [Download VMSM.jar](https://github.com/ShookTea/VMSM/releases/latest),
1. Create new shortcut to `.jar` file. It can be located anywhere for now,
1. Rename shortcut to "VMSM",
1. Right-click shortcut and select `Properties`,
1. Edit `target` field by adding `explorer ` (with space) before existing target, i.e. `explorer C:\Users\Norbert\IdeaProjects\VMSM\out\artifacts\VMSM_jar\VMSM.jar`,
1. (*not required*) Change shortcut icon if you want,
1. Close Properties dialog,
1. Right-click shortcut and select `Pin to task bar` and/or `Pin to start screen`,
1. Delete shortcut.