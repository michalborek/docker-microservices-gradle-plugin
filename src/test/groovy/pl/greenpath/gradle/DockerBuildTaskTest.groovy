package pl.greenpath.gradle

import org.gradle.testfixtures.ProjectBuilder

class DockerBuildTaskTest extends AbstractDockerTaskTest {

  static final String TASK_NAME = 'dockerBuild'

  @Override
  String getTaskName() {
    return TASK_NAME
  }

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    new DockerPlugin().apply(rootProject)
  }

  def "should run 'build' on given container"() {
    given:
    AbstractDockerTask buildTask = getMockedTask()
    rootProject.buildDir = new File('/tmp')
    when:
    buildTask.exec()
    then:
    buildTask.getArgs() == ['build', '-t', 'testProject', '/tmp/docker']
  }
}
