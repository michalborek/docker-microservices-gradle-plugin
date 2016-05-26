package pl.greenpath.gradle.task

import groovy.transform.CompileStatic
import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import pl.greenpath.gradle.extension.DockerExtension
import pl.greenpath.gradle.extension.DockerfileDeclaration

@CompileStatic
class GenerateDockerfileTask extends AbstractTask {

  public static final String GROUP_NAME = 'docker'

  public GenerateDockerfileTask() {
    setGroup(GROUP_NAME)
  }

  @TaskAction
  def executeTask() {
    if (!getProject().getExtensions().getByType(DockerExtension).shouldGenerateDockerfile()) {
      println 'Dockerfile generation disabled.'
      return
    }
    File dockerfile = getDockerfile()
    if (dockerfile.exists()) {
      dockerfile.delete()
    } else {
      getDockerDirectory().mkdirs()
    }
    DockerfileDeclaration declaration = getProject().getExtensions().getByType(DockerExtension).getDockerfile()
    dockerfile << declaration.toDockerfile()
  }

  @OutputFile
  File getDockerfile() {
    new File(getDockerDirectory(), 'Dockerfile')
  }

  private File getDockerDirectory() {
    new File(getProject().getBuildDir(), 'docker')
  }
}
