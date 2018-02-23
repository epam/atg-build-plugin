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

import com.epam.atg.gradle.ATGPluginConstants
import com.epam.atg.gradle.build.repository.ATGRepository
import com.epam.atg.gradle.build.repository.ATGRepositoryFactory
import com.epam.atg.gradle.utils.ProjectUtils
import org.gradle.api.Project
import org.gradle.api.UnknownProjectException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RootProjectPluginInitializer extends AbstractProjectPluginInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootProjectPluginInitializer.class)

    @Override
    protected boolean isSupportedProject(Project project) {
        return project.parent == null
    }

    @Override
    void apply(Project project) {
        checkRequirements(project)
        initializeAtgRootProjects(project)
        initializeAtgRepository(project)
        initializeRootProjectTasks(project)
    }

    private static void checkRequirements(Project project) {
        if (!project.hasProperty(ATGPluginConstants.PROJECT_ATG_ROOT_PROPERTY))
            throw new IllegalArgumentException("${ATGPluginConstants.PROJECT_ATG_ROOT_PROPERTY} project property is required")
    }

    private static void initializeRootProjectTasks(Project project) {
        project.task(ATGPluginConstants.ATG_ROOT_MODULES_TASK, group: ATGPluginConstants.ATG_TASK_GROUP) {
            doLast {
                Map map = ProjectUtils.getAtgRootProjectsPathsWithModulesNames(project)
                map.sort().each { k, v ->
                    println "Project '$k' linked to '$v' dir"
                }
            }
        }
    }

    private static void initializeAtgRootProjects(Project project) {
        String rootProjectsNames = project.findProperty(ATGPluginConstants.ATG_ROOT_PROJECTS)
        if (!rootProjectsNames) {
            rootProjectsNames = ':'
        }
        Map<String, String> projectNameToModuleName = new HashMap<>()
        for (String item in rootProjectsNames.split(',')) {
            String[] parts = item.trim().split("=>")
            String projectName = parts[0].trim()
            Project atgRootProject
            try {
                atgRootProject = project.project(projectName)
            } catch (UnknownProjectException ignored) {
                LOGGER.error('Unable to find project with name {}', projectName)
                continue
            }
            String moduleName = parts.length == 2 ? parts[1].trim() : atgRootProject.projectDir.name
            projectNameToModuleName.put(projectName, moduleName)
        }

        LOGGER.info('ATG root projects -> ATG module: {}', projectNameToModuleName)
        project.ext[ATGPluginConstants.ATG_ROOT_PROJECTS] = projectNameToModuleName
    }

    private static void initializeAtgRepository(Project project) {
        String atgRoot = project.property(ATGPluginConstants.PROJECT_ATG_ROOT_PROPERTY) as String
        File atgRootDirectory = new File(atgRoot)
        if (!(atgRootDirectory.exists() && atgRootDirectory.isDirectory())) {
            throw new IllegalArgumentException("Incorrect ATG Root Directory $atgRootDirectory")
        }

        ATGRepository atgRepository = ATGRepositoryFactory.createRepository(atgRoot)
        project.ext[ATGPluginConstants.PROJECT_ATG_REPOSITORY_PROPERTY] = atgRepository

        LOGGER.info('ATG Repository initialized. path: {}', atgRoot)
    }
}