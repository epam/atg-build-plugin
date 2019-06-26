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

package com.epam.atg.gradle.build.utils

import com.epam.atg.gradle.ATGPluginConstants
import com.epam.atg.gradle.build.ATGGradleProject
import com.epam.atg.gradle.build.module.ATGModule
import com.epam.atg.gradle.build.module.ATGProjectModule
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ATGDependenciesResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ATGDependenciesResolver.class)

    void defineDependencies(ATGGradleProject atgGradleProject) {
        addProjectClassPathDependencies(atgGradleProject)
        addProjectRequiredModulesDependencies(atgGradleProject)
    }

    private static void addProjectClassPathDependencies(ATGGradleProject atgGradleProject) {
        List<File> filesToExclude = new ArrayList<>()
        filesToExclude.add(atgGradleProject.projectJarArchive)
        filesToExclude.add(atgGradleProject.projectResources)
        filesToExclude.addAll(atgGradleProject.projectSourceSetsOutputFiles)
        ConfigurableFileCollection toExcludeFileCollection = atgGradleProject.project.files(filesToExclude.toArray())
        addModuleClassPathDependencies(atgGradleProject.project, atgGradleProject.atgProjectModule, toExcludeFileCollection)
    }

    private void addProjectRequiredModulesDependencies(ATGGradleProject atgGradleProject) {
        atgGradleProject.clearModuleDependencyMarkers()
        List<String> requiredModules = new ArrayList<>(atgGradleProject.atgProjectModule.requiredModules)
        requiredModules.addAll(atgGradleProject.atgProjectModule.requiredIfModules)
        LOGGER.info('{} has the following required modules: {}', atgGradleProject, requiredModules)
        addDependencies(atgGradleProject, requiredModules)
    }

    private void addDependencies(ATGGradleProject atgGradleProject, List<String> modules) {
        for (String module : modules) {
            LOGGER.debug('Required module {} for {}', module, atgGradleProject)
            addModuleDependency(atgGradleProject, module)
        }
    }

    private void addModuleDependency(ATGGradleProject atgGradleProject, String module) {
        if (atgGradleProject.isAlreadyAddedDependency(module)) {
            LOGGER.debug('Module {} dependency already added to the {}', module, atgGradleProject)
        } else {
            LOGGER.debug('Add module dependency {} to the {}', module, atgGradleProject)
            ATGModule atgModule = atgGradleProject.atgRepository.getATGModule(atgGradleProject.project, module)
            if (atgModule != null) {
                addModuleDependency(atgGradleProject, atgModule)
            } else {
                LOGGER.warn('Dependency {} not found for {}', module, atgGradleProject)
            }
        }
    }

    private void addModuleDependency(ATGGradleProject atgGradleProject, ATGModule atgModule) {
        if (atgModule instanceof ATGProjectModule) {
            addATGProjectModuleDependency(atgGradleProject, (ATGProjectModule) atgModule)
        } else {
            addATGModuleDependency(atgGradleProject, atgModule)
        }
        atgGradleProject.markAddedDependency(atgModule.name)
    }

    private void addATGModuleDependency(ATGGradleProject atgGradleProject, ATGModule atgModule) {
        addModuleClassPathDependencies(atgGradleProject.project, atgModule, null)
        addModuleRequiredDependencies(atgGradleProject, atgModule)
    }

    private static void addATGProjectModuleDependency(ATGGradleProject atgGradleProject, ATGProjectModule atgProjectModule) {
        Project projectDependency = atgProjectModule.project
        Project targetProject = atgGradleProject.project
        LOGGER.debug('Add atg {} module to {}', projectDependency, targetProject)
        targetProject.dependencies.add(ATGPluginConstants.COMPILE_DEPENDENCIES, projectDependency)
    }

    private static void addModuleClassPathDependencies(Project project, ATGModule module, FileCollection filesToExclude) {
        List<File> entries = module.classPathDependencies
        LOGGER.debug('Add classpath dependencies {} from {} module to {}', entries, module, project)
        FileCollection artifacts = project.files(entries.toArray())
        if (filesToExclude != null)
            artifacts = artifacts - filesToExclude

        project.dependencies.add(ATGPluginConstants.COMPILE_DEPENDENCIES, artifacts)
    }

    private void addModuleRequiredDependencies(ATGGradleProject atgGradleProject, ATGModule atgModule) {
        List<String> modules = atgModule.requiredModules
        LOGGER.debug('Add modules required module dependencies {}', modules)
        addDependencies(atgGradleProject, modules)
    }

}