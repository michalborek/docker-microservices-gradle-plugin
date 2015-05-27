package pl.greenpath.gradle

class DockerExtension {
  List<String> linkedMicroservices = []
  int port
  String containerName
  String imageName
  List<String> runExtraArgs = []
  boolean runDetached = true
}
