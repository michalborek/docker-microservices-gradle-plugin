package pl.greenpath.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerStopTaskTest extends Specification {

  static final String TASK_NAME = 'dockerStop'

  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    new DockerPlugin().apply(rootProject)
  }

  def "should have set 'docker' as a default executable of the task"() {
    when:
    DockerStopTask removeImageTask = rootProject.getTasksByName(TASK_NAME, false)[0]
    then:
    removeImageTask.getExecutable() == 'docker'
  }

  def "should run 'stop' on given project with 2 seconds timeout"() {
    given:
    DockerStopTask stopTask = rootProject.getTasksByName(TASK_NAME, false)[0]
    stopTask.executable 'echo'
    when:
    stopTask.exec()
    then:
    stopTask.getArgs() == ['stop', '--time=2', 'testProject']
  }
}
