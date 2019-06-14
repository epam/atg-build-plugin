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

package com.epam.atg.gradle.build.repository

import com.epam.atg.gradle.build.ATGGradleProject
import com.epam.atg.gradle.build.module.ATGModule
import com.epam.atg.gradle.build.module.ATGProjectModule
import com.epam.atg.gradle.build.utils.ManifestUtils
import com.epam.atg.gradle.utils.ProjectUtils
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ConcurrentHashMap
import java.util.jar.Manifest

class ATGRepositoryImpl implements ATGRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ATGRepositoryImpl.class)

    private File atgRoot
    private Map<String, ATGModule> modulesMap
    private Map<String, String> installUnitFoldersMap

    void initializeRepository() {
        LOGGER.info('Initializing ATGRepository')

        modulesMap = new ConcurrentHashMap<>()
        installUnitFoldersMap = new HashMap<>()

        scanInstalledModules()
    }


    private void scanInstalledModules() {
        atgRoot.eachDir() { dir ->
            Manifest manifest = ManifestUtils.findManifestInModuleDirectory(dir)
            if (manifest != null) {
                String installUnit = ManifestUtils.getInstalledUnit(manifest)
                if (installUnit) {
                    mapInstallUnitToModuleFolder(installUnit, dir.absolutePath)
                }
            }
        }
    }

    private void mapInstallUnitToModuleFolder(String moduleName, String installedModulePath) {
        installUnitFoldersMap.put(moduleName, installedModulePath)
    }

    private boolean isInstalledModule(String moduleName) {
        return installUnitFoldersMap.containsKey(moduleName)
    }

    private String getInstalledModuleRelativeFolder(String moduleName) {
        return installUnitFoldersMap.get(moduleName)
    }

    private void addModule(ATGModule module) {
        modulesMap.put(module.name, module)
    }

    private ATGModule findModule(String module) {
        return modulesMap.get(module)
    }

    @Override
    ATGProjectModule getProjectATGModule(ATGGradleProject atgGradleProject) {
        String moduleName = atgGradleProject.calculateAtgModuleName()
        LOGGER.debug('Project {} has module name: {}', atgGradleProject, moduleName)
        ATGModule atgModule = getATGModule(atgGradleProject.project, moduleName)
        if(atgModule instanceof ATGProjectModule) {
            return (ATGProjectModule) atgModule
        }
        LOGGER.error('Module {} is not ATGProjectModule', moduleName)
        return null
    }

    @Override
    ATGModule getATGModule(Project project, String moduleName) {
        if(!moduleName) {
            LOGGER.warn('Unable to get module with null moduleName for project: {}', project)
            return null
        }
        ATGModule atgModule = findModule(moduleName)
        if (atgModule != null) {
            return atgModule
        }
        Project atgRootGradleProjectForModule = ProjectUtils.findAtgRootProjectForModule(moduleName, project)
        if (atgRootGradleProjectForModule) {
            atgModule = createCustomAtgModule(moduleName, atgRootGradleProjectForModule)
        } else  {
            atgModule = createOutOfTheBoxAtgModule(moduleName)
        }
        if (atgModule == null) {
            LOGGER.warn('Could not find module {}', moduleName)
            return null
        }
        if(!atgModule.initialize()) {
            LOGGER.warn('{} atg module not valid and will be skipped.', project)
            return null
        }
        addModule(atgModule)
        return atgModule
    }

    @Override
    List<String> getDependencies(String moduleName) {
        ATGModule module = modulesMap[moduleName]
        if(module == null) {
            return []
        }
        return module.requiredModules.asImmutable()
    }

    private ATGModule createCustomAtgModule(String moduleName, Project atgRootGradleProjectForModule) {
        ATGModule module = null
        LOGGER.debug('Module {} is part of the {}', moduleName, atgRootGradleProjectForModule)
        File atgRootProjectDir = atgRootGradleProjectForModule.projectDir
        String atgRootModuleName = ProjectUtils.getAtgRootModuleName(atgRootGradleProjectForModule)
        String projectRelativeModuleName = ProjectUtils.getModuleProjectRelativeName(moduleName, atgRootModuleName)
        String relativePath = ManifestUtils.convertModuleNameToRelativePath(projectRelativeModuleName)
        File moduleLocation = new File(atgRootProjectDir.absolutePath + File.separator + relativePath)
        if (moduleLocation.exists()) {
            Project moduleGradleProject = ProjectUtils.findModuleProject(moduleLocation, atgRootGradleProjectForModule)
            if (moduleGradleProject) {
                module = new ATGProjectModule(moduleName, moduleLocation, moduleGradleProject)
            } else {
                LOGGER.debug('Project {} not handled by gradle', moduleLocation)
                module = new ATGModule(moduleName, moduleLocation)
            }
        } else {
            LOGGER.warn('Module location {} does not exist', moduleLocation)
        }
        if (module == null) {
            LOGGER.warn('Module does not exist in location {}', moduleLocation)
        }
        return module
    }

    private ATGModule createOutOfTheBoxAtgModule(String moduleName) {
        ATGModule module = null
        File moduleLocation
        LOGGER.debug('Module {} is ATG module', moduleName)
        String rootModuleName = ManifestUtils.getRootModuleFromModuleName(moduleName)
        if (isInstalledModule(rootModuleName)) {
            String installedModulePath = getInstalledModuleRelativeFolder(rootModuleName)
            moduleLocation = new File(installedModulePath + File.separator + ManifestUtils.convertModuleNameToRelativePath(moduleName))
        } else {
            moduleLocation = new File(atgRoot.absolutePath + File.separator + ManifestUtils.convertModuleNameToRelativePath(moduleName))
        }
        if (moduleLocation.exists()) {
            module = new ATGModule(moduleName, moduleLocation)
        }
        if (module == null) {
            LOGGER.warn('Module does not exist in location {}', moduleLocation)
        }
        return module
    }


}
