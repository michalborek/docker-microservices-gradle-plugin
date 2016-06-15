package pl.greenpath.gradle.task

import groovy.transform.CompileStatic
import pl.greenpath.gradle.extension.DockerExtension

@CompileStatic
class DockerRemoveContainerTask extends AbstractDockerTask {

  public DockerRemoveContainerTask() {
    setIgnoreExitValue true
  }

  @Override
  protected void prepareExecution() {
    args(getArgumentList())
    println 'Removing container: ' + getContainerName()
  }

  private List<String> getArgumentList() {
    List<String> arguments = ['rm']
    if (project.extensions.getByType(DockerExtension).removeVolumes) {
      arguments += '-v'
    }
    arguments += getContainerName()
    return arguments
  }
}
