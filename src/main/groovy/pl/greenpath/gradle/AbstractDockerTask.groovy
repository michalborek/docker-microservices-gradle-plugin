package pl.greenpath.gradle

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction

abstract class AbstractDockerTask extends Exec {

  public static final String GROUP_NAME = 'docker'

  protected AbstractDockerTask() {
    setGroup(GROUP_NAME)
  }

  protected abstract void prepareExecution()

  @TaskAction
  protected final void exec() {
    prepareExecution()
    setExecutable(getExecutable());
    super.exec()
  }

  @Override
  String getExecutable() {
    return project.extensions.docker.executable
  }

  protected String getContainerName() {
    def containerName = project.extensions.docker.containerName;
    return containerName ?: project.name.replaceAll('/', '-')
  }

  protected String getImageName() {
    def imageName = project.extensions.docker.imageName;
    return imageName ?: project.name.replaceAll('/', '-')
  }

  protected String getPortMapping() {
    def port = project.extensions.docker.port
    return "$port:$port"
  }

}
