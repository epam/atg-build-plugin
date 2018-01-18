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
  static void exec(Project project, String atgRootDir, ArrayList<String> arguments) {
    def cliExecutable = "$atgRootDir/home/bin/runAssembler"
    if(OsUtils.isWindows()) {
      cliExecutable += '.bat'
    }
    cliExecutable = new File(cliExecutable).absolutePath
    project.logger.quiet "Executing: ${cliExecutable} ${arguments.join(' ')}"

    ProcessBuilder processBuilder
    StringBuilder sb = new StringBuilder(cliExecutable)
    for(String arg in arguments) {
      sb.append(' ').append(arg)
    }
    if(OsUtils.isWindows()) {
      processBuilder = new ProcessBuilder('cmd.exe', '/C', sb.toString())
    } else {
      processBuilder = new ProcessBuilder('/bin/bash', '-c', sb.toString())
    }
    Map<String, String> environment = processBuilder.environment()
    if(!System.getenv('ATGJRE')) {
      if(System.getenv('JRE_HOME')) {
        environment.put('ATGJRE', new File((System.env.JRE_HOME as String) + '/bin/java').absolutePath)
      } else if(System.getenv('JAVA_HOME')) {
        environment.put('ATGJRE', new File((System.env.JAVA_HOME as String) + '/bin/java').absolutePath)
      } else {
        project.logger.warn('ATGJRE, JRE_HOME or JAVA_HOME must be set!')
      }
    }
    environment.put('ATG_HOME', new File(project.atgRoot as String).absolutePath)
    environment.put("DYNAMO_HOME", new File((project.atgRoot as String) + '/home').absolutePath)

    project.logger.debug("RunAssemblerExecutor: evironment = $environment")

    Process p = processBuilder.start()
    inheritIO(p.getInputStream(), System.out)
    inheritIO(p.getErrorStream(), System.err)
    p.waitFor()
  }

  private static void inheritIO(final InputStream src, final PrintStream dest) {
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
