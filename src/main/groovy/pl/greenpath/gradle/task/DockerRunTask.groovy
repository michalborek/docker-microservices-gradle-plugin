package pl.greenpath.gradle.task

class DockerRunTask extends AbstractDockerTask {

  @Override
  protected void prepareExecution() {
    args(['run'] + (dockerExtension.runDetached ? ['-d'] : []) +
        (dockerExtension.port > 0 ? ['-p', portMapping] : []) + getLinkedMicroservices() + getRunExtraArgs() +
        ['--name=' + getContainerName(), getImageName()])
    println 'Running container: ' + containerName + ' args: ' + args
  }

  protected List<String> getRunExtraArgs() {
    return dockerExtension.runExtraArgs
  }

  private List<String> getLinkedMicroservices() {
    return dockerExtension.linkedMicroservices.collect {
      String linkName = it.replaceAll('/', '-')
      "--link=$linkName:$linkName"
    }
  }

}
