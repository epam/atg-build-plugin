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

package com.epam.atg.gradle.manifest

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class ManifestGeneratorTask extends DefaultTask {

    private static final String LINE_PATTERN = "%s: %s\n"

    public static final String MANIFEST_VERSION = "Manifest-Version"
    public static final String ATG_CONFIG_PATH = "ATG-Config-Path"
    public static final String ATG_REQUIRED = "ATG-Required"
    public static final String ATG_REQUIRED_IF_PRESENT = "ATG-Required-If-Present"
    public static final String ATG_CLASS_PATH = "ATG-Class-Path"
    public static final String ATG_CLIENT_CLASS_PATH = "ATG-Client-Class-Path"
    public static final String ATG_J2EE = "ATG-J2EE"
    public static final String ATG_EAR_MODULE = "ATG-EAR-Module"
    public static final String ATG_PRODUCT = "ATG-Product"
    public static final String ANT_VERSION = "Ant-Version"
    public static final String CREATED_BY = "Created-By"
    public static final String INDIVIDUAL_RESOURCE_NAME = "Name"
    public static final String INDIVIDUAL_RESOURCE_ATG_CLIENT_UPDATE_FILE = "ATG-Client-Update-File"
    public static final String INDIVIDUAL_RESOURCE_ATG_ASSEMBLER_IMPORT_FILE = "ATG-Assembler-Import-File"

    @Nested
    ManifestConfig manifestConfig

    @OutputFile
    File outputFile

    @TaskAction
    void executeManifestGeneratorTask() {
        println('config ' + manifestConfig)

        StringBuilder output = StringBuilder.newInstance()
        if (manifestConfig.manifestVersion) {
            output <<= String.format(LINE_PATTERN, MANIFEST_VERSION, manifestConfig.manifestVersion)
        }
        if (manifestConfig.atgConfigPath) {
            output <<= String.format(LINE_PATTERN, ATG_CONFIG_PATH, manifestConfig.atgConfigPath)
        }
        if (!manifestConfig.atgRequired.isEmpty()) {
            output <<= String.format(LINE_PATTERN, ATG_REQUIRED, printLongList(manifestConfig.atgRequired, 20))
        }
        if (!manifestConfig.atgRequiredIfPresent.isEmpty()) {
            output <<= String.format(LINE_PATTERN, ATG_REQUIRED_IF_PRESENT, printLongList(manifestConfig.atgRequiredIfPresent, 20))
        }
        if (!manifestConfig.atgClassPath.isEmpty()) {
            output <<= String.format(LINE_PATTERN, ATG_CLASS_PATH, printLongList(manifestConfig.atgClassPath, 4))
        }
        if (!manifestConfig.atgClientClassPath.isEmpty()) {
            output <<= String.format(LINE_PATTERN, ATG_CLIENT_CLASS_PATH, printLongList(manifestConfig.atgClientClassPath, 5))
        }
        if (manifestConfig.atgJ2ee) {
            output <<= String.format(LINE_PATTERN, ATG_J2EE, manifestConfig.atgJ2ee)
        }
        if (manifestConfig.atgEarModule) {
            output <<= String.format(LINE_PATTERN, ATG_EAR_MODULE, manifestConfig.atgEarModule)
        }
        if (manifestConfig.atgProduct) {
            output <<= String.format(LINE_PATTERN, ATG_PRODUCT, manifestConfig.atgProduct)
        }
        if (manifestConfig.antVersion) {
            output <<= String.format(LINE_PATTERN, ANT_VERSION, manifestConfig.antVersion)
        }
        if (manifestConfig.createdBy) {
            output <<= String.format(LINE_PATTERN, CREATED_BY, manifestConfig.createdBy)
        }
        for (other in manifestConfig.others) {
            output <<= String.format(LINE_PATTERN, other.key, other.value)
        }
        for (individualResource in manifestConfig.individualResources) {
            output <<= "\n"
            output <<= String.format(LINE_PATTERN, INDIVIDUAL_RESOURCE_NAME, individualResource.name)
            output <<= String.format(LINE_PATTERN, INDIVIDUAL_RESOURCE_ATG_CLIENT_UPDATE_FILE, individualResource.atgClientUpdateFile)
            if (individualResource.atgAssemblerImportFile) {
                output <<= String.format(LINE_PATTERN, INDIVIDUAL_RESOURCE_ATG_ASSEMBLER_IMPORT_FILE, individualResource.atgAssemblerImportFile)
            }
        }
        outputFile.write(output.toString())
    }


    private static String printLongList(Collection<String> list, int factor) {
        StringBuilder output = StringBuilder.newInstance()
        def iterator = list.iterator()
        int i = 0;
        while (iterator.hasNext()) {
            output <<= iterator.next()
            i++;
            if (iterator.hasNext()) {
                if (i % factor == 0) {
                    output <<= "\n"
                    output <<= "  "
                } else {
                    output <<= " "
                }
            }
        }
        return output.toString()
    }

}