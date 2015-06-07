package pl.greenpath.gradle

class DockerInteractiveLogTaskTest extends AbstractDockerTaskTest {

  static final String TASK_NAME = 'dockerLogs'

  String getTaskName() {
    return TASK_NAME
  }

  def "should run interactive docker logs for given project"() {
    given:
    AbstractDockerTask logsTask = getMockedTask()
    when:
    logsTask.exec()
    then:
    logsTask.getArgs() == ['logs', '-f', 'testProject']
  }
}
