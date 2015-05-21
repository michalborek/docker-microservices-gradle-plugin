package com.nokia.csd.gradle

import org.gradle.api.tasks.Exec

abstract class AbstractDockerTask extends Exec {

  public static final String GROUP_NAME = 'docker'

  protected AbstractDockerTask() {
    executable 'docker'
    setGroup(GROUP_NAME)
  }

  protected String getContainerName() {
    def containerName = project.extensions.docker.containerName;
    return containerName ?: project.name.replaceAll('/', '-')
  }

  protected String getImageName() {
    def imageName = project.extensions.docker.imageName;
    return imageName ?: project.name.replaceAll('/', '-')
  }

  protected List<String> getLinkedMicroservices() {
    return project.extensions.docker.linkedMicroservices.collect {
      def linkName = it.replaceAll('/', '-')
      "--link=$linkName:$linkName"
    }
  }

  protected String getPortMapping() {
    def port = project.extensions.docker.port
    return "$port:$port"
  }

}
