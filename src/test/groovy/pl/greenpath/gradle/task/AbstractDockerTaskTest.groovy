package pl.greenpath.gradle.task
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.DockerPlugin
import spock.lang.Specification

abstract class AbstractDockerTaskTest extends Specification {

  Project rootProject

  abstract String getTaskName()

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    def plugin = new DockerPlugin()
    plugin.apply(rootProject)
  }

  def "should have set 'docker' as a default executable of the task"() {
    when:
    AbstractDockerTask task = rootProject.getTasksByName(getTaskName(), false)[0]
    then:
    task.getExecutable() == 'docker'
  }

  def "it should be able to change executable"() {
    when:
    AbstractDockerTask task = rootProject.getTasksByName(getTaskName(), false)[0]
    rootProject.docker.executable = 'testExecutable'
    then:
    task.getExecutable() == 'testExecutable'
  }


  AbstractDockerTask getMockedTask() {
    AbstractDockerTask task = rootProject.getTasksByName(TASK_NAME, false)[0]
    rootProject.docker.executable 'echo'
    task
  }
}
