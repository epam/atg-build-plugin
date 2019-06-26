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

package com.epam.atg.gradle

import com.epam.atg.gradle.initializers.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginAware
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Aleksei Prokofev
 */
class ATGPlugin implements Plugin<PluginAware> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ATGPlugin.class)
    private static final List<? extends Initializer> INITIALIZERS = [
            new ATGPluginExtensionProjectPluginInitializer(),
            new RootProjectPluginInitializer(),
            new ModulePluginInitializer(),
            new SettingsPluginInitializer(),
            new ProjectDependenciesExtensionInitializer(),
            new GradleTasksInitializer(),
    ].asImmutable()

    @Override
    void apply(PluginAware target) {
        boolean initialized = false
        for (Initializer<? extends PluginAware> initializer in INITIALIZERS) {
            if (initializer.isSupported(target)) {
                initializer.apply(target)
                initialized = true
                LOGGER.debug("Initializer {} was applied to {} of type {}", initializer, target, target.class)
            }
        }
        if (!initialized) {
            LOGGER.warn('No one initializer was been applied for "{}"', target)
        }
    }

    static ATGPluginExtension getPluginExtension(Project project) {
        return project.extensions.getByName(ATGPluginConstants.ATG_PLUGIN_EXTENSION_NAME) as ATGPluginExtension
    }
}