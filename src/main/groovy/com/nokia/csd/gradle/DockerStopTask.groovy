package com.nokia.csd.gradle

import org.gradle.api.tasks.TaskAction

class DockerStopTask extends AbstractDockerTask {

  public DockerStopTask() {
    super()
    setIgnoreExitValue(true)
  }

  @TaskAction
  protected void exec() {
    println 'Stopping container: ' + getContainerName()
    super.args 'stop', '--time=2', getContainerName()
    super.exec()
  }
}
