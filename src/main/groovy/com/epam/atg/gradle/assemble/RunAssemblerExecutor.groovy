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

import com.epam.atg.gradle.utils.OsUtils
import org.gradle.api.Project

/**
 * Executes the runAssembler utility
 */
class RunAssemblerExecutor {

    private static final String ATGJRE = 'ATGJRE'
    private static final String JRE_HOME = 'JRE_HOME'
    private static final String JAVA_HOME = 'JAVA_HOME'
    private static final String ATG_HOME = 'ATG_HOME'
    private static final String DYNAMO_HOME = 'DYNAMO_HOME'

    void exec(Project project, String atgRootDir, List<String> arguments) {
        String cliExecutable = getExecutable(atgRootDir, project, arguments)
        ProcessBuilder processBuilder = createProcessBuilder(cliExecutable, arguments)
        prepareEnvironment(processBuilder, project)

        Process p = processBuilder.start()
        inheritIO(p.inputStream, System.out)
        inheritIO(p.errorStream, System.err)
        p.waitFor()
    }

    private static String getExecutable(String atgRootDir, Project project, List<String> arguments) {
        String cliExecutable = "$atgRootDir/home/bin/runAssembler"
        if (OsUtils.isWindows()) {
            cliExecutable += '.bat'
        }
        cliExecutable = new File(cliExecutable).absolutePath
        project.logger.quiet "Executing: ${cliExecutable} ${arguments.join(' ')}"
        cliExecutable
    }

    private static void prepareEnvironment(ProcessBuilder processBuilder, Project project) {
        Map<String, String> environment = processBuilder.environment()
        if (!System.getenv(ATGJRE)) {
            if (System.getenv(JRE_HOME)) {
                environment.put(ATGJRE, new File((System.env.JRE_HOME as String) + '/bin/java').absolutePath)
            } else if (System.getenv(JAVA_HOME)) {
                environment.put(ATGJRE, new File((System.env.JAVA_HOME as String) + '/bin/java').absolutePath)
            } else {
                project.logger.warn('{}, {} or {} environment variable required!', ATGJRE, JRE_HOME, JAVA_HOME)
            }
        }
        environment.put(ATG_HOME, new File(project.atgRoot as String).absolutePath)
        environment.put(DYNAMO_HOME, new File((project.atgRoot as String) + '/home').absolutePath)
        project.logger.debug('RunAssemblerExecutor: environment = {}', environment)
    }

    private static ProcessBuilder createProcessBuilder(String cliExecutable, List<String> arguments) {
        ProcessBuilder processBuilder
        StringBuilder sb = new StringBuilder(cliExecutable)
        for (String arg in arguments) {
            sb.append(' ').append(arg)
        }
        if (OsUtils.isWindows()) {
            processBuilder = new ProcessBuilder('cmd.exe', '/C', sb.toString())
        } else {
            processBuilder = new ProcessBuilder('/bin/bash', '-c', sb.toString())
        }
        return processBuilder
    }

    private static void inheritIO(InputStream src, PrintStream dest) {
        new Thread(new Runnable() {
            void run() {
                Scanner sc = new Scanner(src)
                while (sc.hasNextLine()) {
                    dest.println(sc.nextLine())
                }
            }
        }).start()
    }
}
