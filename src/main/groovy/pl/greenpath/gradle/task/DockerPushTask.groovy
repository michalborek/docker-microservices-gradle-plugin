package pl.greenpath.gradle.task

import groovy.transform.CompileStatic

@CompileStatic
class DockerPushTask extends AbstractDockerTask {

  @Override
  protected void prepareExecution() {
    println 'Pushing image: ' + getImageName()
    args 'push', getImageName()
  }
}
