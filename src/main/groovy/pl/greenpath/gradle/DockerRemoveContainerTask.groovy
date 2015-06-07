package pl.greenpath.gradle

class DockerRemoveContainerTask extends AbstractDockerTask {

  public DockerRemoveContainerTask() {
    setIgnoreExitValue true
  }

  @Override
  protected void prepareExecution() {
    super.args 'rm', getContainerName()
    println 'Removing container: ' + getContainerName()
  }
}
