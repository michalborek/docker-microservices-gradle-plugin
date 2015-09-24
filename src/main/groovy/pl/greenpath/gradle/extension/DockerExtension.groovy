package pl.greenpath.gradle.extension

import org.gradle.api.Project

class DockerExtension {

  private String fixedRootProjectPath = '/project'
  private String containerName
  private String executable
  private String imageName
  private List<String> linkedMicroservices = []
  private int port
  private boolean removeVolumes = true
  private boolean runDetached = true
  private List<String> runExtraArgs = []
  private DockerfileDeclaration dockerfile
  private boolean generateDockerfile = true
  private boolean mapProjectPathsToFixedRoot = false
  private Project project
  private DockerRunExtension runExtension = new DockerRunExtension()

  DockerExtension(Project project) {
    this.project = project
    executable 'docker'
    dockerfile = new DockerfileDeclaration(project)
  }

  void fixedRootProjectPath(String path) {
    fixedRootProjectPath = path
  }

  /**
   * Defines a name wich will be bound to created container during dockerRun task execution.
   * @param containerName
   */
  void containerName(String containerName) {
    this.containerName = containerName
  }

  /**
   * Defines an executable of docker (by default 'docker').
   *
   * On some platforms it may be needed to use different executable
   * (e.g. when you use Windows and you cannot map boot2docker ip in env variables.
   *
   * @param newExecutable
   */
  void executable(String newExecutable) {
    this.executable = newExecutable
  }

  /**
   * Defines a name of an image that is created using dockerImage.
   *
   * Additionally it is used by docker run to create a container based on image with that name.
   *
   * By default the image name is equal to gradle project name.
   * @param imageName
   */
  void imageName(String imageName) {
    this.imageName = imageName
  }

  /**
   * Defines a microservices that this microservice is dependent on.
   *
   * Such microservices are always run before current one.
   * Additionally during "run" method invocation they are
   * linked using "--link:msName:msName" parameter.
   * @param linkedMicroservices
   */
  void linkedMicroservices(String... linkedMicroservices) {
    this.linkedMicroservices = linkedMicroservices.toList()
  }

  /**
   * Defines a port on which microservice is available.
   *
   * It is equivalent to using parameter "-p port:port" in "docker run" command
   * @param port a port which will be exposed
   */
  void port(int port) {
    this.port = port
    dockerfile.expose port
  }

  void publishPort(int hostPort, int containerPort) {
    addDockerRunArgs('-p', "${hostPort}:${containerPort}")
  }

  String getProjectDirPathOnDockerHost() {
    URI rootProjectDirURI = project.rootProject.projectDir.toURI()
    URI projectDirURI = project.projectDir.toURI()
    String relativeProjectDirPath = rootProjectDirURI.relativize(projectDirURI)

    if (relativeProjectDirPath.isEmpty())
      return fixedRootProjectPath

    fixedRootProjectPath + '/' + relativeProjectDirPath
  }

  String replaceMarkerWithProjectPathMappedToFixedRoot(String pathWithMarker) {
    String rootProjectDirMarker = '%rootProjectDir%'
    String projectDirMarker = '%projectDir%'

    if (pathWithMarker.contains(rootProjectDirMarker)) {
      return pathWithMarker.replaceFirst(rootProjectDirMarker, fixedRootProjectPath)
    } else if (pathWithMarker.contains(projectDirMarker)) {
      return pathWithMarker.replaceFirst(projectDirMarker, getProjectDirPathOnDockerHost())
    }

    pathWithMarker
  }

  String replaceMarkerWithRealPath(String pathWithMarker) {
    String rootProjectDirMarker = '%rootProjectDir%'
    String projectDirMarker = '%projectDir%'

    if (pathWithMarker.contains(rootProjectDirMarker)) {
      return pathWithMarker.replaceFirst(rootProjectDirMarker, project.rootProject.projectDir.toString())
    } else if (pathWithMarker.contains(projectDirMarker)) {
      return pathWithMarker.replaceFirst(projectDirMarker, project.projectDir.toString())
    }

    pathWithMarker
  }

  void bindMount(String srcPath, String dstPath) {
    def srcPathWithoutMarker
    if (mapProjectPathsToFixedRoot) {
      srcPathWithoutMarker = replaceMarkerWithProjectPathMappedToFixedRoot(srcPath)
    } else {
      srcPathWithoutMarker = replaceMarkerWithRealPath(srcPath)
    }
    addDockerRunArgs('-v', "${srcPathWithoutMarker}:${dstPath}")
  }

  /**
   * Defines whether volumes should be deleted when container is removed.
   *
   * This option is turned on by default.
   * @param removeVolumes
   */
  void removeVolumes(boolean removeVolumes) {
    this.removeVolumes = removeVolumes
  }

  void runDetached(boolean detached) {
    this.runDetached = detached
  }

  void generateDockerfile(boolean generate) {
    this.generateDockerfile = generate
  }

  void mapProjectPathsToFixedRoot(boolean shouldMap) {
    this.mapProjectPathsToFixedRoot = shouldMap
  }

  /**
   * Defines extra arguments that are attached to default ones on 'docker run'
   * command execution.
   * @param extraArgs
   */
  void runExtraArgs(String... extraArgs) {
    println('runExtraArgs is deprecated. Instead use run { extraArgs "arg1", "arg2" }')
    this.runExtraArgs = extraArgs.toList()
  }

  void addDockerRunArgs(String... additionalArgs) {
    this.runExtraArgs.addAll(additionalArgs)
  }

  String getFixedRootProjectPath() {
    return fixedRootProjectPath
  }

  String getContainerName() {
    return containerName
  }

  String getExecutable() {
    return executable
  }

  String getImageName() {
    return imageName
  }

  boolean shouldGenerateDockerfile() {
    return generateDockerfile
  }

  List<String> getLinkedMicroservices() {
    return linkedMicroservices
  }

  List<String> getRunArguments() {
    return runExtension.getArguments();
  }

  int getPort() {
    return port
  }

  boolean getRemoveVolumes() {
    return removeVolumes
  }

  boolean getRunDetached() {
    return runDetached
  }

  List<String> getRunExtraArgs() {
    return runExtraArgs
  }

  DockerfileDeclaration getDockerfile() {
    return dockerfile
  }

  boolean getMapProjectPathsToFixedRoot() {
    return mapProjectPathsToFixedRoot
  }

  void dockerfile(Closure<DockerfileDeclaration> closure) {
    dockerfile.with closure
  }

  void dockerfile(String dockerfile) {
    this.dockerfile.stringBasedDockerfile(dockerfile)
  }

  void run(Closure<DockerRunExtension> runParameters) {
    runParameters.delegate = runExtension;
    runParameters.resolveStrategy = Closure.DELEGATE_ONLY
    runParameters();
  }

  static Closure<DockerfileDeclaration> microserviceTemplate = {
    def jarFile = "${project.name}-${project.version}.jar"
    from 'java:8'
    add jarFile, '.'
    cmd "java -jar $jarFile"
  }
}
