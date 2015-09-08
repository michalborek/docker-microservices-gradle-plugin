package pl.greenpath.gradle.task

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.DockerPlugin
import pl.greenpath.gradle.extension.DockerExtension
import spock.lang.Specification

class GenerateDockerfileTaskTest extends Specification {

  static final String GENERATE_DOCKERFILE_TASK_NAME = 'generateDockerfile'

  static final String TEST_PROJECT_NAME = 'testProject'

  Project rootProject

  Task generateDockerfileTask

  def setup() {
    rootProject = createTestProject()

    applyDockerPlugin(rootProject)

    generateDockerfileTask = findTask(rootProject, GENERATE_DOCKERFILE_TASK_NAME)

    createTemporaryBuildDirForProject()
  }

  private applyDockerPlugin(Project rootProject) {
    def plugin = new DockerPlugin()
    plugin.apply(rootProject)
  }

  Task findTask(Project project, String taskName) {
    project.getTasksByName(taskName, false).first()
  }

  Project createTestProject() {
    rootProject = ProjectBuilder.builder().withName(TEST_PROJECT_NAME).build()
    rootProject
  }

  void createTemporaryBuildDirForProject() {
    rootProject.buildDir = File.createTempDir()
  }

  def "should generate Dockerfile for Spring Boot microservice when corresponding template is given"() {
    given:
    rootProject.version = '1.1'
    rootProject.extensions['docker'].port 8082
    rootProject.extensions.docker.dockerfile.with DockerExtension.microserviceTemplate
    when:
    generateDockerfileTask.executeTask()
    then:
    def dockerfile = new File(rootProject.buildDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''FROM java:8
                                     |EXPOSE 8082
                                     |ADD testProject-1.1.jar .
                                     |CMD java -jar testProject-1.1.jar
                                     |'''.stripMargin()
  }

  def "should be able to redefine parameters after applying template"() {
    given:
    rootProject.version = '1.1'
    rootProject.extensions.docker.port 8082
    rootProject.extensions.docker.dockerfile.template DockerExtension.microserviceTemplate
    rootProject.extensions.docker.dockerfile.add('testing', '.')
    when:
    generateDockerfileTask.executeTask()
    then:
    def dockerfile = new File(rootProject.buildDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''FROM java:8
                                     |EXPOSE 8082
                                     |ADD testProject-1.1.jar .
                                     |ADD testing .
                                     |CMD java -jar testProject-1.1.jar
                                     |'''.stripMargin()
  }

  def "should generate Dockerfile with user defined base image when defined in extension"() {
    rootProject.extensions.docker.dockerfile.from 'postgres:9.4'
    rootProject.version = '1.2'
    when:
    generateDockerfileTask.executeTask()
    then:
    def dockerfile = new File(rootProject.buildDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''FROM postgres:9.4
                                     |'''.stripMargin()
  }

  def "should generate Dockerfile with port defined in docker extension"() {
    rootProject.version = '1.2'
    rootProject.extensions.docker.port 8080
    when:
    generateDockerfileTask.executeTask()
    then:
    def dockerfile = new File(rootProject.buildDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''EXPOSE 8080
                                     |'''.stripMargin()
  }

  def "should replace content of file if already exists"() {
    rootProject.version = '1.2'
    rootProject.extensions.docker.port 8080
    when:
    generateDockerfileTask.executeTask()
    generateDockerfileTask.executeTask()
    then:
    def dockerfile = new File(rootProject.buildDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''EXPOSE 8080
                                     |'''.stripMargin()
  }

  def "should skip dockerfile generation when generateDockerfile is set to false"() {
    given:
    rootProject.version = '1.1'
    rootProject.extensions.docker.port 8082
    rootProject.extensions.docker.dockerfile.template DockerExtension.microserviceTemplate
    rootProject.extensions.docker.dockerfile.add('testing', '.')
    rootProject.extensions.docker.generateDockerfile false
    when:
    generateDockerfileTask.executeTask()
    then:
    def dockerfile = new File(rootProject.buildDir, 'docker/Dockerfile')
    !dockerfile.exists()
  }


  def "should generate Dockerfile based on string representation passed as 'dockerfile' argument "() {
    given:
    rootProject.version = '1.1'
    rootProject.extensions['docker'].port 8082
    rootProject.extensions.docker.dockerfile """FROM java:8
                                                EXPOSE 9091
                                                CMD echo 'ok'
    """
    when:
    generateDockerfileTask.executeTask()
    then:
    def dockerfile = new File(rootProject.buildDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''FROM java:8
                                     |EXPOSE 9091
                                     |CMD echo 'ok'
                                     |'''.stripMargin()
  }
}
