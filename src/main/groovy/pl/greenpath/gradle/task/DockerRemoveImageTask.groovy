package pl.greenpath.gradle.task

class DockerRemoveImageTask extends AbstractDockerTask {

  public DockerRemoveImageTask() {
    setIgnoreExitValue true
  }

  @Override
  protected void prepareExecution() {
    args('rmi', getImageName())
    println 'Removing image: ' + getImageName()
  }
}
