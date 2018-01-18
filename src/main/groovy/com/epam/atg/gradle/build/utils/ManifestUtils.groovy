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

package com.epam.atg.gradle.build.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.jar.Manifest

class ManifestUtils {
    public static final String META_INF_DIR_NAME = "META-INF"
    public static final String MANIFEST_FILE_NAME = "MANIFEST.MF"
    public static final String MANIFEST_ATTRIBUTE_ATG_PRODUCT_FULL = 'ATG-Product-Full'
    public static final String MANIFEST_ATTRIBUTE_ATG_PRODUCT = 'ATG-Product'
    public static final String MANIFEST_ATTRIBUTE_ATG_CONFIG_PATH = 'ATG-Config-Path'
    public static final String MANIFEST_ATTRIBUTE_ATG_REQUIRED = 'ATG-Required'
    public static final String MANIFEST_ATTRIBUTE_ATG_CLASS_PATH = 'ATG-Class-Path'
    public static final String MANIFEST_ATTRIBUTE_ATG_INSTALL_UNIT = 'ATG-Install-Unit'

    public static final String ATG_MODULE_SEPARATOR = "."
    public static final String URI_PATH_SEPARATOR = "/"

    private static Logger LOGGER = LoggerFactory.getLogger(ManifestUtils.class)

    static String getManifestPath(File moduleDirectory) {
        return moduleDirectory.getAbsolutePath() + File.separator + META_INF_DIR_NAME + File.separator + MANIFEST_FILE_NAME
    }

    static boolean isManifestExists(File moduleDirectory) {
        return new File(getManifestPath(moduleDirectory)).exists()
    }

    static Manifest findManifestInModuleDirectory(File moduleDirectory) {
        return getManifest(getManifestPath(moduleDirectory))
    }

    static Manifest getManifest(String manifestFileLocation) {
        return getManifest(new File(manifestFileLocation))
    }

    static Manifest getManifest(File manifestFile) {
        if (manifestFile.exists()) {
            InputStream is = new FileInputStream(manifestFile)
            try {
                return new Manifest(is)
            }catch (Exception e) {
                if(LOGGER.isErrorEnabled()) {
                    LOGGER.error("Unable to open manifest file: " + manifestFile.absolutePath, e)
                }
            } finally {
                is.close()
            }
        }
        return null
    }

    static List<String> getATGModuleClassPath(Manifest manifest) {
        return getListValueFromManifestMainAttributes(manifest, MANIFEST_ATTRIBUTE_ATG_CLASS_PATH)
    }

    static List<String> getATGRequiredModules(Manifest manifest) {
        return getListValueFromManifestMainAttributes(manifest, MANIFEST_ATTRIBUTE_ATG_REQUIRED)
    }

    static List<String> getListValueFromManifestMainAttributes(Manifest manifest, String mainAttributeName) {
        String attributeStringValue = getMainAttribute(manifest, mainAttributeName)
        def entries
        if (attributeStringValue) {
            entries = attributeStringValue.split(" +")
            entries = entries.collect { entry ->
                entry.trim()
            }.findAll({ !it.isEmpty() })
        } else {
            entries = new ArrayList<String>()
        }
        return entries
    }

    static String getRootModuleFromModuleName(String moduleName) {
        return convertModuleNameToModuleHierarchy(moduleName).get(0)
    }

    static List<String> convertModuleNameToModuleHierarchy(String moduleName) {
        return moduleName.split("\\.").toList()
    }

    static String convertModuleNameToRelativePath(String moduleName) {
        return moduleName.replaceAll("\\.", "\\" + File.separator)
    }

    static String convertRelativeURIPathToModuleName(URI relativeURI) {
        return convertRelativeURIPathToModuleName(relativeURI.getPath())
    }

    static String convertRelativeURIPathToModuleName(String relativeURIPath) {
        String relPath = relativeURIPath
        if (relPath.endsWith(URI_PATH_SEPARATOR))
            relPath = relPath.substring(0, relPath.length() - 1)
        String module = relPath.replaceAll(URI_PATH_SEPARATOR, ATG_MODULE_SEPARATOR)
        return module
    }

    static String getDescription(Manifest manifest) {
        def mainAttributes = manifest.getMainAttributes()
        def atgProductFull = mainAttributes.getValue(MANIFEST_ATTRIBUTE_ATG_PRODUCT_FULL)
        def atgProduct = mainAttributes.getValue(MANIFEST_ATTRIBUTE_ATG_PRODUCT)
        def projectDescriptionOverride = null
        if (atgProduct && atgProductFull) {
            projectDescriptionOverride = "$atgProduct - $atgProductFull"
        } else if (atgProductFull) {
            projectDescriptionOverride = atgProductFull
        } else if (atgProduct) {
            projectDescriptionOverride = atgProduct
        }
        return projectDescriptionOverride
    }

    static String getInstalledUnit(Manifest manifest) {
        return getMainAttribute(manifest, MANIFEST_ATTRIBUTE_ATG_INSTALL_UNIT)
    }

    static String getConfigPath(Manifest manifest) {
        return getMainAttribute(manifest, MANIFEST_ATTRIBUTE_ATG_CONFIG_PATH)
    }

    private static String getMainAttribute(Manifest manifest, String attributeName) {
        def mainAttributes = manifest.getMainAttributes()
        return mainAttributes.getValue(attributeName)
    }

}
