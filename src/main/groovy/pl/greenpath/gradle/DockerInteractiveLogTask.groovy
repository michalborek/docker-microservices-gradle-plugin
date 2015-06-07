package pl.greenpath.gradle

class DockerInteractiveLogTask extends AbstractDockerTask {

  @Override
  protected void prepareExecution() {
    println 'Starting logging: ' + getContainerName()
    super.args 'logs', '-f', getContainerName()
  }
}
