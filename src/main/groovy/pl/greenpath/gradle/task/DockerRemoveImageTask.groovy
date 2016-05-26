package pl.greenpath.gradle.task

import groovy.transform.CompileStatic

@CompileStatic
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
