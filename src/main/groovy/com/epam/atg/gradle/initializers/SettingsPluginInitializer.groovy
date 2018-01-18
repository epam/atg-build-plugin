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
import com.epam.atg.gradle.build.utils.ManifestUtils
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.PluginAware
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SettingsPluginInitializer implements Initializer<Settings> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsPluginInitializer.getClass())

    private static FileFilter projectFilter = new ProjectsFilter()

    @Override
    boolean isSupported(PluginAware target) {
        return target instanceof Settings
    }

    @Override
    void apply(Settings settings) {
        autoIncludeModuleProjects(settings)
    }

    static void autoIncludeModuleProjects(Settings settings) {
        LOGGER.debug('Apply atg plugin for setting...')
        //find projects
        List<File> projects = findAtgProjects(settings)

        // build projects names
        List<String> projectNames = buildProjectNames(settings, projects)

        //add projects to settings
        includeProjects(settings, projectNames)
    }

    static void includeProjects(Settings settings, List<String> projectNames) {
        List<String> excludedProjects = settings.hasProperty(ATGPluginConstants.PROJECT_SETTINGS_EXCLUDE_MODULES) ?
                extractExcludedProjects(settings.properties[ATGPluginConstants.PROJECT_SETTINGS_EXCLUDE_MODULES] as String) : []
        LOGGER.debug('excludedProjects = {}', excludedProjects)
        for(String projectName : projectNames) {
            if (!excludedProjects.contains(projectName) && !settings.findProject(projectName)) {
                settings.include(projectName)
                LOGGER.debug('include project {}', projectName)
            }
        }
    }

    private static List<String> extractExcludedProjects(String value) {
        return value.split(',').collect { it.trim() }
    }

    private static List<String> buildProjectNames(Settings settings, List<File> projects) {
        int rootOffset = settings.rootDir.absolutePath.length()
        List<String> result = new ArrayList<>()
        for (File project in projects) {
            result.add(project.absolutePath.substring(rootOffset).replaceAll('[/\\\\]', ':'))
        }
        LOGGER.debug('projectNames = {}', result)
        return result
    }

    private static List<File> findAtgProjects(Settings settings) {
        List<File> result = new ArrayList<>()
        findAtgProjectsRecursive(settings.rootDir.listFiles(projectFilter), result)
        return result
    }

    private static void findAtgProjectsRecursive(File[] projectDirs, List<File> result) {
        LOGGER.debug('findAtgProjectsRecursive: projectDirs = {}, result = {}', projectDirs, result)
        for (File projectDir in projectDirs) {
            result.add(projectDir)
            findAtgProjectsRecursive(projectDir.listFiles(projectFilter), result)
        }
    }


    private static class ProjectsFilter implements FileFilter {
        @Override
        boolean accept(File projectDir) {
            if (!projectDir.isDirectory()) {
                return false
            }
            File meta = new File(projectDir.absolutePath + File.separator + ManifestUtils.META_INF_DIR_NAME)
            if (!meta.exists() || !meta.isDirectory()) {
                return false
            }
            String[] subfiles = meta.list()
            def manifestFile = subfiles.find {
                it.equalsIgnoreCase(ManifestUtils.MANIFEST_FILE_NAME)
            }
            return manifestFile != null
        }
    }
}
