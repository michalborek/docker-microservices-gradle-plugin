package pl.greenpath.gradle.task

import groovy.transform.CompileStatic

@CompileStatic
class DockerInteractiveLogTask extends AbstractDockerTask {

  @Override
  protected void prepareExecution() {
    println 'Starting logging: ' + getContainerName()
    super.args 'logs', '-f', getContainerName()
  }
}
