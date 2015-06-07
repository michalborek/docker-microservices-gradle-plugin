package pl.greenpath.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerRemoveImageTaskTest extends Specification {

  static final String TASK_NAME = 'dockerRemoveImage'

  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    new DockerPlugin().apply(rootProject)
  }

  def "should have set 'docker' as a default executable of the task"() {
    when:
    DockerRemoveImageTask removeImageTask = rootProject.getTasksByName(TASK_NAME, false)[0]
    then:
    removeImageTask.getExecutable() == 'docker'
  }

  def "should run 'rmi' on docker's container"() {
    given:
    DockerRemoveImageTask removeImageTask = rootProject.getTasksByName(TASK_NAME, false)[0]
    removeImageTask.executable 'echo'
    when:
    removeImageTask.exec()
    then:
    removeImageTask.getArgs() == ['rmi', 'testProject']
  }
}
