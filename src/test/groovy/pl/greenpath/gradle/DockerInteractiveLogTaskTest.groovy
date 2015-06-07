package pl.greenpath.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerInteractiveLogTaskTest extends Specification {

  static final String TASK_NAME = 'dockerLogs'
  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
  }

  def "should have set 'docker' as a default executable of the task"() {
    given:
    new DockerPlugin().apply(rootProject)
    when:
    DockerInteractiveLogTask task = rootProject.getTasksByName(TASK_NAME, false)[0]
    then:
    task.getExecutable() == 'docker'
  }

  def "should run interactive docker logs for given project"() {
    given:
    new DockerPlugin().apply(rootProject)
    DockerInteractiveLogTask task = rootProject.getTasksByName(TASK_NAME, false)[0]
    task.executable 'echo'
    when:
    task.exec()
    then:
    task.getArgs() == ['logs', '-f', 'testProject']
  }
}
