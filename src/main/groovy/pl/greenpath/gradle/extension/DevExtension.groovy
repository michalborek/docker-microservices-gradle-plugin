package pl.greenpath.gradle.extension;

class DevExtension {

  private String containerDependenciesPath = '/dependencies/'

  private String containerProjectPath = '/project/'

  private boolean hotSwapEnabled

  void containerDependenciesDir(String containerDependenciesDir) {
    this.containerDependenciesPath = attachTrailingSlash(containerDependenciesDir)
  }

  void containerProjectDir(String containerProjectDir) {
    this.containerProjectPath = attachTrailingSlash(containerProjectDir)
  }

  String getContainerDependenciesPath() {
    return containerDependenciesPath
  }

  String getContainerProjectPath() {
    return containerProjectPath
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
