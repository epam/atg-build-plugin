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
import com.epam.atg.gradle.build.module.ATGModule
import com.epam.atg.gradle.build.repository.ATGRepository
import org.gradle.api.GradleException
import org.gradle.api.Project

class ProjectDependenciesExtensionInitializer extends AbstractProjectPluginInitializer {
    @Override
    void apply(Project project) {
        prepareAtgDependenciesByType(project, ATGPluginConstants.API_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.API_ELEMENTS_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.COMPILE_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.COMPILE_CLASSPATH_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.COMPILE_ONLY_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.IMPLEMENTATION_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.RUNTIME_CLASSPATH_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.RUNTIME_ELEMENTS_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.RUNTIME_ONLY_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.TEST_COMPILE_CLASSPATH_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.TEST_COMPILE_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.TEST_COMPILE_ONLY_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.TEST_IMPLEMENTATION_DEPENDENCIES)
        prepareAtgDependenciesByType(project, ATGPluginConstants.TEST_RUNTIME_CLASSPATH_DEPENDENCIES)
    }

    private static void prepareAtgDependenciesByType(Project project, String dependenciesType) {
        String atgDependenciesType = 'atg' + dependenciesType.capitalize()
        project.dependencies.ext[atgDependenciesType] = { String firstModuleName, String ... moduleNames ->
            atg(project, dependenciesType, firstModuleName)
            if(moduleNames) {
                for (String moduleName : moduleNames) {
                    atg(project, dependenciesType, moduleName)
                }
            }
        }
    }

    private static void atg(Project project, String dependencyType, String moduleName) {
        ATGRepository repository = project.rootProject.ext[ATGPluginConstants.PROJECT_ATG_REPOSITORY_PROPERTY] as ATGRepository
        List<String> result = getDependenciesFromModule(project, moduleName, repository)
        project.dependencies.add(dependencyType, project.files(result))
    }

    private static List<String> getDependenciesFromModule(Project project, String moduleName, ATGRepository repository) {
        ATGModule module = repository.getATGModule(project, moduleName)
        if(!module) {
            throw new GradleException("Unable to resolve dependency on module '$moduleName'")
        }
        List<String> result = new ArrayList<>()
        result.addAll(module.classPathDependencies.collect {file -> file.absolutePath})
        for(String requiredName : module.requiredModules) {
            result.addAll(getDependenciesFromModule(project, requiredName, repository))
        }
        return result
    }
}
