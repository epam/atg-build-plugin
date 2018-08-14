## ATG Build Plugin for gradle [![Build Status](https://travis-ci.org/epam/atg-build-plugin.svg?branch=master)](https://travis-ci.org/epam/atg-build-plugin)
This plugin allows to populate gradle dependencies for ATG modules
You could find test project `TProject` in examples directory

#### Key features:
--------------------------------------------------------
* Resolves dependencies by ATG modules.
* Auto discovers ATG modules in gradle project and add them as sub projects.
* Provides gradle task type which can be used to define tasks building ATG EAR artifacts.
* Prints ATG modules dependency tree.
* Supports multiple root atg modules in one gradle project.
* It is not necessary to put the project into ATG installation directory.
* Supports adding dependencies on ATG module in gradle configuration. 

#### Steps to apply plugin in your ATG project:
--------------------------------------------------------
Add build script dependencies to your project
```
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.epam.dep.esp:atg-build-plugin:1.3.1'
    }
}
```
Define ATG folder in root project properties in `build.grale`
```
def atgRoot = '/ATG-ROOT/Location'
ext.atgRoot = atgRoot
```
Apply ATG plugin to all required projects
```
apply plugin: 'atg'
```

#### ATG modules auto-discovering
--------------------------------------------------------
Allow to find atg modules and add it as sub projects automatically.
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
By default plugin uses root project name as single root atg module (placed in ATG dir)
But you can configure multiple ATG root modules in build script through property "atgRootProjects"
It receives comma separated gradle project names which will be used to detect ATG root modules

Example `build.gradle`:
```
ext.atgRootProjects=:ModuleA,:ModuleB
```

In this case plugin will detect two ATG root module: ModuleA and ModuleB.
Dependencies on modules must be defined as `ATG-Required: ModuleA ModuleB` instead of `ATG-Required: TProject.ModuleA TProject.ModuleB`

###### Since v1.2:
`atgRootProjects` can be defined as map in following format:
```
ext.atgRootProjects = :ModuleA => NewNameA, :ModuleB => NameB
```
In this case plugin will use map values as names of root modules.
 
#### Additional dependencies on ATG module 
--------------------------------------------------------
In some cases you may want to add additional dependency on ATG module without changing manifest file.
The plugin makes it possible. Supported all most popular gradle configurations configurations:


Gradle configuration | Equals configuration working with ATG modules
-------------------- | -----------------
api | atgApi
apiElements | atgApiElements
compile | atgCompile
compileClasspath | atgCompileClasspath
compileOnly | atgCompileOnly
implementation | atgImplementation
runtimeClasspath | atgRuntimeClasspath
runtimeElements | atgRuntimeElements
runtimeOnly | atgRuntimeOnly
testCompile | atgTestCompile
testCompileClasspath | atgTestCompileClasspath
testCompileOnly | atgTestCompileOnly
testImplementation | atgTestImplementation
testRuntimeClasspath | atgTestRuntimeClasspath

Examples:
```
dependencies {
    atgCompile('DAF')
}
```

```
dependencies {
    atgTestCompile('DAF')
}
```

Link to gradle configurations documentation:
```
https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_configurations_graph
```