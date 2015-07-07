package pl.greenpath.gradle.task

import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.TaskAction
import pl.greenpath.gradle.extension.DockerfileGenerator

class GenerateDockerfileTask extends AbstractTask {

  Closure<DockerfileGenerator> templateClosure

  @TaskAction
  def executeTask() {
    def dockerDirectory = new File(getProject().getBuildDir(), 'docker')
    dockerDirectory.mkdirs()
    def dockerfile = new File(dockerDirectory, 'Dockerfile')
    def dockerfileExtension = new DockerfileGenerator()
    Closure<DockerfileGenerator> template = templateClosure.asType(Closure).rehydrate(dockerfileExtension, this, this)
    template()
    dockerfile << dockerfileExtension.toDockerfile()
  }

  void template(Closure<DockerfileGenerator> template) {
    this.templateClosure = template;
  }
}
