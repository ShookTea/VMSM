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

1. Download source code (`git clone https://github.com/ShookTea/VMSM.git`),
1. Import to IntelliJ IDEA,
1. Solve all dependencies from Maven,
    * `org.json:json:20180130`
    * `org.reactfx:reactfx:2.0-M5`
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

## Managing VMs

### Create new VM
* Open `New VM` dialog via 
    *`Virtual machines/New VM` menu, or
    * `Virtual machines/VM Manager` menu and pressing `New VM` button
* Choose name for your virtual machine. It's highly recommended to use unique names.
* Currently there is only one type of virtual machines: Vagrant.
* Select path to your VM's root directory. Its a directory that contains `.vagrant/machines` directory. If you choose
    wrong path, you'll get an information about that.
* (*not required*) write URL address to HTTP server hosted on your VM. It can be either IP address or domain address (for
    example if you configured your `hosts` file)
* Press `Create` button.

New VM will be selected automatically.

### Checking and changing VM state
There is an Vagrant icon (blue V) right over address field. Ball located next to Vagrant icon can be in one of three
colors, indicating VM state:
* red - VM is not running
* green - VM is on
* yellow - either VM's state is unknown yet or it's changing now.

Right after choosing your VM status icon will be yellow, which means VMSM checks state of VM. It can take up to few seconds.
When status icon is red or green, you can toggle on/off VM by simply pressing status icon (it will be yellow during process
of turning virtual machine on/off) or by using `Vagrant` menu with three options: `Start`, `Restart` and `Stop`.

If your VM is on (green icon) and you've setted up URL address during VM creation, you can press Home button next to
address field to open main page choosen by you.

### Switching between VMs
You can change your current virtual machine by selecting it in `Virtual machines` menu.

### Editing and deleting VMs
In VM Manager screen (`Virtual machines/VM Manager`) you can see more detailed list of virtual machines.
* To edit existing VM, double click entry in table. You cannot edit name and type of your VM.
* To remove existing VM, right-click entry in table and confirm your decision.

Editing and removing VMs are permanent actions.