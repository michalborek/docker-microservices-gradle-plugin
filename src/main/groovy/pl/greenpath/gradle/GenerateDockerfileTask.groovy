package pl.greenpath.gradle

import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.TaskAction

import static java.lang.String.format

class GenerateDockerfileTask extends AbstractTask {


  @TaskAction
  def executeTask() {
    def dockerDirectory = new File(getProject().getBuildDir(), 'docker')
    dockerDirectory.mkdirs()
    def dockerfile = new File(dockerDirectory, 'Dockerfile')

    def dockerExtension = getProject().extensions.docker
    def appJarName = format('%s-%s.jar', getProject().getName(), getProject().getVersion())
    dockerfile << format('FROM %s%n', (String) dockerExtension.baseImage)
    dockerfile << format('EXPOSE %d%n', (int) dockerExtension.port)
    dockerfile << format('ADD %s%n', appJarName)
    dockerfile << format('CMD java -jar %s%n', appJarName)
  }
}
