package pl.greenpath.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerRunTaskTest extends Specification {

  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    def plugin = new DockerPlugin()
    plugin.apply(rootProject)
  }

  def "should have set 'docker' as a default executable of the task"() {
    when:
    DockerRunTask task = rootProject.getTasksByName('dockerRun', false)[0]
    then:
    task.getExecutable() == 'docker'
  }

  def "should invoke run on docker with default attributes"() {
    given:
    DockerRunTask task = getMockedTask()
    when:
    task.exec()
    then:
    task.getArgs() == ['run', '-d', '--name=testProject', 'testProject']
  }

  def "should invoke run without detached mode when defined in extension"() {
    given:
    DockerRunTask task = getMockedTask()
    rootProject.docker.runDetached = false
    when:
    task.exec()
    then:
    task.getArgs() == ['run', '--name=testProject', 'testProject']
  }

  def "should invoke run on docker with given port"() {
    given:
    rootProject.docker.port = 8080
    DockerRunTask task = getMockedTask()
    when:
    task.exec()
    then:
    task.getArgs() == ['run', '-d', '-p', '8080:8080', '--name=testProject', 'testProject']
  }

  DockerRunTask getMockedTask() {
    DockerRunTask task = rootProject.getTasksByName('dockerRun', false)[0]
    task.executable 'echo'
  }
}
