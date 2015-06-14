package pl.greenpath.gradle

class DockerRemoveContainerTask extends AbstractDockerTask {

  public DockerRemoveContainerTask() {
    setIgnoreExitValue true
  }

  @Override
  protected void prepareExecution() {
    super.args (['rm'] + (project.extensions.docker.removeVolumes ? ['-v'] : []) + [getContainerName()])
    println 'Removing container: ' + getContainerName()
  }
}
