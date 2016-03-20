package pl.greenpath.gradle.task.dev

import pl.greenpath.gradle.extension.DevExtension
import pl.greenpath.gradle.task.DockerRunTask

class DockerBootRunTask extends DockerRunTask {

  static final String CLASSPATH_SEPARATOR = ':'
  static final String MAIN_CLASS_NAME_PROPERTY = 'mainClassName'

  @Override
  protected String getImageName() {
    return 'java:8'
  }

  @Override
  protected void prepareExecution() {
    super.prepareExecution()
    args(['java', '-cp', classpath() + ':' + sourceClassPath(), mainClassName()])
  }

  @Override
  protected List<String> getRunExtraArgs() {
    File gradleHomeDir = project.getGradle().getGradleUserHomeDir()
    return super.runExtraArgs +
        ['-v', gradleHomeDir.absolutePath + ':' + getDevExtension().getContainerDependenciesPath()] +
        ['-v', project.getBuildDir().absolutePath + ':' + getDevExtension().getContainerBuildPath()]
  }

  private String classpath() {
    String dependenciesPath = getDevExtension().getContainerDependenciesPath()
    URI gradleHomeDir = project.getGradle().getGradleUserHomeDir().toURI()
    String result = new SourceSetFinder(project).findMainSourceSet().getRuntimeClasspath().filter {
      it.isFile()
    }.collect {
      dependenciesPath + gradleHomeDir.relativize(it.getAbsoluteFile().toURI())
    }.join(CLASSPATH_SEPARATOR)
    return result
  }

  private String sourceClassPath() {
    String buildPath = getDevExtension().getContainerBuildPath()
    String result = new SourceSetFinder(project).findMainSourceSet().getOutput().collect {
      buildPath + project.getBuildDir().toURI().relativize(it.toURI())
    }.join(CLASSPATH_SEPARATOR)
    return result
  }

  private String mainClassName() {
    if (project.hasProperty(MAIN_CLASS_NAME_PROPERTY) == null) {
      throw new IllegalStateException("MainClass not set")
    }
    return project.getProperties().get(MAIN_CLASS_NAME_PROPERTY)
  }

  private DevExtension getDevExtension() {
    getDockerExtension().getDevExtension()
  }
}
