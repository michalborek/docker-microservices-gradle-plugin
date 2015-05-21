package com.nokia.csd.gradle

import org.gradle.api.tasks.TaskAction

class DockerInteractiveLogTask extends AbstractDockerTask {

  @TaskAction
  protected void exec() {
    println 'Starting logging: ' + getContainerName()
    super.args 'logs', '-f', getContainerName()
    super.exec()
  }
}
