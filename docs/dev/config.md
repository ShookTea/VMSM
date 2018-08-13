[← back to index](../index.md)

## Configuration file
VMSM is currently using JSON to store it's configuration, but it is planned to [migrate to YAML](#migration-to-yaml).

### JSON format
Configuration is stored in `~/.vmsm/config.json`, where `~` marks user home directory. VMSM correctly reads configuration
file with whitespaces, i.e.:
```json
{
  "foo": "bar",
  "my table": [
    "a",
    5,
    true
  ]
}
```

But always saves files to smallest file possible: without new lines, tabulations and spaces (except if they're part of)
config values themselves):
```json
{"foo":"bar","my table":["a",5,true]}
```

In objects (elements starting with `{` and ending with `}`, including root object) order of elements is ignored.

For easier reading, all configuration samples below will use whitespaces.

#### Root
```json
{
  "config": {},
  "ignored_vagrant_machines": [],
  "VMs": [],
  "current_vm": "CURRENT VM"
}
```

* `config` is an JSON object that can contain settings of VMSM. There is currently only one value that can be stored here:
    `screen`, which contains ID of screen where VMSM app should be displayed. Example:
    ```json
    {
      "config": {
        "screen": "1"
      }
    }
    ```
* `ignored_vagrant_machines` is array used by Vagrant to store paths to correct Vagrant root directories that Vagrant
    has found automatically, but user decided to ignore them. Example:
    ```json
    {
      "ignored_vagrant_machines": [
        "/home/user/vagrant/mymachine"
      ]
    }
    ```
* `VMs` contains array of [VM JSON objects](#vm-json-object) representing VMs added to VMSM.
* `current_vm` is a string containing name of one of the VMs that is currently used (if VMSM is on) or was used in the
    moment VMSM has been stopped. During loading of VMSM configuration, if VM with name saved in `current_vm` still exists,
    that VM will be automatically selected as current VM.

#### VM JSON object

Sample JSON object that will be stored in `VMs` array:
```json
{
  "path": "/home/user/mymachine",
  "name": "My Machine",
  "type": "Vagrant",
  "url": "http://mymachine.local",
  "modules": {}
}
```
* `path` is a root file/directory of choosen VM,
* `name` is a displayed name of virtual machine. It's also stored in `ROOT/current_vm` if selected VM is currently used,
* `type` is a virtual machine type name, i.e. `"Vagrant"` or `"Docker Compose"`,
* `url` is a root URL address that will be used to open main page,
* `modules` is a JSON object that contains pairs `"module_name": {}` for each installed module. Inside every module
    object it's configuration is stored. Content of that objects depends from module to module.

### Migration to YAML

There are two primary reasons why there is a plan to replace JSON with YAML in configuration storage:
* **One dependence less**. `org.json:json` library is used only for reading configuration files; at the same time
    `com.amihaiemil.web:camel` is used to read YAML files for Docker Compose. We obviously cannot change Docker Compose
    config format, but we can change ours. After migration we will be able to simply remove JSON dependency.
* **More human-readable format**. It forces to use whitespaces and reduce characters representing objects and tables.

#### How will migration work
(if .json exists, read from it; saving always to YAML and removing .json files)