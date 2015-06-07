package pl.greenpath.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerBuildTaskTest extends Specification {

  static final String TASK_NAME = 'dockerBuild'

  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    new DockerPlugin().apply(rootProject)
  }

  def "should have set 'docker' as a default executable of the task"() {
    when:
    DockerBuildTask buildTask = rootProject.getTasksByName(TASK_NAME, false)[0]
    then:
    buildTask.getExecutable() == 'docker'
  }

  def "should run 'build' on given container"() {
    given:
    DockerBuildTask buildTask = rootProject.getTasksByName(TASK_NAME, false)[0]
    rootProject.buildDir = new File('/tmp')
    buildTask.executable 'echo'
    when:
    buildTask.exec()
    then:
    buildTask.getArgs() == ['build', '-t', 'testProject', '/tmp/docker']
  }
}
