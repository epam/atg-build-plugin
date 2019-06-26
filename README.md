## ATG Build Plugin for gradle [![Build Status](https://travis-ci.org/epam/atg-build-plugin.svg?branch=master)](https://travis-ci.org/epam/atg-build-plugin)
This plugin allows to populate gradle dependencies for ATG modules\
You could find test projects `TProject` and `TProjectManifestGeneration` in examples directory

Tested with Gradle 4.10.3, Gradle 5.4.1

#### Key features:
--------------------------------------------------------
* Resolves dependencies by ATG modules.
* Auto discovers ATG modules in gradle project and add them as sub projects.
* Provides gradle task type which can be used to define tasks building ATG EAR artifacts.
* Prints ATG modules dependency tree.
* Supports multiple root atg modules in one gradle project.
* It is not necessary to put the project into ATG installation directory.
* Supports adding dependencies on ATG module in gradle configuration.
* Supports fetching all libs dependencies to specific folder.
* Supports ATG manifest file generation based on your build.gradle configuration.

#### Steps to apply plugin in your ATG project:
--------------------------------------------------------
Add build script dependencies to your project
```
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.epam.dep.esp:atg-build-plugin:1.4'
    }
}
```
Define ATG folder in root project properties in `build.gradle`
```
ext.atgRoot = '/ATG-ROOT/Location'
```
Apply ATG plugin to all required projects
```
apply plugin: 'atg'
```

#### ATG modules auto-discovering
--------------------------------------------------------
Allow to find atg modules and add it as sub projects automatically.\
In `settings.gradle`:
```
buildscript {
    dependencies {
        classpath 'com.epam.dep.esp:atg-build-plugin:1.2'
    }
}
//Optional exclude specific modules from search
ext.excludedAtgProjects=':MyNotATGModule1, :MyNotATGModuleN'

apply plugin: 'atg'
```

#### Steps to create ear:
--------------------------------------------------------
Define task:
```
task buildEAR(type: RunAssemblerTask) {
    modules = ['DAS']
    earName = 'DAS'
    options {
        server = 'DASServer'
    }
}
```

Call defined task:
```
gradle buildEAR
```

#### RunAssemblerTask parameters:
--------------------------------------------------------
Property | Type | Default Value | Description
-------- | ---- | ------------- | -----------
 modules | List<String> | - | List of required modules for building
 outputDir | String | projectDir/build/ear | Target directory for artifact
 earName | String | equals to task name | Artifact name
 layers | List<String> | [] | list of ATG named layers
 atgRoot | String | project.atgRoot | Directory with installed ATG platform
 cleanOnBuild | boolean | true | If true - the artifact assembled last time will be removed
 options | Options object | {} | Contains additional parameters

### Options:
Property | Type | Default Value | Matched runAssembler parameter
-------- | ---- | ------------- | ------------------------------
pack | boolean | false | -pack
standalone | boolean | false | -standalone
overwrite | boolean | false | -overwrite
collapseClassPath | boolean| false | -collapse-class-path
collapseExcludeDirs | List<String>  | [] | -collapse-exclude-dirs
collapseExcludeFiles | List<String> | [] | -collapse-exclude-files
jarDirs | boolean | false | -jardirs
verbose | boolean | false | -verbose
classesOnly | boolean | false | -classesonly
displayName | String | '' | -displayname <name>
serverName | String | '' | -server
liveConfig | boolean | false | -liveconfig
distributable | boolean | false | -distributable
addEarFile | List<String> | [] | -add-ear-file <EAR file name>
contextRootsFile | String | '' | -context-roots-file <properties file>
dynamoEnvProperties | String | '' | -dynamo-env-properties <properties file>
excludeAccResources | boolean | false | -exclude-acc-resources
noFix | boolean | false | -nofix
prependJars | List<String> | [] | -prependJars <jar1,jar2,...>
runInPlace | boolean | false | -run-in-place
tomcat | boolean | false | -tomcat
tomcatAdditionalResourcesFile | String | false | -tomcat-additional-resources-file <fileName>
tomcatInitialResourcesFile | String | false | -tomcat-initial-resources-file <fileName>
tomcatUseJotm | boolean | false | -tomcat-use-jotm
tomcatUseAtomikos | boolean | false | -tomcat-use-atomikos
jboss | boolean | false | -jboss
extra | string | '' | this string will be added to end of options list

#### Multiple ATG root modules support
--------------------------------------------------------
By default plugin uses root project name as single root atg module (placed in ATG dir)\
But you can configure multiple ATG root modules in build script through property "atgRootProjects"\
It receives comma separated gradle project names which will be used to detect ATG root modules

Example `build.gradle`:
```
ext.atgRootProjects=:ModuleA,:ModuleB
```

In this case plugin will detect two ATG root module: ModuleA and ModuleB.\
Dependencies on modules must be defined as `ATG-Required: ModuleA ModuleB` instead of `ATG-Required: TProject.ModuleA TProject.ModuleB`

###### Since v1.2:
`atgRootProjects` can be defined as map in following format:
```
ext.atgRootProjects = :ModuleA => NewNameA, :ModuleB => NameB
```
In this case plugin will use map values as names of root modules.

#### Additional dependencies on ATG module
--------------------------------------------------------
###### Since v1.4: added ATG-Required-If-Present support, 'atg' prefix was replaces with 'atgRequired' and 'atgRequiredIf'

