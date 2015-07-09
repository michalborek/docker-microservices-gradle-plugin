package pl.greenpath.gradle.task

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.DockerPlugin

class DockerRunTaskTest extends AbstractDockerTaskTest {

  static final String TASK_NAME = 'dockerRun'

  @Override
  String getTaskName() {
    return TASK_NAME
  }

  def "should invoke run on docker with default attributes"() {
    given:
    AbstractDockerTask task = getMockedTask()
    when:
    task.exec()
    then:
    task.getArgs() == ['run', '-d', '--name=testProject', 'testProject']
  }


  def "should invoke run on docker custom container name when defined in extension"() {
    given:
    AbstractDockerTask task = getMockedTask()
    rootProject.docker.containerName 'custom'
    when:
    task.exec()
    then:
    task.getArgs() == ['run', '-d', '--name=custom', 'testProject']
  }

  def "should invoke run on docker custom image name when defined in extension"() {
    given:
    AbstractDockerTask task = getMockedTask()
    rootProject.docker.imageName 'custom'
    when:
    task.exec()
    then:
    task.getArgs() == ['run', '-d', '--name=testProject', 'custom']
  }

  def "should invoke run without detached mode when defined in extension"() {
    given:
    AbstractDockerTask task = getMockedTask()
    rootProject.docker.runDetached false
    when:
    task.exec()
    then:
    task.getArgs() == ['run', '--name=testProject', 'testProject']
  }

  def "should invoke run on docker with given port"() {
    given:
    rootProject.docker.port 8080
    AbstractDockerTask task = getMockedTask()
    when:
    task.exec()
    then:
    task.getArgs() == ['run', '-d', '-p', '8080:8080', '--name=testProject', 'testProject']
  }

  def "should invoke run with extra args when defined in extension"() {
    given:
    rootProject.docker.runExtraArgs '-v', '--rm=false'
    AbstractDockerTask task = getMockedTask()
    when:
    task.exec()
    then:
    task.getArgs() == ['run', '-d', '-v', '--rm=false', '--name=testProject', 'testProject']
  }

  def "should should link microservices defined in extension"() {
    given:
    createDummyGradleProject('test/one')
    createDummyGradleProject('test-two')
    rootProject.docker.linkedMicroservices 'test/one', 'test-two'
    AbstractDockerTask task = getMockedTask()
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

}
