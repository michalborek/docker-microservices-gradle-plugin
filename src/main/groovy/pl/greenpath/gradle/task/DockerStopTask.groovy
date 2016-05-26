package pl.greenpath.gradle.task

import groovy.transform.CompileStatic

@CompileStatic
class DockerStopTask extends AbstractDockerTask {

  public DockerStopTask() {
    setIgnoreExitValue true
  }

  @Override
  protected void prepareExecution() {
    println 'Stopping container: ' + getContainerName()
    args 'stop', '--time=2', getContainerName()
  }
}
