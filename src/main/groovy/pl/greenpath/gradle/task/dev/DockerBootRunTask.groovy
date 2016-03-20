package pl.greenpath.gradle.task.dev

import org.gradle.api.artifacts.component.ModuleComponentIdentifier
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
    String classPath = dependenciesClassPath() + CLASSPATH_SEPARATOR + sourceClassPath() + CLASSPATH_SEPARATOR + resourcesPath()
    args(['java'])
    String springLoadedFile = findSpringloaded()
    if (springLoadedFile != null && getDevExtension().isHotSwapEnabled()) {
      args(['-javaagent:' + springLoadedFile, '-noverify'])
    }
    args(['-cp', classPath, mainClassName()])
  }

  private String findSpringloaded() {
    File gradleHomeDir = project.getGradle().getGradleUserHomeDir()
    String path = getDevExtension().getContainerDependenciesPath()
    List<String> springLoadedFiles = project.buildscript.configurations.
        getByName('classpath').resolvedConfiguration.resolvedArtifacts.find {
      if (!(it.getId().getComponentIdentifier() instanceof ModuleComponentIdentifier)) {
        return false
      }
      ModuleComponentIdentifier identifier = it.getId().getComponentIdentifier() as ModuleComponentIdentifier
      return identifier.getGroup() == 'org.springframework' && identifier.getModule() == 'springloaded'
    }.collect {
      DockerBootRunTask.switchToRelative(gradleHomeDir, path, it.getFile())
    }
    return springLoadedFiles.isEmpty() ? null : springLoadedFiles.first()
  }

  @Override
  protected List<String> getRunExtraArgs() {
    File gradleHomeDir = project.getGradle().getGradleUserHomeDir()

    return super.runExtraArgs +
        ['-v', gradleHomeDir.absolutePath + ':' + getDevExtension().getContainerDependenciesPath()] +
        ['-v', project.getRootDir().absolutePath + ':' + getDevExtension().getContainerProjectPath()]
  }

  private String dependenciesClassPath() {
    File gradleHomeDir = project.getGradle().getGradleUserHomeDir()
    String containerPath = getDevExtension().getContainerDependenciesPath()
    String result = new SourceSetFinder(project).findMainSourceSet().runtimeClasspath.filter {
      it.isFile()
    }.collect {
      DockerBootRunTask.switchToRelative(gradleHomeDir, containerPath, it)
    }.join(CLASSPATH_SEPARATOR)
    return result
  }

  private String sourceClassPath() {
    String projectPath = getDevExtension().getContainerProjectPath()
    File classesDir = new SourceSetFinder(project).findMainSourceSet().output.classesDir
    return switchToRelative(project.getRootDir(), projectPath, classesDir)
  }

  private String resourcesPath() {
    String projectPath = getDevExtension().getContainerProjectPath()
    Set<File> resourcesDirs = new SourceSetFinder(project).findMainSourceSet().getResources().srcDirs

    return resourcesDirs.collect {
      DockerBootRunTask.switchToRelative(project.getRootDir(), projectPath, it)
    }.join(CLASSPATH_SEPARATOR)
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

  public static String switchToRelative(File relativeTo, String destinationPath, File file) {
    return destinationPath + relativeTo.toURI().relativize(file.getAbsoluteFile().toURI())
  }

}
