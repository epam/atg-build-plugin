/*
 * Copyright 2017 EPAM SYSTEMS INC
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

import com.epam.atg.gradle.utils.OsUtils

/**
 * Builds command line arguments based on CLI usage
 * runAssembler [option*] output-file-name [-layer config-layer-list] -m dynamo-module-list
 */
class RunAssemblerCommandLineBuilder {

  static final String PACK = '-pack'
  static final String STANDALONE = '-standalone'
  static final String OVERWRITE = '-overwrite'
  static final String COLLAPSE_CLASS_PATH = '-collapse-class-path'
  static final String COLLAPSE_EXCLUDE_DIRS = '-collapse-exclude-dirs'
  static final String COLLAPSE_EXCLUDE_FILES = '-collapse-exclude-files'
  static final String JAR_DIRS = '-jardirs'
  static final String VERBOSE = '-verbose'
  static final String CLASSES_ONLY = '-classesonly'
  static final String DISPLAY_NAME = '-displayname'
  static final String SERVER = '-server'
  static final String LIVE_CONFIG = '-liveconfig'
  static final String DISTRIBUTABLE = '-distributable'
  static final String ADD_EAR_FILE = '-add-ear-file'
  static final String CONTEXT_ROOTS_FILE = '-context-roots-file'
  static final String DYNAMO_ENV_PROPERTIES = '-dynamo-env-properties'
  static final String EXCLUDE_ACC_RESOURCES = '-exclude-acc-resources'
  static final String NO_FIX = '-nofix'
  static final String PREPEND_JARS = '-prependJars'
  static final String RUN_IN_PLACE = '-run-in-place'
  static final String TOMCAT = '-tomcat'
  static final String TOMCAT_ADDITIONAL_RESOURCES_FILE = '-tomcat-additional-resources-file'
  static final String TOMCAT_INITIAL_RESOURCES_FILE = '-tomcat-initial-resources-file'
  static final String TOMCAT_USE_JOTM = '-tomcat-use-jotm'
  static final String TOMCAT_USE_ATOMIKOS = '-tomcat-use-atomikos'
  static final String JBOSS = '-jboss'
  static final String LAYER = '-layer'

  /**
   * Builds a command line list of arguments ready for execution
   * @param project
   * @param assemblyName
   * @return the list of command line arguments
   */
  static ArrayList<String> build(RunAssemblerTask assembler) {

    def arguments = new ArrayList<String>()

    //Add options (optional)
    OptionsContainer options = assembler.options
    if (options.pack) {
      arguments << PACK
    }
    if (options.standalone) {
      arguments << STANDALONE
    }
    if (options.overwrite) {
      arguments << OVERWRITE
    }
    if (options.collapseClassPath) {
      arguments << COLLAPSE_CLASS_PATH
    }
    if (options.collapseExcludeDirs) {
      arguments << COLLAPSE_EXCLUDE_DIRS
    }
    if (options.collapseExcludeFiles) {
      arguments << COLLAPSE_EXCLUDE_FILES
    }
    if (options.jarDirs) {
      arguments << JAR_DIRS
    }
    if (options.verbose) {
      arguments << VERBOSE
    }
    if (options.classesOnly) {
      arguments << CLASSES_ONLY
    }
    if (options.displayName) {
      arguments << DISPLAY_NAME
      arguments << options.displayName
    }
    if (options.server) {
      arguments << SERVER
      def server = options.server
      if(OsUtils.isWindows() && !server.startsWith('"')) {
        server = "\"${server}\""
      }
      arguments << server
    }
    if (options.liveConfig) {
      arguments << LIVE_CONFIG
    }
    if (options.distributable) {
      arguments << DISTRIBUTABLE
    }
    if (options.addEarFile) {
      arguments << ADD_EAR_FILE
      options.addEarFile.each {
        arguments << it
      }
    }
    if (options.contextRootsFile) {
      arguments << CONTEXT_ROOTS_FILE
      arguments << options.contextRootsFile
    }
    if (options.dynamoEnvProperties) {
      arguments << DYNAMO_ENV_PROPERTIES
      arguments << options.dynamoEnvProperties
    }
    if (options.excludeAccResources) {
      arguments << EXCLUDE_ACC_RESOURCES
    }
    if (options.noFix) {
      arguments << NO_FIX
    }
    if (options.prependJars) {
      arguments << PREPEND_JARS
      def joinedValue = options.prependJars.join(',')
      arguments << "\"$joinedValue\""
    }
    if (options.runInPlace) {
      arguments << RUN_IN_PLACE
    }
    if (options.tomcat) {
      arguments << TOMCAT
    }
    if (options.tomcatAdditionalResourcesFile) {
      arguments << TOMCAT_ADDITIONAL_RESOURCES_FILE
      arguments << options.tomcatAdditionalResourcesFile
    }
    if (options.tomcatInitialResourcesFile) {
      arguments << TOMCAT_INITIAL_RESOURCES_FILE
      arguments << options.tomcatInitialResourcesFile
    }
    if (options.tomcatUseJotm) {
      arguments << TOMCAT_USE_JOTM
    }
    if (options.tomcatUseAtomikos) {
      arguments << TOMCAT_USE_ATOMIKOS
    }
    if (options.jboss) {
      arguments << JBOSS
    }

    if (options.extra != null) {
      arguments << options.extra
    }

    //Add output file name (required)
    def targetPath = new File("${assembler.outputDir}/${assembler.earName}").getAbsolutePath()
    if(OsUtils.isWindows() && !targetPath.startsWith('"')) {
      targetPath = "\"${targetPath}\""
    }

    assembler.project.logger.debug('RunAssemblerCommandLineBuilder - targetPath={}', targetPath)

    arguments << targetPath

    //Add layers (optional)
    if (assembler.layers) {
      arguments << LAYER
      arguments.addAll(assembler.layers)
    }

    arguments << '-m'
    arguments.addAll(assembler.modules)
    return arguments
  }

}
