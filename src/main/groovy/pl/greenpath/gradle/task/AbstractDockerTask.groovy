package pl.greenpath.gradle.task

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction
import pl.greenpath.gradle.extension.DockerExtension

@CompileStatic
abstract class AbstractDockerTask extends Exec {

  public static final String GROUP_NAME = 'docker'

  protected AbstractDockerTask() {
    setGroup(GROUP_NAME)
  }

  protected abstract void prepareExecution()

  @TaskAction
  protected final void exec() {
    prepareExecution()
    setExecutable(executable);
    super.exec()
  }

  @Override
  String getExecutable() {
    return dockerExtension.executable
  }

  protected String getContainerName() {
    String containerName = dockerExtension.containerName;
    return containerName ?: project.name.replaceAll('/', '-')
  }

  protected String getImageName() {
    String imageName = dockerExtension.imageName;
    return imageName ?: project.name.replaceAll('/', '-')
  }

  protected String getPortMapping() {
    int port = dockerExtension.port
    return "$port:$port"
  }

  protected DockerExtension getDockerExtension() {
    return project.extensions.getByType(DockerExtension)
  }

}
