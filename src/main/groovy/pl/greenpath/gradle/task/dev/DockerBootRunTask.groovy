package pl.greenpath.gradle.task.dev

import groovy.transform.CompileStatic
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import pl.greenpath.gradle.extension.DevExtension
import pl.greenpath.gradle.task.DockerRunTask

// TODO this class is just a proof of concept. Rewrite it!
@CompileStatic
class DockerBootRunTask extends DockerRunTask {

  static final String CLASSPATH_SEPARATOR = ':'

  static final String MAIN_CLASS_NAME_PROPERTY = 'mainClassName'
  public static final int MAX_ARGUMENT_SIZE = 4 * 1024

  @Override
  protected String getImageName() {
    return 'java:8'
  }

  @Override
  protected void prepareExecution() {
    super.prepareExecution()
    args(['sh', '-c', "java \${MS_JAVA_OPTS} ${getJavaAgentDefinition()} -cp ${getClasspathVars()} ${mainClassName()}"])
  }

  private String getJavaAgentDefinition() {
    String springLoadedFile = findSpringLoaded()
    if (!(springLoadedFile != null && getDevExtension().isHotSwapEnabled())) {
      return ''
    }
    return '-javaagent:' + springLoadedFile + ' -noverify '
  }

  private List<String> getClasspathEnvArgs() {
    List<List<String>> classPathPartitioned = getClassPathPartitioned()
    List<String> envArgs = []
    for (int i = 0; i < classPathPartitioned.size(); i++) {
      String classpathPart = classPathPartitioned[i].join(CLASSPATH_SEPARATOR)
      envArgs << '-e' << "CP${i}=${classpathPart}"
    }
    return envArgs
  }

  private String getClasspathVars() {
    List<List<String>> classPathPartitioned = getClassPathPartitioned()
    List<String> classPathVariables = []
    for (int i = 0; i < classPathPartitioned.size(); i++) {
      classPathVariables << "\${CP${i}}"
    }
    return classPathVariables.join(CLASSPATH_SEPARATOR)
  }

  private List<List<String>> getClassPathPartitioned() {
    List<String> classPath = sourceClassPath() + resourcesPath() + dependenciesClassPath()
    return collateClasspath(classPath)
  }

  public static List<List<String>> collateClasspath(List<String> list) {
    long argumentLength = list.collect { it.length() }.sum() + list.size()
    if (argumentLength < MAX_ARGUMENT_SIZE) {
      return [list]
    }
    int collateFactor = Math.floor((double) list.size() / ((double) argumentLength / (double) MAX_ARGUMENT_SIZE))
    List<List<String>> result = []
    list.collate(collateFactor).collect {
      collateClasspath(it)
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
      ResolvedArtifact resolvedArtifact = it as ResolvedArtifact
      switchToRelative(gradleHomeDir, dependenciesPath, resolvedArtifact.getFile())
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
    return getDevExtension().getHostRootProjectPath() ?: project.getRootDir().absolutePath
  }

  private String getGradleHomeDir() {
    return getDevExtension().getHostDependenciesPath() ?: project.getGradle().getGradleUserHomeDir().absolutePath
  }

  private List<String> dependenciesClassPath() {
    File gradleHomeDir = project.getGradle().getGradleUserHomeDir()
    String projectPath = getDevExtension().getContainerProjectPath()
    String containerPath = getDevExtension().getContainerDependenciesPath()
    return new SourceSetFinder(project).findMainSourceSet().runtimeClasspath.filter {
      it.isFile()
    }.collect {
      if ((it as File).absolutePath.startsWith(gradleHomeDir.absolutePath)) {
        switchToRelative(gradleHomeDir, containerPath, it)
      } else {
        switchToRelative(project.getRootDir(), projectPath, it)
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
      switchToRelative(project.getRootDir(), projectPath, it)
    }
  }

  private String mainClassName() {
    if (!project.hasProperty(MAIN_CLASS_NAME_PROPERTY)) {
      throw new IllegalStateException('MainClass not set')
    }
    return project.getProperties().get(MAIN_CLASS_NAME_PROPERTY)
  }

  private DevExtension getDevExtension() {
    return getDockerExtension().getDevExtension()
  }

  public static String switchToRelative(File relativeTo, String destinationPath, File file) {
    return destinationPath + relativeTo.toURI().relativize(file.getAbsoluteFile().toURI())
  }

}
