package pl.greenpath.gradle.task

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.DockerPlugin
import spock.lang.Specification

class CopyJarToDockerDirTest extends Specification {

  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    def dockerPlugin = new DockerPlugin()
    rootProject.pluginManager.apply('java')
    dockerPlugin.apply(rootProject)
  }

  def "should copy JAR file named based on JAR task configuration when copyJarToDockerDir task is run"() {
    given:
    rootProject.jar.baseName = 'testName123'
    rootProject.jar.version = '1.1.1'
    Copy task = rootProject.getTasksByName("copyJarToDockerDir", false).first()
    def jarFilePath = rootProject.buildDir.toPath().resolve('docker').resolve('testName123-1.1.1.jar')
    when:
    Jar jarTask = rootProject.getTasksByName('jar', true).first()
    jarTask.execute()
    task.execute()
    then:
    jarFilePath.toFile().exists()

  }
}
