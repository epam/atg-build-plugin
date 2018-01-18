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

import com.epam.atg.gradle.ATGPluginConstants
import com.epam.atg.gradle.utils.FileUtils
import com.epam.atg.gradle.utils.ProjectUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.util.ConfigureUtil

/**
 * Wrapper task for executing runAssembler utility.
 * It iterates over a list of script configurations which define the parameters for each assembly.
 */
class RunAssemblerTask extends DefaultTask {
  private String outputDir = null
  private String earName = null
  private List layers = []
  private String atgRootDir = null
  private boolean cleanOnBuild = true
  private List modules = []
  private OptionsContainer options = new OptionsContainer()

  RunAssemblerTask() {
    description = 'Assembles an Oracle Commerce (ATG) EAR.'
    group = 'atg'
    if(project.hasProperty('atgRoot')) {
      atgRootDir = project.atgRoot
    }
    outputDir = "${project.buildDir.absolutePath}/ears"
  }

  String getOutputDir() {
    return outputDir
  }

  void setOutputDir(String pOutputDir) {
    outputDir = pOutputDir
  }

  List getLayers() {
    return layers
  }

  void setLayers(List pLayers) {
    layers = pLayers
  }

  String getAtgRootDir() {
    return atgRootDir
  }

  void setAtgRootDir(String atgRootDir) {
    this.atgRootDir = atgRootDir
  }

  List getModules() {
    return modules
  }

  void setModules(List pModules) {
    modules = pModules
  }

  OptionsContainer getOptions() {
    return options
  }

  void setOptions(OptionsContainer pOptions) {
    options = pOptions
  }

  void options(Closure closure) {
    ConfigureUtil.configure(closure, options)
  }

  String getEarName() {
    return earName
  }

  void setEarName(String pEarName) {
    earName = pEarName
  }

  boolean getCleanOnBuild() {
    return cleanOnBuild
  }

  void setCleanOnBuild(boolean pCleanOnBuild) {
    cleanOnBuild = pCleanOnBuild
  }

  @TaskAction
  void executeCommandLine() {
    if(!atgRootDir) {
      atgRootDir = project.property(ATGPluginConstants.PROJECT_ATG_ROOT_PROPERTY)
      if(!atgRootDir) {
        throw new IllegalArgumentException('"atgRootDir" is required property.')
      }
    }

    if(modules == null || modules.isEmpty()) {
      throw new IllegalArgumentException('Can\'t assemble empty modules list.')
    }
    if(!earName){
      earName = name
    }

    if(!earName.toLowerCase().endsWith(".ear")) {
      earName += ".ear"
    }

    if(cleanOnBuild) {
      def target = project.file("$outputDir/$earName")
      if (target.exists()) {
        project.delete(target)
      }
    }

    if(!project.file(outputDir).exists()) {
      project.mkdir(outputDir)
    }

    List<File> tempLinks = []
    Map atgRootModules = ProjectUtils.getAtgRootProjectsPathsWithModulesNames(project)
    atgRootModules.each { String projectPath, String moduleName ->
      File atgModuleFile = new File("$atgRootDir/$moduleName")
      if(!atgModuleFile.exists()) {
        Project rootAtgProject = project.project(projectPath)
        FileUtils.createLink(atgModuleFile, rootAtgProject.projectDir)
        tempLinks.add(atgModuleFile)
        project.logger.info("Added temporary link from $rootAtgProject.projectDir to $atgModuleFile")
      }
    }

    try {
      def args = RunAssemblerCommandLineBuilder.build(this)
      RunAssemblerExecutor.exec(project, atgRootDir, args)
    } finally {
      for(File link in tempLinks) {
        link.delete()
        project.logger.info("Removed temporary link $link")
      }
    }
  }
}
