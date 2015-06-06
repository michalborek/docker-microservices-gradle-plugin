package pl.greenpath.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerRemoveContainerTaskTest extends Specification {

  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
  }

  def "should run 'rm' on docker's container"() {
    given:
    new DockerPlugin().apply(rootProject)
    DockerRemoveContainerTask removeContainerTask = rootProject.getTasksByName('dockerRemoveContainer', false)[0]
    removeContainerTask.executable 'echo'
    when:
    removeContainerTask.exec()
    then:
    removeContainerTask.getArgs() == ['rm', 'testProject']
  }
}
