package pl.greenpath.gradle

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.extension.DockerExtension
import spock.lang.Specification
import spock.lang.Unroll

class DockerPluginTest extends Specification {

  Project rootProject

  def setup() {
    def buildDir = File.createTempDir()
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    rootProject.setBuildDir(buildDir)
  }

  @Unroll
  def "project with applied plugin should have #taskName task defined"() {
    given:
    def plugin = new DockerPlugin()
    plugin.apply(rootProject)
    expect:
    rootProject.getTasksByName(taskName, false) != null
    where:
    taskName << ['generateDockerfile', 'copyDockerfile', 'dockerStop', 'dockerLogs', 'dockerRun', 'dockerRunSingle', 'dockerRemoveContainer', 'dockerRemoveImage', 'dockerBuild']
  }

  def "dockerRun should be dependant on project's dockerRun given in linkedMicroservices attribute"() {
    given:
    def plugin = new DockerPlugin()
    def link1Project = sampleDockerProject('link1')
    def link2Project = sampleDockerProject('link2')
    when:
    plugin.apply(rootProject)
    rootProject.docker {
      linkedMicroservices 'link1', 'link2'
    }
    then:
    dependsOnDockerRunOf link1Project
    dependsOnDockerRunOf link2Project
  }

  def "copyToDockerDir should copy files defined in docker extensions into docker directory"() {
    given:
    File fileToCopy = getTempFile()
    File anotherFileToCopy = getTempFile()
    def plugin = new DockerPlugin()
    plugin.apply(rootProject)
    rootProject.extensions.docker.copyToDockerDir fileToCopy, anotherFileToCopy
    when:
    rootProject.getTasksByName('copyToDockerDir', false)[0].copy()
    then:
    new File(rootProject.getBuildDir(), "docker/${fileToCopy.name}").exists()
    new File(rootProject.getBuildDir(), "docker/${anotherFileToCopy.name}").exists()
  }

  private File getTempFile() {
    def anotherFileToCopy = File.createTempFile('testfile' + Math.random() * 100 % 100, '.jar')
    anotherFileToCopy.deleteOnExit()
    anotherFileToCopy
  }


  @Unroll
  def "project should have #extensionName extension bound to #extensionClass"() {
    given:
    def plugin = new DockerPlugin()
    plugin.apply(rootProject)
    expect:
    extensionClass.isInstance(rootProject[extensionName])
    where:
    extensionName | extensionClass
    'docker'      | DockerExtension
  }

  private void dependsOnDockerRunOf(Project project) {
    def task = getDockerRunTask project
    def dockerRunTask = rootProject.getTasksByName('dockerRun', false)[0]
    assert task in dockerRunTask.taskDependencies.getDependencies(dockerRunTask)
  }

  private static Task getDockerRunTask(Project link1Project) {
    link1Project.getTasksByName('dockerRun', false)[0]
  }

  private Project sampleDockerProject(String projectName) {
    def project = ProjectBuilder.builder().withName(projectName).withParent(rootProject).build()
    new DockerPlugin().apply(project)
    project
  }
}
