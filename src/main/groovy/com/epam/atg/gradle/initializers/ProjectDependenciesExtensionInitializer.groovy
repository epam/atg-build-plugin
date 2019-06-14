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

package com.epam.atg.gradle.initializers

import com.epam.atg.gradle.ATGPluginConstants
import com.epam.atg.gradle.build.ATGGradleProject
import com.epam.atg.gradle.build.module.ATGModule
import com.epam.atg.gradle.build.module.ATGProjectModule
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
        String atgRequiredType = 'atgRequired' + dependenciesType.capitalize()
        project.dependencies.ext[atgRequiredType] = { String firstModuleName, String ... moduleNames ->
            atg(project, dependenciesType, firstModuleName, false)
            if(moduleNames) {
                for (String moduleName : moduleNames) {
                    atg(project, dependenciesType, moduleName, false)
                }
            }
        }
        String atgRequiredIfPresentType = 'atgRequiredIf' + dependenciesType.capitalize()
        project.dependencies.ext[atgRequiredIfPresentType] = { String firstModuleName, String ... moduleNames ->
            atg(project, dependenciesType, firstModuleName, true)
            if(moduleNames) {
                for (String moduleName : moduleNames) {
                    atg(project, dependenciesType, moduleName, true)
                }
            }
        }
    }

    private static void atg(Project project, String dependencyType, String moduleName, boolean ifPresent) {
        ATGRepository repository = project.rootProject.ext[ATGPluginConstants.PROJECT_ATG_REPOSITORY_PROPERTY] as ATGRepository
        addDependenciesFromModule(project, moduleName, repository, dependencyType, ifPresent)
        updateRepositoryAtgDependenciesProperty(repository, project, moduleName, ifPresent)
    }

    private static void updateRepositoryAtgDependenciesProperty(ATGRepository repository, Project project, String atgModuleName, boolean ifPresent) {
        ATGModule module = repository.getProjectATGModule(new ATGGradleProject(project: project))
        if(!module) {
            throw new GradleException("Unable to resolve dependency on module '$atgModuleName'")
        }
        def modules = ifPresent ? module.requiredIfModules : module.requiredModules
        if (!modules.contains(atgModuleName)) {
            modules.add(atgModuleName)
        }
    }

    private static void addDependenciesFromModule(Project project, String moduleName, ATGRepository repository, String type, boolean ifPresent) {
        ATGModule module = repository.getATGModule(project, moduleName)
        if(!module) {
            if (ifPresent) {
                return
            }
            throw new GradleException("Unable to resolve dependency on module '$moduleName'")
        }
        if (module instanceof ATGProjectModule) {
            project.dependencies.add(type, module.project)
        } else {
            List<String> result = new ArrayList<>()
            result.addAll(module.classPathDependencies.collect { file -> file.absolutePath })
            for (String requiredName : module.requiredModules) {
                result.addAll(getDependenciesFromModule(project, requiredName, repository, ifPresent))
            }
            project.dependencies.add(type, project.files(result))
        }
    }

    private static List<String> getDependenciesFromModule(Project project, String moduleName, ATGRepository repository, boolean ifPresent) {
        ATGModule module = repository.getATGModule(project, moduleName)
        if(!module) {
            if (ifPresent) {
                return []
            }
            throw new GradleException("Unable to resolve dependency on module '$moduleName'")
        }
        if (module instanceof ATGProjectModule) {
            return new ArrayList<String>()
        }
        List<String> result = new ArrayList<>()
        result.addAll(module.classPathDependencies.collect {file -> file.absolutePath})
        for(String requiredName : module.requiredModules) {
            result.addAll(getDependenciesFromModule(project, requiredName, repository, false))
        }
        for(String requiredIfPresentName : module.requiredIfModules) {
            result.addAll(getDependenciesFromModule(project, requiredIfPresentName, repository, true))
        }
        return result
    }
}
