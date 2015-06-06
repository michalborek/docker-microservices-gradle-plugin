package pl.greenpath.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerInteractiveLogTaskTest extends Specification {

  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
  }

  def "should run interactive docker logs for given project"() {
    given:
    new DockerPlugin().apply(rootProject)
    DockerInteractiveLogTask dockerLogsTask = rootProject.getTasksByName('dockerLogs', false)[0]
    dockerLogsTask.executable 'echo'
    when:
    dockerLogsTask.exec()
    then:
    dockerLogsTask.getArgs() == ['logs', '-f', 'testProject']
  }
}
