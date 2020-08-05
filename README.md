# File Expander
[![Latest release](https://img.shields.io/jetbrains/plugin/v/11940.svg?colorB=blue&label=File%20Expander&style=popout)](https://plugins.jetbrains.com/plugin/11940-file-expander)
[![Latest release](https://img.shields.io/jetbrains/plugin/d/11940-file-expander.svg?color=brightgreen&label=Downloads&style=popout)](https://plugins.jetbrains.com/plugin/11940-file-expander)

A Intellij plugin that can explore archive-based file in project view

## Building from source
If you would like to build from source, use this following command:

```
$ ./gradlew clean buildPlugin
```

Once it finishes the locally built this IntelliJ Plugin will be located here:

```
build/distributions/fileExpander-<version>.zip
```

You can install this from <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd> point to newly built zip file

###### Note: Require build version from 173+ (2017.3.5).

## Key features

### Browse archive-based file in Intellij project view

##### Supported file type: 
* zip-based type (.zip .jar .war .epc etc)

* .gz

* .tar

* .tar.gz , .tgz

* .7z

* nested archive file.

<img src="https://raw.githubusercontent.com/Camork/file-expander-plugin/master/screenshots/screenshot.png" width="650">

## Contribute

Any issues, feature requirement and contributions are welcome!

## License

File Expander Plugin is available under the GNU General Public License, Version 3.0.
