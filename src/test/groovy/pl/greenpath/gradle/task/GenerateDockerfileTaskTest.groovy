package pl.greenpath.gradle.task

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.DockerBootPlugin
import pl.greenpath.gradle.extension.DockerfileGenerator
import spock.lang.Specification

class GenerateDockerfileTaskTest extends Specification {

  static final String TASK_NAME = 'generateDockerfile'

  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    def plugin = new DockerBootPlugin()
    plugin.apply(rootProject)
  }

  def "should generate Dockerfile for Spring Boot microservice when DockerBootPlugin is used"() {
    given:
    def tempDir = File.createTempDir()
    rootProject.version = '1.1'
    rootProject.extensions.docker.port = 8082
    rootProject.buildDir = tempDir
    def task = rootProject.getTasksByName(TASK_NAME, false)[0]
    when:
    task.executeTask()
    then:
    def dockerfile = new File(tempDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''FROM ubuntu:14.04
                                     |EXPOSE 8082
                                     |ADD testProject-1.1.jar .
                                     |CMD java -jar testProject-1.1.jar
                                     |'''.stripMargin()
  }

  def "should generate Dockerfile with user defined base image when defined in extension"() {
    def tempDir = File.createTempDir()
    rootProject.extensions['dockerfile'].asType(DockerfileGenerator).from 'postgres:9.4'
    rootProject.version = '1.2'
    rootProject.buildDir = tempDir
    def task = rootProject.getTasksByName(TASK_NAME, false)[0]
    when:
    task.executeTask()
    then:
    def dockerfile = new File(tempDir, 'docker/Dockerfile')
    dockerfile.exists()
    dockerfile.getText('UTF-8') == '''FROM postgres:9.4
                                     |EXPOSE 8080
                                     |ADD testProject-1.2.jar .
                                     |CMD java -jar testProject-1.2.jar
                                     |'''.stripMargin()
  }


}
