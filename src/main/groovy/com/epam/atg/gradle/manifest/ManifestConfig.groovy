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

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.util.ConfigureUtil

class ManifestConfig implements Serializable {

    @Input @Optional String manifestVersion
    @Input @Optional String antVersion
    @Input @Optional String atgJ2ee
    @Input @Optional String atgEarModule
    @Input @Optional String atgProduct
    @Input @Optional String createdBy
    @Input Set<IndividualResource> individualResources = []
    @Input Set<String> atgClientClassPath = []
    @Input Set<String> atgClassPath = []
    @Input Set<String> atgRequired = []
    @Input Set<String> atgRequiredIfPresent = []
    @Input @Optional String atgConfigPath
    @Input Set<String> projectClassPath = []
    @Input boolean generateAtgClientClassPath = false
    @Input boolean generateIndividualResources = false
    @Input boolean skipGeneration = true
    @Input boolean override = false
    @Input String manifestFilePath = "META-INF/MANIFEST.MF.new"
    @Input Map others = [:]


    void individualResources(Closure configuration) {
        individualResources.add(ConfigureUtil.configure(configuration, new IndividualResource()))
    }

    void manifestVersion(String manifestVersion) {
        this.manifestVersion = manifestVersion
    }

    void antVersion(String antVersion) {
        this.antVersion = antVersion
    }

    void atgJ2ee(String atgJ2ee) {
        this.atgJ2ee = atgJ2ee
    }

    void atgEarModule(String atgEarModule) {
        this.atgEarModule = atgEarModule
    }

    void atgProduct(String atgProduct) {
        this.atgProduct = atgProduct
    }

    void createdBy(String createdBy) {
        this.createdBy = createdBy
    }

    void atgClientClassPath(Set<String> atgClientClassPath) {
        this.atgClientClassPath = atgClientClassPath
    }

    void atgClassPath(Set<String> atgClassPath) {
        this.atgClassPath = atgClassPath
    }

    void atgRequired(Set<String> atgRequired) {
        this.atgRequired = atgRequired
    }

    void atgRequiredIfPresent(Set<String> atgRequiredIfPresent) {
        this.atgRequiredIfPresent = atgRequiredIfPresent
    }

    void atgConfigPath(String atgConfigPath) {
        this.atgConfigPath = atgConfigPath
    }

    void projectClassPath(Set<String> projectClassPath) {
        this.projectClassPath = projectClassPath
    }

    void generateAtgClientClassPath(boolean generateAtgClientClassPath) {
        this.generateAtgClientClassPath = generateAtgClientClassPath
    }

    void generateIndividualResources(boolean generateIndividualResources) {
        this.generateIndividualResources = generateIndividualResources
    }

    void skipGeneration(boolean skipGeneration) {
        this.skipGeneration = skipGeneration
    }

    void override(boolean override) {
        this.override = override
    }

    void manifestFilePath(String manifestFilePath) {
        this.manifestFilePath = manifestFilePath
    }

    void others(Map others) {
        this.others = others
    }

    @Override
    public String toString() {
        return "ManifestConfig{" +
                "manifestVersion='" + manifestVersion + '\'' +
                ", antVersion='" + antVersion + '\'' +
                ", atgJ2ee='" + atgJ2ee + '\'' +
                ", atgEarModule='" + atgEarModule + '\'' +
                ", atgProduct=" + atgProduct +
                ", createdBy='" + createdBy + '\'' +
                ", atgClientClassPath='" + atgClientClassPath + '\'' +
                ", individualResources='" + individualResources + '\'' +
                ", atgClassPath=" + atgClassPath +
                ", atgRequired=" + atgRequired +
                ", atgRequiredIfPresent=" + atgRequiredIfPresent +
                ", atgConfigPath='" + atgConfigPath + '\'' +
                ", projectClassPath='" + projectClassPath + '\'' +
                ", generateAtgClientClassPath=" + generateAtgClientClassPath +
                ", generateIndividualResources=" + generateIndividualResources +
                ", manifestFilePath=" + manifestFilePath +
                ", skipGeneration=" + skipGeneration +
                ", override=" + override +
                ", others=" + others +
                '}';
    }
}
