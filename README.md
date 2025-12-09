# JHCR
Java Hot Code Reloader provides a custom agent that allows to reload your Java code during runtime.
Link for the documentation of the project: [jhcr.illucrum.com](https://jhcr.illucrum.com/)

## Usage
Download the latest jar from the release section. When starting your Java application, use our custom class loader and java agent. For example:
```
-Djava.system.class.loader=com.illucrum.tools.jhcr.loader.JHCRClassLoader -javaagent:path-to-jhcr-jar/jhcr-x.x.x.jar=argument-string
```

### Argument string
The argument string should consist of semicolon separated key and values. For example:
```
-javaagent:path-to-jhcr-jar/jhcr-x.x.x.jar=key1=value1;key2=value2
```
Supported argument keys:
| Key | Description | Note | Version | Default value |
|-|-|-|-|-|
| jhcr.projectDirectory | Allows to specify the directory to be watch for modified files | Optional but recommended | 1.0.0+ | If not set, calls ```System.getProperty("user.dir")``` |
| jhcr.watcher.interval | Allows you to specify the interval for ```org.apache.commons.io.monitor.FileAlterationMonitor```. Basically, how often to look for file changes in milliseconds. | Optional | 1.0.0+ | 1000
| jhcr.logger.fileName | Allows you to specify the name or the full path to the file where logs related to JHCR will be printed | Optional | 1.0.0+ | JHCRLogger.log |
| jhcr.logger.prefix | Allows you to specify the prefix used by the logger. | Optional | 1.0.0+ | \$JHCR\$ |
| jhcr.logger.dateFormat | Allows you to specify the string format used by the logger. This value will be passed as is to the SimpleDateFormat class constructor. | Optional | 1.0.0+ | dd/MM/yyyy HH:mm:ss.SSS |
| jhcr.logger.template | Allows you to specify a template used by the logger. | Optional | 1.0.0+ | [%s] %s: %s: %s\n -> (Date, prefix, level, message) |
| jhcr.logger.level | Allows to specify the what level of logs you want printed. Can be set to: config, fine, finer, finest, info, severe or warning.  | Optional | 1.0.0+ | all |
| jhcr.custom.loader | Allows to specify a custom class loader to be used  | Optional | 2.1.0+ | empty |

## Features
### 2.1.0
 - Added spport for custom class loaders

### 2.0.0
 - Watches a repository for changes in ```.class``` files.
 - If no changes in the class structure, class is redefined.
 - Redefined classes change their behavior immediately.
 - If there are changes to the class structure, class is overriden.
 - For overriden classes, changes apply only to new class instances.

### 1.0.0
 - Watches a repository for changes in ```.class``` files.
 - If no changes in the class structure, class is redefined.
 - Redefined classes change their behavior immediately.
