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

package com.epam.atg.gradle.build.module

import com.epam.atg.gradle.build.utils.ManifestUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.CopyOnWriteArrayList
import java.util.jar.Manifest

class ATGModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(ATGModule.class)

    private String name
    private File moduleLocation

    private String description
    private String atgConfigPath

    private List<String> classPathEntries
    private List<File> classPathDependencyFiles

    private List<String> requiredModules = new CopyOnWriteArrayList<>()
    private List<String> requiredIfModules = new CopyOnWriteArrayList<>()

    ATGModule(String moduleName, File moduleLocation) {
        if (moduleLocation == null || !moduleLocation.exists())
            throw new IllegalArgumentException("Module folder should exist $moduleLocation")

        this.name = moduleName
        this.moduleLocation = moduleLocation
    }

    boolean initialize() {
        Manifest manifest = ManifestUtils.findManifestInModuleDirectory(moduleLocation)
        if (manifest == null) {
            return false
        }
        initializeClassPath(manifest)
        initializeRequiredModules(manifest)
        initializeRequiredIfModules(manifest)
        initializeNameAndDescription(manifest)
        initializeConfigPath(manifest)

        return true
    }

    private void initializeClassPath(Manifest manifest) {
        classPathEntries = ManifestUtils.getATGModuleClassPath(manifest)
        //TODO find out if need such functionality
        //classPathEntries.addAll(ManifestUtils.getATGIndividualResources(manifest))
        classPathDependencyFiles = new ArrayList<>()
        String moduleAbsolutePath = moduleLocation.absolutePath
        LOGGER.debug("initializeClassPath -> moduleAbsolutePath: {}", moduleAbsolutePath)
        for (String classPathEntry : classPathEntries) {
            File classPathDependencyFile = new File(moduleAbsolutePath + File.separator + classPathEntry)
            if (classPathDependencyFile.exists()) {
                classPathDependencyFiles.add(classPathDependencyFile)
            }
        }
        LOGGER.debug("initializeClassPath-> manifest: {} classPathEntries: {}, classPathDependencyFiles: {}",
                ManifestUtils.getDescription(manifest), classPathEntries, classPathDependencyFiles)
    }

    protected void initializeRequiredModules(Manifest manifest) {
        requiredModules.addAll(ManifestUtils.getATGRequiredModules(manifest))
    }

    protected void initializeRequiredIfModules(Manifest manifest) {
        requiredIfModules.addAll(ManifestUtils.getATGRequiredIfModules(manifest))
    }


    private void initializeNameAndDescription(Manifest manifest) {
        description = ManifestUtils.getDescription(manifest)
    }

    private void initializeConfigPath(Manifest manifest) {
        atgConfigPath = ManifestUtils.getConfigPath(manifest)
    }

    void addRequiredModule(String module) {
        if (!requiredModules.contains(module)) {
            requiredModules.add(module)
        } else {
            LOGGER.warn("module {} already exists in requiredModules", module)
        }
    }

    void addRequiredIfPresentModule(String module) {
        if (!requiredIfModules.contains(module)) {
            requiredIfModules.add(module)
        } else {
            LOGGER.warn("module {} already exists in requiredIfModules", module)
        }
    }

    List<File> getClassPathDependencies() {
        return classPathDependencyFiles.asImmutable()
    }

    String getDescription() {
        return description
    }

    String getAtgConfigPath() {
        return atgConfigPath
    }

    List<String> getRequiredModules() {
        return requiredModules.asImmutable()
    }

    List<String> getRequiredIfModules() {
        return requiredIfModules.asImmutable()
    }

    String getName() {
        return name
    }

    @Override
    String toString() {
        return name
    }
}
