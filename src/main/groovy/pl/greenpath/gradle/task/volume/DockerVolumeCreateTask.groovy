package pl.greenpath.gradle.task.volume

import groovy.transform.CompileStatic
import pl.greenpath.gradle.task.AbstractDockerTask

@CompileStatic
class DockerVolumeCreateTask extends AbstractDockerTask {

  private String volumeToCreate

  @Override
  protected void prepareExecution() {
    println 'Creating volume: ' + volumeToCreate
    args 'volume', 'create', '--name', volumeToCreate
  }

  void volumeName(String name) {
    this.volumeToCreate = name
  }
}
