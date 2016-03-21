package pl.greenpath.gradle.task.dev

import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import pl.greenpath.gradle.extension.DevExtension
import pl.greenpath.gradle.task.DockerRunTask

class DockerBootRunTask extends DockerRunTask {

  static final String CLASSPATH_SEPARATOR = ':'

  static final String MAIN_CLASS_NAME_PROPERTY = 'mainClassName'
  public static final int MAX_ARGUMENT_SIZE = 8192
  public static final String PROJECT_CLASSPATH = 'PROJECT_CLASSPATH'

  @Override
  protected String getImageName() {
    return 'java:8'
  }

  @Override
  protected void prepareExecution() {
    super.prepareExecution()
    args(['sh', '-c', constructJavaCommand()])
  }

  private String constructJavaCommand() {
    String javaCommand = 'java ${MS_JAVA_OPTS} '
    String springLoadedFile = findSpringLoaded()
    if (springLoadedFile != null && getDevExtension().isHotSwapEnabled()) {
      javaCommand += '-javaagent:' + springLoadedFile + ' -noverify '
    }
    return javaCommand + "-cp \${$PROJECT_CLASSPATH} " + mainClassName()
  }

  private List<String> getClasspathEnvArgs() {
    LinkedList<String> classPath = sourceClassPath() + resourcesPath() + dependenciesClassPath() +
        sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath() + sourceClassPath() + resourcesPath() + dependenciesClassPath()
    List<List<String>> classPathsPartitioned = collateClasspath(classPath)
    List<String> envArgs = []
    List<String> classPathVariables = []
    for (int i = 0; i < classPathsPartitioned.size(); i++) {
      String classpathPart = classPathsPartitioned[i].join(CLASSPATH_SEPARATOR)
      envArgs << '-e' << "CP$i=$classpathPart"
      classPathVariables << "\${CP${i}}"
    }
    envArgs << '-e' << "$PROJECT_CLASSPATH=${classPathVariables.join(CLASSPATH_SEPARATOR)}"
    return envArgs
  }

  public static List<List<String>> collateClasspath(List<String> list) {
    long sum = list.collect { it.length() }.sum() + list.size()
    if (sum < MAX_ARGUMENT_SIZE) {
      return [list]
    }
    int collateFactor = Math.floor((double) list.size() / ((double) sum / (double) MAX_ARGUMENT_SIZE))
    List<List<List<String>>> result = []
    list.collate(collateFactor).collect {
      DockerBootRunTask.collateClasspath(it)
    }.forEach {
      result.addAll(it)
    }
    return result
  }


  private String findSpringLoaded() {
    File gradleHomeDir = project.getGradle().getGradleUserHomeDir()
    String dependenciesPath = getDevExtension().getContainerDependenciesPath()
    List<String> springLoadedFiles = project.buildscript.configurations.
        getByName('classpath').resolvedConfiguration.resolvedArtifacts.find {
      if (!(it.getId().getComponentIdentifier() instanceof ModuleComponentIdentifier)) {
        return false
      }
      ModuleComponentIdentifier identifier = it.getId().getComponentIdentifier() as ModuleComponentIdentifier
      return identifier.getGroup() == 'org.springframework' && identifier.getModule() == 'springloaded'
    }.collect {
      DockerBootRunTask.switchToRelative(gradleHomeDir, dependenciesPath, it.getFile())
    }
    return springLoadedFiles.isEmpty() ? null : springLoadedFiles.first()
  }

  @Override
  protected List<String> getRunExtraArgs() {
    return super.runExtraArgs +
        ['-v', getGradleHomeDir() + ':' + getDevExtension().getContainerDependenciesPath()] +
        ['-v', getProjectDir() + ':' + getDevExtension().getContainerProjectPath()] +
        getClasspathEnvArgs()
  }

  private String getProjectDir() {
    getDevExtension().getHostRootProjectPath() ?: project.getRootDir().absolutePath
  }

  private String getGradleHomeDir() {
    getDevExtension().getHostDependenciesPath() ?: project.getGradle().getGradleUserHomeDir().absolutePath
  }

  private List<String> dependenciesClassPath() {
    File gradleHomeDir = project.getGradle().getGradleUserHomeDir()
    String projectPath = getDevExtension().getContainerProjectPath()
    String containerPath = getDevExtension().getContainerDependenciesPath()
    return new SourceSetFinder(project).findMainSourceSet().runtimeClasspath.filter {
      it.isFile()
    }.collect {
      if (it.absolutePath.startsWith(gradleHomeDir.absolutePath)) {
        DockerBootRunTask.switchToRelative(gradleHomeDir, containerPath, it)
      } else {
        DockerBootRunTask.switchToRelative(project.getRootDir(), projectPath, it)
      }
    }
  }

  private List<String> sourceClassPath() {
    String projectPath = getDevExtension().getContainerProjectPath()
    File classesDir = new SourceSetFinder(project).findMainSourceSet().output.classesDir
    return [switchToRelative(project.getRootDir(), projectPath, classesDir)]
  }

  private List<String> resourcesPath() {
    String projectPath = getDevExtension().getContainerProjectPath()
    Set<File> resourcesDirs = new SourceSetFinder(project).findMainSourceSet().getResources().srcDirs

    return resourcesDirs.collect {
      DockerBootRunTask.switchToRelative(project.getRootDir(), projectPath, it)
    }
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
