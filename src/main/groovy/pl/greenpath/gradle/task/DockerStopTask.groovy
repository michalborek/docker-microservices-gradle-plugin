package pl.greenpath.gradle.task

class DockerStopTask extends AbstractDockerTask {

  public DockerStopTask() {
    ignoreExitValue true
  }

  @Override
  protected void prepareExecution() {
    println 'Stopping container: ' + getContainerName()
    args 'stop', '--time=2', getContainerName()
  }
}
