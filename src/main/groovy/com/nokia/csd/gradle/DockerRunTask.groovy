package com.nokia.csd.gradle

import org.gradle.api.tasks.TaskAction

class DockerRunTask extends AbstractDockerTask {

  @TaskAction
  protected void exec() {
    super.args(['run', '-d', '-p', getPortMapping()] + getLinkedMicroservices()
        + ['--name=' + getContainerName(), getContainerName()])
    println 'Running container: ' + getContainerName() + ' args: ' + super.getArgs()
    super.exec()
  }
}
