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

package com.epam.atg.gradle.initializers

import com.epam.atg.gradle.ATGPlugin
import com.epam.atg.gradle.ATGPluginConstants
import com.epam.atg.gradle.ATGPluginExtension
import com.epam.atg.gradle.build.ATGGradleProject
import com.epam.atg.gradle.build.module.ATGProjectModule
import com.epam.atg.gradle.build.utils.ATGDependenciesResolver
import com.epam.atg.gradle.build.utils.ATGModuleTreePrinter
import com.epam.atg.gradle.utils.ProjectUtils
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ModulePluginInitializer extends AbstractProjectPluginInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(getClass())

    @Override
    protected boolean isSupportedProject(Project project) {
        boolean isCompiled = project.plugins.hasPlugin('java') || project.plugins.hasPlugin('kotlin')
        if(!(isCompiled && super.isSupportedProject(project))) {
            return false
        }
        if(!ProjectUtils.isChildOfAtgRootProject(project)) {
            LOGGER.info('Project {} is not child of any ATG root project', project)
            return false
        }
        return true
    }

    @Override
    void apply(Project project) {
        checkRequirements(project)
        applyATGModuleToGradleProject(new ATGGradleProject(project: project))
    }

    private static void createPrintDependenciesTask(ATGGradleProject atgGradleProject) {
        atgGradleProject.project.task(ATGPluginConstants.ATG_DEPENDENCIES_TASK, group: ATGPluginConstants.ATG_TASK_GROUP) {
            doLast {
                ATGModuleTreePrinter printer = new ATGModuleTreePrinter(atgGradleProject.atgRepository)

                ATGPluginExtension pluginExtension = ATGPlugin.getPluginExtension(atgGradleProject.project)
                File outputFile = pluginExtension.dependenciesTreeOutputFile
                if (outputFile != null) {
                    File outputDir = outputFile.parentFile
                    if(!outputDir.exists()) {
                        outputDir.mkdirs()
                    }
                    outputFile.withPrintWriter { printWriter ->
                        printer.printDependencies(atgGradleProject.atgProjectModule.name, printWriter)
                    }
                    println("Dependencies tree for project " + atgGradleProject.project.name + " printed to file " + outputFile.getAbsolutePath())
                } else {
                    println("Dependencies tree for project " + atgGradleProject.project.name + ":")
                    printer.printDependencies(atgGradleProject.atgProjectModule.name)
                }
            }
        }
    }

    private static void checkRequirements(Project project) {
        if (!project.hasProperty(ATGPluginConstants.PROJECT_ATG_REPOSITORY_PROPERTY)) {
            throw new IllegalArgumentException('ATG plugin must be applied to root project.')
        }
    }

    private static void applyATGModuleToGradleProject(ATGGradleProject atgGradleProject) {
        LOGGER.info('Apply ATG module plugin to {}', atgGradleProject)

        ATGProjectModule currentModule = atgGradleProject.atgRepository.getProjectATGModule(atgGradleProject)
        if (currentModule == null) {
            return
        }
        atgGradleProject.atgProjectModule = currentModule

        defineProjectDescription(atgGradleProject)
        addConfigResources(atgGradleProject)
        addProjectDependencies(atgGradleProject)
        createPrintDependenciesTask(atgGradleProject)
    }

    private static void addConfigResources(ATGGradleProject atgGradleProject) {
        String atgConfigPath = atgGradleProject.atgProjectModule.getAtgConfigPath()
        if (!atgConfigPath || !atgGradleProject.project.file(atgConfigPath).isDirectory()) {
            return
        }
        LOGGER.info('Add config resources {}', atgConfigPath)
        if (!atgConfigPath.startsWith('/')) {
            atgConfigPath = "/$atgConfigPath"
        }
        SourceSet configSourceSet = atgGradleProject.project.sourceSets.maybeCreate(ATGPluginConstants.CONFIG_SOURCESET_NAME)
        configSourceSet.resources.srcDirs(atgConfigPath)
    }

    private static void addProjectDependencies(ATGGradleProject atgGradleProject) {
        ATGDependenciesResolver atgDependenciesResolver = new ATGDependenciesResolver()
        atgDependenciesResolver.defineDependencies(atgGradleProject)
    }

    private static void defineProjectDescription(ATGGradleProject atgGradleProject) {
        def projectDescriptionOverride = atgGradleProject.atgProjectModule.getDescription()
        if (!atgGradleProject.project.getDescription() && projectDescriptionOverride) {
            atgGradleProject.project.setDescription(projectDescriptionOverride)
        }
    }

}