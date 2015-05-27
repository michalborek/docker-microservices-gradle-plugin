package pl.greenpath.gradle

import org.gradle.api.tasks.TaskAction

class DockerRunTask extends AbstractDockerTask {

  @TaskAction
  protected void exec() {
    super.args(['run'] + (project.extensions.docker.runDetached ? ['-d'] : []) +
        ['-p', getPortMapping()] + getLinkedMicroservices() + project.extensions.docker.runExtraArgs +
        ['--name=' + getContainerName(), getContainerName()])
    println 'Running container: ' + getContainerName() + ' args: ' + super.getArgs()
    super.exec()
  }
}
