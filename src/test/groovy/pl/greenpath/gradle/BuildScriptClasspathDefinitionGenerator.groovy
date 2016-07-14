package pl.greenpath.gradle

import groovy.transform.CompileStatic

@CompileStatic
class BuildScriptClasspathDefinitionGenerator {

  static String generateBuildScriptClasspathDefinition() {
    return """
        buildscript {
            dependencies {
                classpath files(${setupPluginClasspath()})
            }
        }
    """
  }

  private static String setupPluginClasspath() {
    URL classpathResource = BuildScriptClasspathDefinitionGenerator.classLoader.getResource('plugin-classpath.txt')
    if (classpathResource == null) {
      throw new IllegalStateException('Did not find plugin classpath resource, run `testClasses` testApp.build task.')
    }

    return classpathResource.readLines()
        .collect { it.replace('\\', '\\\\') } // escape backslashes in Windows paths
        .collect { "'$it'" }
        .join(', ')
  }
}
