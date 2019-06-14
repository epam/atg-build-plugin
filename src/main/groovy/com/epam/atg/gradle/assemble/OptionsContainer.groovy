/*
 * Copyright 2019 EPAM SYSTEMS INC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

    boolean pack = false

    void pack(boolean pack) {
        this.pack = pack
    }

    boolean standalone = false

    void standalone(boolean standalone) {
        this.standalone = standalone
    }

    boolean overwrite = false

    void overwrite(boolean overwrite) {
        this.overwrite = overwrite
    }

    boolean collapseClassPath = false

    void collapseClassPath(boolean collapseClassPath) {
        this.collapseClassPath = collapseClassPath
    }

    private List<String> collapseExcludeDirs = []

    List<String> getCollapseExcludeDirs() {
        CollectionUtils.stringize(this.collapseExcludeDirs)
    }

    void setCollapseExcludeDirs(String... args) {
        this.collapseExcludeDirs.clear()
        this.collapseExcludeDirs.addAll(args as List)
    }

    void collapseExcludeDirs(String... args) {
        this.collapseExcludeDirs.addAll(args as List)
    }

    private List<String> collapseExcludeFiles = []

    List<String> getCollapseExcludeFiles() {
        CollectionUtils.stringize(this.collapseExcludeFiles)
    }

    void setCollapseExcludeFiles(String... args) {
        this.collapseExcludeFiles.clear()
        this.collapseExcludeFiles.addAll(args as List)
    }

    void collapseExcludeFiles(String... args) {
        this.collapseExcludeFiles.addAll(args as List)
    }

    boolean jarDirs = false

    void jarDirs(boolean jarDirs) {
        this.jarDirs = jarDirs
    }

    boolean verbose = false

    void verbose(boolean verbose) {
        this.verbose = verbose
    }

    boolean classesOnly = false

    void classesOnly(boolean classesOnly) {
        this.classesOnly = classesOnly
    }

    String displayName

    void displayName(String displayName) {
        this.displayName = displayName
    }

    String server

    void server(String server) {
        this.server = server
    }

    boolean liveConfig = false

    void liveConfig(boolean liveConfig) {
        this.liveConfig = liveConfig
    }

    boolean distributable = false

    void distributable(boolean distributable) {
        this.distributable = distributable
    }

    private List<String> addEarFile = []

    List<String> getAddEarFile() {
        CollectionUtils.stringize(this.addEarFile)
    }

    void setAddEarFile(String... args) {
        this.addEarFile.clear()
        this.addEarFile.addAll(args as List)
    }

    void addEarFile(String... args) {
        this.addEarFile.addAll(args as List)
    }

    String contextRootsFile

    void contextRootsFile(String contextRootsFile) {
        this.contextRootsFile = contextRootsFile
    }

    String dynamoEnvProperties

    void dynamoEnvProperties(String dynamoEnvProperties) {
        this.dynamoEnvProperties = dynamoEnvProperties
    }

    boolean excludeAccResources = false

    void excludeAccResources(boolean excludeAccResources) {
        this.excludeAccResources = excludeAccResources
    }

    boolean noFix = false

    void noFix(boolean noFix) {
        this.noFix = noFix
    }

    private List<String> prependJars = []

    List<String> getPrependJars() {
        CollectionUtils.stringize(this.prependJars)
    }

    void setPrependJars(String... args) {
        this.prependJars.clear()
        this.prependJars.addAll(args as List)
    }

    void prependJars(String... args) {
        this.prependJars.addAll(args as List)
    }

    boolean runInPlace = false

    void runInPlace(boolean runInPlace) {
        this.runInPlace = runInPlace
    }

    boolean tomcat = false

    void tomcat(boolean tomcat) {
        this.tomcat = tomcat
    }

    String tomcatAdditionalResourcesFile

    void tomcatAdditionalResourcesFile(String tomcatAdditionalResourcesFile) {
        this.tomcatAdditionalResourcesFile = tomcatAdditionalResourcesFile
    }

    String tomcatInitialResourcesFile

    void tomcatInitialResourcesFile(String tomcatInitialResourcesFile) {
        this.tomcatInitialResourcesFile = tomcatInitialResourcesFile
    }

    boolean tomcatUseJotm = false

    void tomcatUseJotm(boolean tomcatUseJotm) {
        this.tomcatUseJotm = tomcatUseJotm
    }

    boolean tomcatUseAtomikos = false

    void tomcatUseAtomikos(boolean tomcatUseAtomikos) {
        this.tomcatUseAtomikos = tomcatUseAtomikos
    }

    boolean jboss = false

    void jboss(boolean jboss) {
        this.jboss = jboss
    }

    String extra = null

    void extra(String extra) {
        this.extra = extra
    }
}
