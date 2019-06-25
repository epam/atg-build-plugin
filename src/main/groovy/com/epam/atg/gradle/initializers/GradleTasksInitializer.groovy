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

import com.epam.atg.gradle.ATGPlugin
import com.epam.atg.gradle.ATGPluginConstants
import com.epam.atg.gradle.assemble.RunAssemblerTask
import com.epam.atg.gradle.build.ATGGradleProject
import com.epam.atg.gradle.manifest.IndividualResource
import com.epam.atg.gradle.manifest.ManifestConfig
import com.epam.atg.gradle.build.repository.ATGRepository
import com.epam.atg.gradle.manifest.ManifestGeneratorTask
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

class GradleTasksInitializer extends AbstractProjectPluginInitializer {

    @Override
    void apply(Project project) {
        project.ext.RunAssembler = RunAssemblerTask.class


        def atgClassPath = project.configurations.create(ATGPluginConstants.MANIFEST_ATG_CLASSPATH)

        project.afterEvaluate {
            project.tasks.create(ATGPluginConstants.DEPENDENCIES_SINK_TASK, Copy.class, {  task ->
                def plugin = ATGPlugin.getPluginExtension(project)
                task.onlyIf {
                    println("dependenciesSinkPath: ${plugin.dependenciesSinkPath}")
                    plugin.dependenciesSinkPath != null
                }
                task.from(project.configurations.atgClassPath)
                task.into(plugin.dependenciesSinkPath)
                task.doFirst {project.delete(plugin.dependenciesSinkPath)}
            })
            project.tasks.create(ATGPluginConstants.MANIFEST_TASK, ManifestGeneratorTask.class, { task ->
                ManifestConfig manifestConfig = buildAtgManifestConfig(project)
                task.onlyIf {
                    if (manifestConfig == null) {
                        return false
                    }
                    boolean notExist = !manifestConfig.override && !new File(manifestConfig.manifestFilePath).exists()
                    boolean override = manifestConfig.override
                    boolean skipGeneration = manifestConfig.skipGeneration
                    return !skipGeneration && (override || notExist)
                }
                task.setGroup(ATGPluginConstants.ATG_TASK_GROUP)
                task.manifestConfig = manifestConfig
                task.outputFile = new File(project.projectDir.absolutePath + '/' + manifestConfig?.manifestFilePath)
            })
        }
    }

    private ManifestConfig buildAtgManifestConfig(Project project) {
        def plugin = ATGPlugin.getPluginExtension(project)
        def manifestConfig = plugin.manifestConfig
        if (manifestConfig == null) {
            return null
        }

        ATGRepository repository = project.atgRepository as ATGRepository
        def repositoryModule = repository.getProjectATGModule(new ATGGradleProject(project: project))

        manifestConfig.atgRequired.addAll(repositoryModule.requiredModules)
        manifestConfig.atgRequiredIfPresent.addAll(repositoryModule.requiredIfModules)

        if (project.jar.enabled) {
            def jarArchive = (project.jar.archivePath.absolutePath - project.projectDir.path).substring(1) as String
            manifestConfig.projectClassPath.add(jarArchive)
        } else {
            if (project.sourceSets.main.java.srcDirs.findAll { it.exists() }.size() > 0) {
                def classesDir = (project.sourceSets.main.java.outputDir.absolutePath - project.projectDir.path).substring(1) as String
                manifestConfig.projectClassPath.add(classesDir)
            }
            if (project.sourceSets.main.resources.srcDirs.findAll { it.exists() }.size() > 0) {
                def resourcesDir = (project.sourceSets.main.output.resourcesDir.absolutePath - project.projectDir.path).substring(1) as String
                manifestConfig.projectClassPath.add(resourcesDir)
            }
        }
        manifestConfig.atgClassPath.addAll(manifestConfig.projectClassPath)
        manifestConfig.atgClassPath.addAll(project.configurations.atgClassPath.collect {
            plugin.dependenciesSinkPath ? (plugin.dependenciesSinkPath + File.separator + it.name) : it.absolutePath
        })
        if (manifestConfig.generateIndividualResources) {
            for (projectClassPath in manifestConfig.projectClassPath) {
                manifestConfig.individualResources.add(new IndividualResource(name: projectClassPath, atgClientUpdateFile: true))
            }
        }
        if (manifestConfig.generateAtgClientClassPath && !manifestConfig.projectClassPath.isEmpty()) {
            manifestConfig.atgClientClassPath.addAll(manifestConfig.projectClassPath)
        }
        manifestConfig
    }
}