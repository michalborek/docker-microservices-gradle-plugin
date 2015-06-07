package pl.greenpath.gradle

class DockerExtension {

  String containerName
  String executable = 'docker'
  String imageName
  List<String> linkedMicroservices = []
  int port
  boolean runDetached = true
  List<String> runExtraArgs = []

  def containerName(String containerName) {
    this.containerName = containerName
  }

  def executable(String newExecutable) {
    this.executable = newExecutable
  }

  def imageName(String imageName) {
    this.imageName = imageName
  }

  def linkedMicroservices(String... linkedMicroservices) {
    this.linkedMicroservices = linkedMicroservices.toList()
  }

  def port(int port) {
    this.port = port
  }

  def runDetached(boolean detached) {
    this.runDetached = detached
  }

  def runExtraArgs(String... extraArgs) {
    this.runExtraArgs = extraArgs.toList()
  }
}
