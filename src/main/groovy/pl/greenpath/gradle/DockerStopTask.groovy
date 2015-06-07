package pl.greenpath.gradle

class DockerStopTask extends AbstractDockerTask {

  public DockerStopTask() {
    setIgnoreExitValue(true)
  }

  @Override
  protected void prepareExecution() {
    println 'Stopping container: ' + getContainerName()
    super.args 'stop', '--time=2', getContainerName()
  }
}
