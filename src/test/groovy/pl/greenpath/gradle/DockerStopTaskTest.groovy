package pl.greenpath.gradle

class DockerStopTaskTest extends AbstractDockerTaskTest {

  static final String TASK_NAME = 'dockerStop'

  @Override
  String getTaskName() {
    return TASK_NAME
  }

  def "should run 'stop' on given project with 2 seconds timeout"() {
    given:
    DockerStopTask stopTask = rootProject.getTasksByName(TASK_NAME, false)[0]
    rootProject.docker.executable 'echo'
    when:
    stopTask.exec()
    then:
    stopTask.getArgs() == ['stop', '--time=2', 'testProject']
  }
}
