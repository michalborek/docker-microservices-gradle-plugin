package pl.greenpath.gradle.task

import groovy.transform.CompileStatic

@CompileStatic
class DockerRunTask extends AbstractDockerTask {

  @Override
  protected void prepareExecution() {
    args(createArgumentList())
    println 'Running container: ' + containerName + ' args: ' + args
  }

  private List<String> createArgumentList() {
    final List<String> arguments = ['run']
    if (dockerExtension.runDetached) {
      arguments += '-d'
    }
    if (dockerExtension.port > 0) {
      arguments += ['-p', portMapping]
    }
    arguments += getLinkedMicroservices()
    arguments += getRunExtraArgs()
    arguments += ['--name=' + getContainerName(), getImageName()]
    return arguments
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
