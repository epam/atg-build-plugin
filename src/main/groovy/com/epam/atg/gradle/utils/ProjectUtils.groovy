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

package com.epam.atg.gradle.utils

import com.epam.atg.gradle.ATGPluginConstants
import org.gradle.api.Project
import org.gradle.api.UnknownProjectException

class ProjectUtils {
    static Project findAtgRootProject(Project project) throws UnknownProjectException {
        def projectPathToModuleName = getAtgRootProjectsPathsWithModulesNames(project)
        String projectPath = project.path
        for(String currentPath : projectPathToModuleName.keySet()) {
            if(projectPath.startsWith(currentPath)) {
                return project.project(currentPath)
            }
        }
        throw new UnknownProjectException("Unable to find atg root project for $project")
    }

    static boolean isChildOfAtgRootProject(Project project) {
        def projectPathToModuleName = getAtgRootProjectsPathsWithModulesNames(project)
        String projectPath = project.path
        for(String currentPath : projectPathToModuleName.keySet()) {
            if(projectPath.startsWith(currentPath)) {
                return true
            }
        }
        return false
    }

    static Map<String, String> getAtgRootProjectsPathsWithModulesNames(Project project) {
        return project.rootProject.ext[ATGPluginConstants.ATG_ROOT_PROJECTS] as Map<String, String>
    }

    static String getAtgRootModuleName(Project atgRootProject) {
        return getAtgRootProjectsPathsWithModulesNames(atgRootProject).get(atgRootProject.path)
    }

    static Project findModuleProject(File moduleLocation, Project atgRootProject) {
        atgRootProject.logger.debug('Find module {} in  {}', moduleLocation, atgRootProject)
        for (Project subProject : atgRootProject.allprojects) {
            if (subProject.projectDir.absolutePath == moduleLocation.absolutePath) {
                return subProject
            }
        }
        return null
    }

    static Project findAtgRootProjectForModule(String moduleName, Project anyProject) {
        for(def entry : getAtgRootProjectsPathsWithModulesNames(anyProject)) {
            if(moduleName.startsWith(entry.value)) {
                return anyProject.project(entry.key)
            }
        }
        return null
    }

    static String getModuleProjectRelativeName(String moduleName, String rootModuleName) {
        return moduleName.substring(rootModuleName.length())
    }
}