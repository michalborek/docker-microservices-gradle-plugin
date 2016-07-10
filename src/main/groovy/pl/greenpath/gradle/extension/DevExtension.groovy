package pl.greenpath.gradle.extension

import groovy.transform.CompileStatic;

@CompileStatic
class DevExtension {

  private String containerDependenciesPath = '/dependencies/'

  private String containerProjectPath = '/project/'

  private String hostDependenciesPath

  private String hostRootProjectPath

  private String baseImageName = 'java:8'

  private boolean hotSwapEnabled

  void containerDependenciesDir(String containerDependenciesDir) {
    this.containerDependenciesPath = attachTrailingSlash(containerDependenciesDir)
  }

  void containerProjectDir(String containerProjectDir) {
    this.containerProjectPath = attachTrailingSlash(containerProjectDir)
  }

  void hostRootProjectDir(String dir) {
    this.hostRootProjectPath = dir
  }

  void hostDependenciesDir(String dir) {
    this.hostDependenciesPath = dir
  }

  void imageName(String name) {
    this.baseImageName = name
  }


  String getContainerDependenciesPath() {
    return containerDependenciesPath
  }

  String getContainerProjectPath() {
    return containerProjectPath
  }

  String getHostDependenciesPath() {
    return hostDependenciesPath
  }

  String getHostRootProjectPath() {
    return hostRootProjectPath
  }

  String getImageName() {
    return baseImageName
  }

  boolean isHotSwapEnabled() {
    return hotSwapEnabled
  }

  void enableHotSwap(boolean enable = true) {
    hotSwapEnabled = enable
  }

  private static String attachTrailingSlash(String value) {
    return !value.endsWith('/') ? value + '/' : value
  }
}
