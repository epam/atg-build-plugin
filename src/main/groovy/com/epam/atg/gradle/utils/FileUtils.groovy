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

package com.epam.atg.gradle.utils

class FileUtils {
    static void createLink(File link, File existingFile) {
        ProcessBuilder processBuilder
        if(OsUtils.isWindows()) {
            def extraFlag = existingFile.isDirectory() ? '/J' : ''
            def command = "mklink $extraFlag \"$link.absolutePath\" \"$existingFile.absolutePath\""
            processBuilder = new ProcessBuilder('cmd.exe', '/C', command)
        } else {
            def command = "ln -s \"$existingFile.absolutePath\" \"$link.absolutePath\""
            processBuilder = new ProcessBuilder('/bin/bash', '-c', command)
        }
        Process p = processBuilder.start()
        p.waitFor()
    }
}
