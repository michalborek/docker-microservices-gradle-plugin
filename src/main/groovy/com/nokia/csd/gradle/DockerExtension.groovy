package com.nokia.csd.gradle

class DockerExtension {
  List<String> linkedMicroservices = []
  int port
  String containerName
  String imageName
}
