package pl.greenpath.gradle.task

class DockerRemoveContainerTaskTest extends AbstractDockerTaskTest {

  static final String TASK_NAME = 'dockerRemoveContainer'


  @Override
  String getTaskName() {
    return TASK_NAME
  }

  def "should run 'rm' on docker's container"() {
    given:
    AbstractDockerTask removeContainerTask = getMockedTask()
    when:
    removeContainerTask.exec()
    then:
    removeContainerTask.getArgs() == ['rm', '-v', 'testProject']
  }

  def "should not delete volumes if removeVolumes option turned off"() {
    given:
    AbstractDockerTask removeContainerTask = getMockedTask()
    rootProject.docker.removeVolumes false
    when:
    removeContainerTask.exec()
    then:
    removeContainerTask.getArgs() == ['rm', 'testProject']
  }
}
