package pl.greenpath.gradle

import org.gradle.api.tasks.TaskAction

class DockerRunTask extends AbstractDockerTask {

  @TaskAction
  protected void exec() {
    super.args(['run'] + (extensions.docker.runDetached ? ['-d'] : []) +
        ['-p', getPortMapping()] + getLinkedMicroservices() + extensions.docker.runExtraArgs +
        ['--name=' + getContainerName(), getContainerName()])
    println 'Running container: ' + getContainerName() + ' args: ' + super.getArgs()
    super.exec()
  }
}
