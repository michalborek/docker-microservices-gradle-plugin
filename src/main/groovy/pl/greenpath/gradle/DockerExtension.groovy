package pl.greenpath.gradle

class DockerExtension {

  String containerName
  String executable = 'docker'
  String imageName
  List<String> linkedMicroservices = []
  int port
  boolean runDetached = true
  List<String> runExtraArgs = []

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
  }

  void runDetached(boolean detached) {
    this.runDetached = detached
  }

  /**
   * Defines extra arguments that are attached to default ones on 'docker run'
   * command execution.
   * @param extraArgs
   */
  void runExtraArgs(String... extraArgs) {
    this.runExtraArgs = extraArgs.toList()
  }
}
