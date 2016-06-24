package pl.greenpath.gradle.bowertoprod

import org.gradle.api.Project

class BowerToProdExtension {

  Project project

  private File destinationDir

  private Map<String, LibraryDefinition> customizations = [:]

  private List<String> ignored = []

  BowerToProdExtension(Project project) {
    this.project = project
  }

  void lib(Map params) {
    customizations[params['name']] = new LibraryDefinition(params)
  }

  void ignore(String... ignoredDependencies) {
    ignored += ignoredDependencies.toList()
  }

  void destination(File destinationDir) {
    this.destinationDir = destinationDir
  }

  boolean hasCustomization(String libraryName) {
    return customizations.containsKey(libraryName)
  }

  LibraryDefinition getCustomization(String libraryName) {
    return customizations[libraryName]
  }

  File getDestination() {
    return destinationDir
  }

  String getBuildDirPath(String libraryName) {
    if (!customizations.containsKey(libraryName)) {
      return ''
    } else {
      return customizations[libraryName].buildDir ?: ''
    }
  }

  List<String> getCustomDestinations() {
    return customizations.values().collect {
      it.getDestination()
    }.findAll {
      it != null && !it.empty
    }
  }

  boolean isIgnored(String dependency) {
    return ignored.contains(dependency)
  }
}
