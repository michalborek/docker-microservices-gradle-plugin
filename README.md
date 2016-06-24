# bower-to-prod-gradle-plugin

[![Build Status](https://travis-ci.org/michalborek/bower-to-prod-gradle-plugin.svg?branch=master)](https://travis-ci.org/michalborek/bower-to-prod-gradle-plugin)

**This plugin is in EARLY ALPHA stage. Use at own risk.**

A gradle plugin that copies only production files of bower dependencis into desired directory.
 
This plugin uses *.bowerrc* to determine the *bower_components* directory.

It automatically copies all files defined in *'main'* section in a *bower.json* of each dependency.

When you want to want to customize certain dependency, use *bowerToProd* declaration:

    bowerToProd {
      destination file('dest') // where to put production files, additionally lib name will be appended
      lib name: 'almond', buildDir: 'build' // indicate dhat for almond dep you want to strip 'build' file in destination directory
      lib name: 'almond', buildDir: 'build', includes: ['./build/a.js'] // ignore the 'main' section in bower.json and copy only a.js
      lib name: 'angular', destination: 'someFolder' // copy to 'someFolder' instead of 'dest/libraryName'
      ignore 'angular', 'moment'
    }


To execute task run:

```./gradlew copyBowerProductionDependencies```
