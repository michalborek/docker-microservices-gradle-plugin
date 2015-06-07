package pl.greenpath.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerRunTaskTest extends Specification {

  static final String TASK_NAME = 'dockerRun'
  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    def plugin = new DockerPlugin()
    plugin.apply(rootProject)
  }

  def "should have set 'docker' as a default executable of the task"() {
    when:
    DockerRunTask task = rootProject.getTasksByName(TASK_NAME, false)[0]
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

  def "should invoke run with extra args when defined in extension"() {
    given:
    rootProject.docker.runExtraArgs = ['-v', '--rm=false']
    DockerRunTask task = getMockedTask()
    when:
    task.exec()
    then:
    task.getArgs() == ['run', '-d', '-v', '--rm=false', '--name=testProject', 'testProject']
  }

  def "should should link microservices defined in extension"() {
    given:
    createDummyGradleProject('test/one')
    createDummyGradleProject('test-two')
    rootProject.docker.linkedMicroservices = ['test/one', 'test-two']
    DockerRunTask task = getMockedTask()
    when:
    task.exec()
    then:
    task.getArgs() == ['run', '-d', '--link=test-one:test-one', '--link=test-two:test-two', '--name=testProject', 'testProject']
  }

  Project createDummyGradleProject(String projectName) {
    def project = ProjectBuilder.builder().withName(projectName).withParent(rootProject).build()
    new DockerPlugin().apply(project)
    project
  }

  DockerRunTask getMockedTask() {
    DockerRunTask task = rootProject.getTasksByName(TASK_NAME, false)[0]
    task.executable 'echo'
  }
}
