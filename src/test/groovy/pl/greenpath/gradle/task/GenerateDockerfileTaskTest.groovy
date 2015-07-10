package pl.greenpath.gradle.task

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.DockerPlugin
import pl.greenpath.gradle.extension.DockerExtension
import spock.lang.Specification

class GenerateDockerfileTaskTest extends Specification {

  static final String TASK_NAME = 'generateDockerfile'

  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    def plugin = new DockerPlugin()
    plugin.apply(rootProject)
  }

  def "should generate Dockerfile for Spring Boot microservice when corresponding template is given"() {
    given:
    def tempDir = File.createTempDir()
    rootProject.version = '1.1'
    rootProject.extensions['docker'].port 8082
    rootProject.buildDir = tempDir
    rootProject.extensions.docker.dockerfile.with DockerExtension.microserviceTemplate
    def task = rootProject.getTasksByName(TASK_NAME, false)[0]
    when:
    task.executeTask()
    then:
    def dockerfile = new File(tempDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''FROM java:8
                                     |EXPOSE 8082
                                     |ADD testProject-1.1.jar .
                                     |CMD java -jar testProject-1.1.jar
                                     |'''.stripMargin()
  }

  def "should be able to redefine parameters after applying template"() {
    given:
    def tempDir = File.createTempDir()
    rootProject.version = '1.1'
    rootProject.buildDir = tempDir
    rootProject.extensions.docker.port 8082
    rootProject.extensions.docker.dockerfile.template DockerExtension.microserviceTemplate
    rootProject.extensions.docker.dockerfile.add('testing', '.')
    def task = rootProject.getTasksByName(TASK_NAME, false)[0]
    when:
    task.executeTask()
    then:
    def dockerfile = new File(tempDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''FROM java:8
                                     |EXPOSE 8082
                                     |ADD testProject-1.1.jar .
                                     |ADD testing .
                                     |CMD java -jar testProject-1.1.jar
                                     |'''.stripMargin()
  }

  def "should generate Dockerfile with user defined base image when defined in extension"() {
    def tempDir = File.createTempDir()
    rootProject.extensions.docker.dockerfile.from 'postgres:9.4'
    rootProject.version = '1.2'
    rootProject.buildDir = tempDir
    def task = rootProject.getTasksByName(TASK_NAME, false)[0]
    when:
    task.executeTask()
    then:
    def dockerfile = new File(tempDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''FROM postgres:9.4
                                     |'''.stripMargin()
  }

  def "should generate Dockerfile with port defined in docker extension"() {
    def tempDir = File.createTempDir()
    rootProject.version = '1.2'
    rootProject.buildDir = tempDir
    rootProject.extensions.docker.port 8080
    def task = rootProject.getTasksByName(TASK_NAME, false)[0]
    when:
    task.executeTask()
    then:
    def dockerfile = new File(tempDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''EXPOSE 8080
                                     |'''.stripMargin()
  }

  def "should replace content of file if already exists"() {
    def tempDir = File.createTempDir()
    rootProject.version = '1.2'
    rootProject.buildDir = tempDir
    rootProject.extensions.docker.port 8080
    def task = rootProject.getTasksByName(TASK_NAME, false)[0]
    when:
    task.executeTask()
    task.executeTask()
    then:
    def dockerfile = new File(tempDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''EXPOSE 8080
                                     |'''.stripMargin()
  }

}
