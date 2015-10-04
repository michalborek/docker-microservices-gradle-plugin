package pl.greenpath.gradle.task

class DockerPushTask extends AbstractDockerTask {

  @Override
  protected void prepareExecution() {
    println 'Pushing image: ' + getImageName()
    args 'push', getImageName()
  }
}
