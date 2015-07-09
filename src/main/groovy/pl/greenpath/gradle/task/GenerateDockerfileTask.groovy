package pl.greenpath.gradle.task

import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.TaskAction
import pl.greenpath.gradle.extension.DockerfileDeclaration

class GenerateDockerfileTask extends AbstractTask {

  @TaskAction
  def executeTask() {
    def dockerDirectory = new File(getProject().getBuildDir(), 'docker')
    dockerDirectory.mkdirs()
    def dockerfile = new File(dockerDirectory, 'Dockerfile')
    if (dockerfile.exists()) {
      dockerfile.delete()
    }
    DockerfileDeclaration declaration = getProject().extensions['docker']['dockerfile']
    dockerfile << declaration.toDockerfile()
  }
}
