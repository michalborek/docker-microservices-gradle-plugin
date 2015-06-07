package pl.greenpath.gradle

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
    removeContainerTask.getArgs() == ['rm', 'testProject']
  }
}