Add additional dependency on any ATG module(OOTB or gradle's) to classpath for build process and for manifest generation.


Gradle configuration | Manifest ATG-Required | Manifest ATG-Required-If-Present
-------------------- | ----------------- | -----------------
api | atgRequiredApi | atgRequiredIfApi
apiElements | atgRequiredApiElements | atgRequiredIfApiElements
compile | atgRequiredCompile | atgRequiredIfCompile
compileClasspath | atgRequiredCompileClasspath | atgRequiredIfCompileClasspath
compileOnly | atgRequiredCompileOnly | atgRequiredIfCompileOnly
implementation | atgRequiredImplementation | atgRequiredIfImplementation
runtimeClasspath | atgRequiredRuntimeClasspath | atgRequiredIfRuntimeClasspath
runtimeElements | atgRequiredRuntimeElements | atgRequiredIfRuntimeElements
runtimeOnly | atgRequiredRuntimeOnly | atgRequiredIfRuntimeOnly
testCompile | atgRequiredTestCompile | atgRequiredIfTestCompile
testCompileClasspath | atgRequiredTestCompileClasspath | atgRequiredIfTestCompileClasspath
testCompileOnly | atgRequiredTestCompileOnly | atgRequiredIfTestCompileOnly
testImplementation | atgRequiredTestImplementation | atgRequiredIfTestImplementation
testRuntimeClasspath | atgRequiredTestRuntimeClasspath | atgRequiredIfTestRuntimeClasspath

Example:
```
dependencies {
    atgRequiredCompile('DAF')
    atgRequiredCompile 'DCS'
    atgRequiredCompile 'DCS.Endeca.Base', 'DAF.Endeca.Assembler'
}
```


#### Scan ATG module manifest file
--------------------------------------------------------
Plugin scan manifests files of ATG modules to read information about used libs and other ATG modules.

Use `scanManifest` property to disable\enable manifest scan for gradle ATG modules, by default - enabled (scan always works for OOTB ATG module).

`build.gradle`
```
project.ext.scanManifest=false
```

Notice: don't recommended `scanManifest=true` with using in your project manifest generation task.

#### Dependencies sink task
--------------------------------------------------------

Fetch all `atgClassPath` dependencies jar files to `dependenciesSinkPath` folder.

Example:

```
  atg {
      dependenciesSinkPath 'build/dependencies'
  }
  dependencies {
      atgClassPath 'org.slf4j:slf4j-api:1.7.26'
      atgClassPath 'org.slf4j:slf4j-simple:1.7.26'
  }

gradle dependenciesSink
```


#### Manifest generation task
--------------------------------------------------------

Manifest generation task depends on following confings for your gradle project:\
`atg.dependenciesSinkPath` as String (default `null`, if not `null` - for each jar dependency file used its `dependenciesSink` task fetch path)\
`atg.manifestConfig` as com.epam.atg.gradle.manifest.ManifestConfig

ManifestConfig field | Type | Description
-------------------- | ----------------- | -----------------
manifestVersion | String | as Manifest-Version
antVersion | String | as Ant-Version
atgJ2ee | String | as ATG-J2EE
atgEarModule | String | as ATG-EAR-Module
atgProduct | String | as ATG-Product
createdBy | String | as Created-By
individualResources | Set\<IndividualResource\> | set of IndividualResource: Name, ATG-Client-Update-File, ATG-Assembler-Import-File
atgClientClassPath | Set\<String\> | as ATG-Client-Class-Path
atgClassPath | Set\<String\> | as ATG-Class-Path
atgRequired | Set\<String\> | as ATG-Required
atgRequiredIfPresent | Set\<String\> | as ATG-Required-If-Present
atgConfigPath | String | as ATG-Config-Path
projectClassPath | Set\<String\> | autofilled depends on your sourceSets, used to fill individualResources, atgClientClassPath and atgClassPath
generateAtgClientClassPath | boolean | default false, flag to fill `atgClientClassPath` field based on `projectClassPath` field
generateIndividualResources | boolean | default false, flag to fill `individualResources` field based on `projectClassPath` field
skipGeneration | boolean |  default `true`, override to `false` to enable atg manifest generation task for current gradle-atg module/project
override | boolean | default `false`, allow generation task to regenerate manifest file, if it exists
manifestFilePath | String | default "META-INF/MANIFEST.MF"
others | Map | add custom key -> value to generated manifest file


Example:
`build.gradle`
```groovy
  atg {
      dependenciesSinkPath 'build/dependencies'
      manifestConfig {
          manifestVersion '1.0'
          atgConfigPath 'config'
          generateAtgClientClassPath true
          generateIndividualResources true
          atgProduct 'Module description'
          atgJ2ee 'j2ee-apps/web'
          atgEarModule 'j2ee-apps/eb

          skipGeneration false
          override true
          manifestFilePath 'META-INF/MANIFEST.MF'
      }
  }
  dependencies {
      atgRequiredCompile 'My.Gradle.Atg.Module', 'DCS.Endeca.Base', 'DAF.Endeca.Assembler'
      atgClassPath 'org.slf4j:slf4j-simple:1.7.26' // includes org.slf4j:slf4j-api:1.7.26
  }
gradle generateAtgManifest
```
generated `MANIFEST.MF`
```
Manifest-Version: 1.0
ATG-Config-Path: config
ATG-Required: My.Gradle.Atg.Module DCS.Endeca.Base DAF.Endeca.Assembler
ATG-Class-Path: build/libs/my_module_classes.jar, build/dependencies/slf4j-simple-1.7.26.jar, build/dependencies/slf4j-api-1.7.26.jar
ATG-Client-Class-Path: build/libs/my_module_classes.jar
ATG-Product: my product description
ATG-J2EE: j2ee-apps/web
ATG-EAR-Module: j2ee-apps/web

Name: build/libs/my_module_classes.jar
ATG-Client-Update-File: true
```
> Supports both `project.jar.enabled = false` and `project.jar.enabled = true`

#### Link to gradle configurations documentation:
```
https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_configurations_graph
```