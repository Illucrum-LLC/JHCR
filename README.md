# JHCR
Java Hot Code Reloader provides a custom agent that allows to reload your Java code during runtime.

## Usage
Download the latest jar from the release section. When starting your Java application, add -javaagent flag. For example:
```
-javaagent:path-to-jhcr-jar/jhcr-x.x.x.jar=argument-string
```

### Argument string
The argument string should consist of semicolon separated key and values. For example:
```
-javaagent:path-to-jhcr-jar/jhcr-x.x.x.jar=key1=value1;key2=value2
```
Supported argument keys:
| Key | Description | Note | Default value |
|-|-|-|-|
| jhcr.projectDirectory | Allows to specify the directory to be watch for modified files | Optional but recommended | If not set, calls ```System.getProperty("user.dir")``` |
| jhcr.watcher.interval | Allows you to specify the interval for ```org.apache.commons.io.monitor.FileAlterationMonitor```. Basically, how often to look for file changes in milliseconds. | Optional | 1000
| jhcr.logger.fileName | Allows you to specify the name or the full path to the file where logs related to JHCR will be printed | Optional | JHCRLogger.log |
| jhcr.logger.prefix | Allows you to specify the prefix used by the logger. | Optional | JHCR |
| jhcr.logger.dateFormat | Allows you to specify the string format used by the logger. This value will be passed as is to the SimpleDateFormat class constructor. | Optional | dd/MM/yyyy HH:mm:ss.SSS |
| jhcr.logger.template | Allows you to specify a template used by the logger. | Optional | [%s] %s: %s: %s\n -> (Date, prefix, level, message) |
| jhcr.logger.level | Allows to specify the what level of logs you want printed. Can be set to: config, fine, finer, finest, info, severe or warning.  | Optional | all |