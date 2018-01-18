/**
 * Copyright 2012 Spindrift
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.epam.atg.gradle.assemble

import groovy.transform.AutoClone
import org.gradle.util.CollectionUtils

/**
 * A container for optional parameters
 */
@AutoClone
class OptionsContainer {

  private boolean pack = false

  void pack(boolean pack) {
    this.pack = pack
  }
  private boolean standalone = false

  void standalone(boolean standalone) {
    this.standalone = standalone
  }
  private boolean overwrite = false

  void overwrite(boolean overwrite) {
    this.overwrite = overwrite
  }
  private boolean collapseClassPath = false

  void collapseClassPath(boolean collapseClassPath) {
    this.collapseClassPath = collapseClassPath
  }
  private List<String> collapseExcludeDirs = []

  List<String> getCollapseExcludeDirs() {
    CollectionUtils.stringize(this.collapseExcludeDirs)
  }

  void setCollapseExcludeDirs(Object... args) {
    this.collapseExcludeDirs.clear()
    this.collapseExcludeDirs.addAll(args as List)
  }

  void collapseExcludeDirs(Object... args) {
    this.collapseExcludeDirs.addAll(args as List)
  }
  private List<String> collapseExcludeFiles = []

  List<String> getCollapseExcludeFiles() {
    CollectionUtils.stringize(this.collapseExcludeFiles)
  }

  void setCollapseExcludeFiles(Object... args) {
    this.collapseExcludeFiles.clear()
    this.collapseExcludeFiles.addAll(args as List)
  }

  void collapseExcludeFiles(Object... args) {
    this.collapseExcludeFiles.addAll(args as List)
  }
  private boolean jarDirs = false

  void jarDirs(boolean jarDirs) {
    this.jarDirs = jarDirs
  }
  private boolean verbose = false

  void verbose(boolean verbose) {
    this.verbose = verbose
  }
  private boolean classesOnly = false

  void classesOnly(boolean classesOnly) {
    this.classesOnly = classesOnly
  }
  private String displayName

  void displayName(String displayName) {
    this.displayName = displayName
  }
  private String server

  void server(String server) {
    this.server = server
  }
  private boolean liveConfig = false

  void liveConfig(boolean liveConfig) {
    this.liveConfig = liveConfig
  }
  private boolean distributable = false

  void distributable(boolean distributable) {
    this.distributable = distributable
  }
  private List<String> addEarFile = []

  List<String> getAddEarFile() {
    CollectionUtils.stringize(this.addEarFile)
  }

  void setAddEarFile(Object... args) {
    this.addEarFile.clear()
    this.addEarFile.addAll(args as List)
  }

  void addEarFile(Object... args) {
    this.addEarFile.addAll(args as List)
  }
  private String contextRootsFile

  void contextRootsFile(String contextRootsFile) {
    this.contextRootsFile = contextRootsFile
  }
  private String dynamoEnvProperties

  void dynamoEnvProperties(String dynamoEnvProperties) {
    this.dynamoEnvProperties = dynamoEnvProperties
  }
  private boolean excludeAccResources = false

  void excludeAccResources(boolean excludeAccResources) {
    this.excludeAccResources = excludeAccResources
  }
  private boolean noFix = false

  void noFix(boolean noFix) {
    this.noFix = noFix
  }
  private List<String> prependJars = []

  List<String> getPrependJars() {
    CollectionUtils.stringize(this.prependJars)
  }

  void setPrependJars(Object... args) {
    this.prependJars.clear()
    this.prependJars.addAll(args as List)
  }

  void prependJars(Object... args) {
    this.prependJars.addAll(args as List)
  }
  private boolean runInPlace = false

  void runInPlace(boolean runInPlace) {
    this.runInPlace = runInPlace
  }
  private boolean tomcat = false

  void tomcat(boolean tomcat) {
    this.tomcat = tomcat
  }
  private String tomcatAdditionalResourcesFile

  void tomcatAdditionalResourcesFile(String tomcatAdditionalResourcesFile) {
    this.tomcatAdditionalResourcesFile = tomcatAdditionalResourcesFile
  }
  private String tomcatInitialResourcesFile

  void tomcatInitialResourcesFile(String tomcatInitialResourcesFile) {
    this.tomcatInitialResourcesFile = tomcatInitialResourcesFile
  }
  private boolean tomcatUseJotm = false

  void tomcatUseJotm(boolean tomcatUseJotm) {
    this.tomcatUseJotm = tomcatUseJotm
  }
  private boolean tomcatUseAtomikos = false

  void tomcatUseAtomikos(boolean tomcatUseAtomikos) {
    this.tomcatUseAtomikos = tomcatUseAtomikos
  }
  private boolean jboss = false

  void jboss(boolean jboss) {
    this.jboss = jboss
  }

  private String extra = null

  void extra(String extra) {
    this.extra = extra
  }
}



