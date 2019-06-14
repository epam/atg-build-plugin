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

package com.epam.atg.gradle.build.utils

import com.epam.atg.gradle.build.repository.ATGRepository

class ATGModuleTreePrinter {

    ATGRepository repository

    ATGModuleTreePrinter(ATGRepository repository) {
        this.repository = repository
    }

    void printDependencies(String moduleName) {
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(System.out))
        printDependencies(moduleName, printWriter)
    }

    void printDependencies(String moduleName, PrintWriter printWriter) {
        printTree(moduleName, 0, printWriter)
        printWriter.flush()
    }

    private void printTree(String moduleName, int offset, PrintWriter printWriter) {
        printWriter.print('|   ' * offset + '+---')
        printWriter.println(moduleName)
        List<String> dependencies = repository.getDependencies(moduleName)
        if (dependencies.isEmpty()) {
            return
        }
        for (String subModule : dependencies) {
            printTree(subModule, offset + 1, printWriter)
        }
    }

}