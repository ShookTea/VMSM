# VMSM
[![Semver](http://img.shields.io/VMSM/1.0.png)](http://semver.org/VMSM/v1.0.html)

Virtual Machine Server Manager

## Installation
**System requirements:**
* Newest version of Java Runtime Environment

You can either download ready-to-use .JAR file or download source code and compile it.

### .JAR file

Download [latest release of VMSM.jar](https://github.com/ShookTea/VMSM/releases/latest) and run it either by
double click (if your OS support it) or by command:

```
java -jar VMSM.jar
```

### Download and compile

1. Download source code (`git clone https://github.com/ShookTea/VMSM.git`)
1. Import to IntelliJ IDEA
1. Solve all dependencies from Maven
    * `org.json:json:20180130`
    * `org.reactfx:reactfx:2.0-M5`
1. Compile and run 

## Adding VMs