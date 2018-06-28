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

package com.epam.atg.gradle

class ATGPluginConstants {

    static final String ATG_ROOT_PROJECTS = 'atgRootProjects'

    static final String PROJECT_ATG_ROOT_PROPERTY = 'atgRoot'
    static final String PROJECT_ATG_REPOSITORY_PROPERTY = 'atgRepository'
    static final String PROJECT_SETTINGS_EXCLUDE_MODULES = 'excludedAtgProjects'

    static final String ATG_RUN_ASSEMBLER_ABSOLUTE_PATH = 'runAssemblerDir'

    static final String ATG_TASK_GROUP = 'atg'
    static final String ATG_PLUGIN_EXTENSION_NAME = 'atg'

    static final String ATG_DEPENDENCIES_TASK = 'atgDependencies'
    static final String ATG_ROOT_MODULES_TASK = 'atgRootModules'
    static final String CONFIG_SOURCESET_NAME = 'config'

    static final String API_DEPENDENCIES = 'api'
    static final String API_ELEMENTS_DEPENDENCIES = 'apiElements'
    static final String COMPILE_DEPENDENCIES = 'compile'
    static final String COMPILE_CLASSPATH_DEPENDENCIES = 'compileClasspath'
    static final String COMPILE_ONLY_DEPENDENCIES = 'compileOnly'
    static final String IMPLEMENTATION_DEPENDENCIES = 'implementation'
    static final String RUNTIME_CLASSPATH_DEPENDENCIES = 'runtimeClasspath'
    static final String RUNTIME_ELEMENTS_DEPENDENCIES = 'runtimeElements'
    static final String RUNTIME_ONLY_DEPENDENCIES = 'runtimeOnly'
    static final String TEST_COMPILE_CLASSPATH_DEPENDENCIES = 'testCompileClasspath'
    static final String TEST_COMPILE_DEPENDENCIES = 'testCompile'
    static final String TEST_COMPILE_ONLY_DEPENDENCIES = 'testCompileOnly'
    static final String TEST_IMPLEMENTATION_DEPENDENCIES = 'testImplementation'
    static final String TEST_RUNTIME_CLASSPATH_DEPENDENCIES = 'testRuntimeClasspath'
}