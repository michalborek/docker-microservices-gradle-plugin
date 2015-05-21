package pl.greenpath.gradle

import org.gradle.api.tasks.TaskAction

class DockerRemoveImageTask extends AbstractDockerTask {

  public DockerRemoveImageTask() {
    setIgnoreExitValue true
  }

  @TaskAction
  protected void exec() {
    println 'Removing image: ' + getImageName()
    super.args('rmi', getImageName())
    super.exec()
  }
}
