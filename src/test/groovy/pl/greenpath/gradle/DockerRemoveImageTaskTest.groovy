package pl.greenpath.gradle

class DockerRemoveImageTaskTest extends AbstractDockerTaskTest {

  static final String TASK_NAME = 'dockerRemoveImage'

  @Override
  String getTaskName() {
    return TASK_NAME
  }

  def "should run 'rmi' on docker's container"() {
    given:
    AbstractDockerTask removeImageTask = getMockedTask()
    when:
    removeImageTask.exec()
    then:
    removeImageTask.getArgs() == ['rmi', 'testProject']
  }
}
