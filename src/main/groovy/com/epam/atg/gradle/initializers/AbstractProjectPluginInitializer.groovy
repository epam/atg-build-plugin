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

import org.gradle.api.Project
import org.gradle.api.plugins.PluginAware

abstract class AbstractProjectPluginInitializer implements Initializer<Project> {

    @Override
    boolean isSupported(PluginAware target) {
        return target instanceof Project && isSupportedProject((Project) target)
    }

    protected boolean isSupportedProject(Project project) {
        return true
    }

}