package pl.greenpath.gradle.task.volume

import groovy.transform.CompileStatic
import pl.greenpath.gradle.task.AbstractDockerTask

@CompileStatic
class DockerVolumeRemoveTask extends AbstractDockerTask {

  private String volumeToRemove

  @Override
  protected void prepareExecution() {
    println 'Removing volume: ' + volumeToRemove
    args 'volume', 'rm', volumeToRemove
  }

  void volumeName(String name) {
    this.volumeToRemove = name
  }
}
