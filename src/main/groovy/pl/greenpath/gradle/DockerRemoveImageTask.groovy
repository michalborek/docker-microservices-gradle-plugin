package pl.greenpath.gradle

class DockerRemoveImageTask extends AbstractDockerTask {

  public DockerRemoveImageTask() {
    setIgnoreExitValue true
  }

  @Override
  protected void prepareExecution() {
    super.args('rmi', getImageName())
    println 'Removing image: ' + getImageName()
  }
}
