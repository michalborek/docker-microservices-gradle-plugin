package com.nokia.csd.gradle

import org.gradle.api.tasks.TaskAction

class DockerRemoveContainerTask extends AbstractDockerTask {

  public DockerRemoveContainerTask() {
    setIgnoreExitValue true
  }

  @TaskAction
  protected void exec() {
    println 'Removing container: ' + getContainerName()
    super.args 'rm', getContainerName()
    super.exec()
  }
}
