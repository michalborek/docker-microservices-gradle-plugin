package pl.greenpath.gradle.task.volume

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.DockerPlugin
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
// not supported by travis
class DockerVolumeTasksTest extends Specification {

  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    def plugin = new DockerPlugin()
    plugin.apply(rootProject)
  }

  def "should invoke create volume"() {
    given:
    String dummyName = 'testVol'
    DockerVolumeCreateTask task = rootProject.task('testTask', type: DockerVolumeCreateTask) {
      volumeName dummyName
    }
    when:
    task.exec()
    then:
    task.getExecResult().exitValue == 0
    task.getArgs() == ['volume', 'create', '--name', dummyName]
    when:
    DockerVolumeRemoveTask removeTask = rootProject.task('testRemoveTask', type: DockerVolumeRemoveTask) {
      volumeName dummyName
    }
    removeTask.exec()
    then:
    removeTask.getExecResult().exitValue == 0
  }
}
