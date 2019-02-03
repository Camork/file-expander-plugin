# File Expander
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

## Key features

### Browse archive-based file in Intellij project view

Supported file type: zip-based type (zip , jar , war , etc), tar , gz , tar.gz

<img src="https://raw.githubusercontent.com/Camork/file-expander-plugin/master/screenshots/screenshot1.jpg">

## Known issue:

* Unable to expand nested archive file.

## Contribute

Any issues, feature requirement and contributions are welcome

## License

File Expander Plugin is available under the GNU General Public License, Version 3.0.