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

package com.epam.atg.gradle.build

import com.epam.atg.gradle.build.module.ATGProjectModule
import com.epam.atg.gradle.build.repository.ATGRepository
import com.epam.atg.gradle.build.utils.ManifestUtils
import com.epam.atg.gradle.utils.ProjectUtils
import org.gradle.api.Project

class ATGGradleProject {

    public static final String RESOURCES_JAR_FILENAME = 'resources.jar'

    private Set<String> addedDependenciesList
    private Project project
    private ATGProjectModule atgProjectModule

    String calculateAtgModuleName() {
        Project atgRootProject = ProjectUtils.findAtgRootProject(project)
        String atgRootModuleName = ProjectUtils.getAtgRootModuleName(atgRootProject)
        if(project == atgRootProject){
            return atgRootModuleName
        }
        URI relativeLocation = atgRootProject.projectDir.toURI().relativize(project.projectDir.toURI())
        String subName = ManifestUtils.convertRelativeURIPathToModuleName(relativeLocation)
        return atgRootModuleName + ManifestUtils.ATG_MODULE_SEPARATOR + subName
    }

    Project getProject() {
        return project
    }

    ATGProjectModule getAtgProjectModule() {
        return atgProjectModule
    }

    void setAtgProjectModule(ATGProjectModule atgProjectModule) {
        this.atgProjectModule = atgProjectModule
    }

    ATGRepository getAtgRepository() {
        return project.atgRepository
    }

    File getProjectJarArchive() {
        return new File(project.libsDirName + File.separator + project.jar.archiveName as String)
    }

    List<File> getProjectSourceSetsOutputFiles() {
        def outputFiles = []
        project.sourceSets.each { outputFiles.addAll(it.output.files) }
        return outputFiles
    }

    File getProjectResources() {
        return new File(project.libsDirName + File.separator + RESOURCES_JAR_FILENAME as String)
    }

    void clearModuleDependencyMarkers() {
        addedDependenciesList = new HashSet<>()
    }

    void markAddedDependency(String moduleName) {
        addedDependenciesList.add(moduleName)
    }

    boolean isAlreadyAddedDependency(String moduleName) {
        addedDependenciesList.contains(moduleName)
    }

    @Override
    String toString() {
        return project != null ? project.toString() : super.toString()
    }
}
