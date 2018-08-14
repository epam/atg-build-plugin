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

/**
 * ATG Assembler EAR
 * This task invokes out of the box ATG and task that will invoke the application assembler, which combines Oracle Commerce Platform libraries, Nucleus component configuration, J2EE applications, and J2EE application components to create a single J2EE application, in the form of an unpacked (open-directory) EAR file.
 * Details:
 * https://docs.oracle.com/cd/E24152_01/Platform.10-1/ATGPlatformProgGuide/html/s0306createunpackedeartask01.html
 * http://docs.oracle.com/cd/E52191_03/Platform.11-1/ATGPlatformProgGuide/html/s0306createunpackedeartask01.html
 */
class AssembleATGTask extends DefaultTask {

    String destinationFile = ""
    String dynamoModules = ""
    String dynamoRoot = ""

    String addEarFile = ""
    boolean collapseClasspath = false
    String contextRootsFile = ""
    String displayName = ""
    String displayVariable = ""
    String dynamoEnvPropsFile = ""
    String layer = ""
    boolean liveConfig = false
    boolean overwrite = false
    String prependJars = ""
    String serverName = ""
    boolean standalone = false

    boolean jboss = false
    boolean omitLicenses = true

    @TaskAction
    void assembleEAR() {
        if (!dynamoRoot) {
            dynamoRoot = project.property(ATGPluginConstants.PROJECT_ATG_ROOT_PROPERTY)
            if (!dynamoRoot) {
                throw new IllegalArgumentException('"dynamoRoot" is required property.')
            }
        }
        List<File> tempLinks = []
        Map atgRootModules = ProjectUtils.getAtgRootProjectsPathsWithModulesNames(project)
        atgRootModules.each { String projectPath, String moduleName ->
            File atgModuleFile = new File("$dynamoRoot/$moduleName")
            if (!atgModuleFile.exists()) {
                Project rootAtgProject = project.project(projectPath)
                FileUtils.createLink(atgModuleFile, rootAtgProject.projectDir)
                tempLinks.add(atgModuleFile)
                project.logger.info('Added temporary link from {} to {}', rootAtgProject.projectDir, atgModuleFile)
            }
        }

        project.ant.taskdef(name: 'assembleEAR',
                classname: 'atg.appassembly.ant.CreateUnpackedEarTask',
                classpath: "$dynamoRoot/home/lib/assembler.jar")

        Map assemblerEARTaskParameters = [
                //required
                destinationFile   : destinationFile,
                dynamoModules     : dynamoModules,
                dynamoRoot        : dynamoRoot,

                //optional
                addEarFile        : addEarFile,
                collapseClasspath : collapseClasspath,
                contextRootsFile  : contextRootsFile,
                displayName       : displayName,
                displayVariable   : displayVariable,
                dynamoEnvPropsFile: dynamoEnvPropsFile,
                layer             : layer,
                liveConfig        : liveConfig,
                overwrite         : overwrite,
                prependJars       : prependJars,
                serverName        : serverName,
                standalone        : standalone,

                //optional for atg 11, does not exists for atg < 11
                jboss             : jboss,

                omitLicenses      : omitLicenses
        ]
        List definedKeys = assemblerEARTaskParameters.findAll {
            (it.value != null && ((it.value instanceof String && !((String) it.value).isEmpty()) || !it.value instanceof String))
        }.collect {
            it.key
        }
        assemblerEARTaskParameters = assemblerEARTaskParameters.subMap(definedKeys)
        println(assemblerEARTaskParameters)
        try {
            project.ant.assembleEAR(assemblerEARTaskParameters) {
                assemblerEARTaskParameters
            }
        } finally {
            for (File link in tempLinks) {
                link.delete()
                project.logger.info('Removed temporary link {}', link)
            }
        }
    }
}
