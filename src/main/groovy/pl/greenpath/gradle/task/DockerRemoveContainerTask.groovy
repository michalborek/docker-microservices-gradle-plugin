package pl.greenpath.gradle.task

class DockerRemoveContainerTask extends AbstractDockerTask {

  public DockerRemoveContainerTask() {
    setIgnoreExitValue true
  }

  @Override
  protected void prepareExecution() {
    args(['rm'] + (project.extensions.docker.removeVolumes ? ['-v'] : []) + [getContainerName()])
    println 'Removing container: ' + getContainerName()
  }
}
