package pl.greenpath.gradle.extension;

class DevExtension {

  private String containerDependenciesPath = '/dependencies/'

  private String containerBuildPath = '/build/'

  void containerDependenciesDir(String containerDependenciesDir) {
    this.containerDependenciesPath = attachTrailingSlash(containerDependenciesDir)
  }

  void containerBuildDir(String containerBuildDir) {
    this.containerBuildPath = attachTrailingSlash(containerBuildDir)
  }

  String getContainerDependenciesPath() {
    return containerDependenciesPath
  }

  String getContainerBuildPath() {
    return containerBuildPath
  }

  private static String attachTrailingSlash(String value) {
    return !value.endsWith('/') ? value + '/' : value
  }
}
