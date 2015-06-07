package pl.greenpath.gradle

class DockerRunTask extends AbstractDockerTask {

  @Override
  protected void prepareExecution() {
    super.args(['run'] + (project.extensions.docker.runDetached ? ['-d'] : []) +
        (project.extensions.docker.port > 0 ? ['-p', getPortMapping()] : []) + getLinkedMicroservices() + project.extensions.docker.runExtraArgs +
        ['--name=' + getContainerName(), getImageName()])
    println 'Running container: ' + getContainerName() + ' args: ' + super.getArgs()
  }

  private List<String> getLinkedMicroservices() {
    return project.extensions.docker.linkedMicroservices.collect {
      def linkName = it.replaceAll('/', '-')
      "--link=$linkName:$linkName"
    }
  }

}
