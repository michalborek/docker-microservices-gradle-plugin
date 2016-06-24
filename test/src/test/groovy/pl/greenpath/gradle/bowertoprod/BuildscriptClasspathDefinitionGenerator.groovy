package pl.greenpath.gradle.bowertoprod;

class BuildscriptClasspathDefinitionGenerator {

  static String generateBuildscriptClasspathDefinition() {
    return """
        buildscript {
            dependencies {
                classpath files(${setupPluginClasspath()})
            }
        }
    """
  }

  private static String setupPluginClasspath() {
    def pluginClasspathResource = BuildscriptClasspathDefinitionGenerator.classLoader.findResource('plugin-classpath.txt')
    if (pluginClasspathResource == null) {
      throw new IllegalStateException('Did not find plugin classpath resource, run `testClasses` build task.')
    }

    return pluginClasspathResource.readLines()
        .collect { it.replace('\\', '\\\\') } // escape backslashes in Windows paths
        .collect { "'$it'" }
        .join(', ')
  }
}
