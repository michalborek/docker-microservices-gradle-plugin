package pl.greenpath.gradle.extension

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.DockerPlugin
import spock.lang.Specification

import static pl.greenpath.gradle.extension.DockerExtension.microserviceTemplate

class DockerExtensionTest extends Specification {

  Project project

  Project childProject

  def setup() {
    project = ProjectBuilder.builder().withName('testProject').build()
    childProject = ProjectBuilder.builder().withName('childProject')
                                           .withParent(project)
                                           .build()
    def plugin = new DockerPlugin()
    plugin.apply(project)
    plugin.apply(childProject)
  }

  def "should apply microserviceTemplate"() {
    given:
    project.version = '1.0-SNAPSHOT'
    DockerExtension dockerExtension = project.extensions['docker']
    dockerExtension.port 8080
    when:
    dockerExtension.dockerfile.template microserviceTemplate
    then:
    dockerExtension.dockerfile.toDockerfile() == '''|FROM java:8
                                             |EXPOSE 8080
                                             |ADD testProject-1.0-SNAPSHOT.jar .
                                             |CMD java -jar testProject-1.0-SNAPSHOT.jar
                                             |'''.stripMargin()
  }

  def "should apply closure directly to dockerfile attribute in docker extension"() {
    given:
    project.version = '1.0-SNAPSHOT'
    DockerExtension dockerExtension = project.extensions['docker']
    dockerExtension.port 8080
    when:
    dockerExtension.dockerfile microserviceTemplate
    then:
    dockerExtension.dockerfile.toDockerfile() == '''|FROM java:8
                                             |EXPOSE 8080
                                             |ADD testProject-1.0-SNAPSHOT.jar .
                                             |CMD java -jar testProject-1.0-SNAPSHOT.jar
                                             |'''.stripMargin()
  }

  def "should allow exposing many ports"() {
    given:
    project.version = '1.0-SNAPSHOT'
    DockerExtension dockerExtension = project.extensions['docker']
    when:
    dockerExtension.dockerfile.expose 8080
    dockerExtension.dockerfile.expose 9090
    then:
    dockerExtension.dockerfile.toDockerfile() == '''EXPOSE 8080 9090
                                             |'''.stripMargin()
  }

  def "should not duplicate exposed ports"() {
    given:
    project.version = '1.0-SNAPSHOT'
    DockerExtension dockerExtension = project.extensions['docker']
    when:
    dockerExtension.dockerfile.expose 8080
    dockerExtension.dockerfile.expose 8080
    then:
    dockerExtension.dockerfile.toDockerfile() == '''EXPOSE 8080
                                             |'''.stripMargin()
  }

  def "should append the docker run args to the old ones"() {
    given:
    project.version = '1.0-SNAPSHOT'
    DockerExtension dockerExtension = project.extensions['docker']
    String arg1 = 'arg1'
    String arg2 = 'arg2'
    when:
    dockerExtension.addDockerRunArgs(arg1)
    dockerExtension.addDockerRunArgs(arg2)
    then:
    dockerExtension.runExtraArgs.join(' ').contains("${arg1} ${arg2}")
  }

  def "should publish specified ports"() {
    given:
    project.version = '1.0-SNAPSHOT'
    DockerExtension dockerExtension = project.extensions['docker']

    def hostPort1 = 123
    def containerPort1 = 456
    def hostPort2 = 22
    def containerPort2 = 33
    when:
    dockerExtension.publishPort(hostPort1, containerPort1)
    dockerExtension.publishPort(hostPort2, containerPort2)
    then:
    dockerExtension.runExtraArgs.join(' ').contains("-p ${hostPort1}:${containerPort1}")
    dockerExtension.runExtraArgs.join(' ').contains("-p ${hostPort2}:${containerPort2}")
  }

  def "should have default root project path on docker host equal to /project"() {
    given:
    project.version = '1.0-SNAPSHOT'
    when:
    DockerExtension dockerExtension = project.extensions['docker']
    then:
    dockerExtension.fixedRootProjectPath == '/project'
  }

  def "should allow to customize the root project path on docker host"() {
    given:
    project.version = '1.0-SNAPSHOT'
    DockerExtension dockerExtension = project.extensions['docker']
    String somePath = '/my_fav_path'
    when:
    dockerExtension.fixedRootProjectPath somePath
    then:
    dockerExtension.fixedRootProjectPath == somePath
  }

  def "should bind mount specified path from the docker host to the container"() {
    given:
    project.version = '1.0-SNAPSHOT'
    DockerExtension dockerExtension = project.extensions['docker']
    when:
    dockerExtension.bindMount('/src', '/dst')
    then:
    dockerExtension.runExtraArgs.join(' ').contains('-v /src:/dst')
  }

  def "should not map docker host paths by default"() {
    expect:
    project.extensions['docker'].mapProjectPathsToFixedRoot == false
  }

  def "should replace %rootProjectDir% marker if mapProjectPathsToFixedRoot is enabled"() {
    given:
    project.version = '1.0-SNAPSHOT'
    String rootProjectPath = '/proj'
    DockerExtension dockerExtension = project.extensions['docker']
    dockerExtension.mapProjectPathsToFixedRoot true
    dockerExtension.fixedRootProjectPath rootProjectPath
    when:
    dockerExtension.bindMount('%rootProjectDir%/some/path', '/dst')
    then:
    dockerExtension.runExtraArgs.join(' ').contains("-v ${rootProjectPath}/some/path:/dst")
  }

  def "should replace %projectDir% marker for rootProject if mapProjectPathsToFixedRoot is enabled"() {
    given:
    project.version = '1.0-SNAPSHOT'
    String rootProjectPath = '/proj'
    DockerExtension dockerExtension = project.extensions['docker']
    dockerExtension.mapProjectPathsToFixedRoot true
    dockerExtension.fixedRootProjectPath rootProjectPath
    when:
    dockerExtension.bindMount('%projectDir%/some/path', '/dst')
    then:
    dockerExtension.runExtraArgs.join(' ').contains("-v ${rootProjectPath}/some/path:/dst")
  }

  def "should replace %projectDir% marker for child project if mapProjectPathsToFixedRoot is enabled"() {
    given:
    project.version = '1.0-SNAPSHOT'
    String rootProjectPath = '/proj'
    DockerExtension dockerExtension = childProject.extensions['docker']
    dockerExtension.mapProjectPathsToFixedRoot true
    dockerExtension.fixedRootProjectPath rootProjectPath
    when:
    dockerExtension.bindMount('%projectDir%/some/path', '/dst')
    then:
    dockerExtension.runExtraArgs.join(' ').contains("-v ${rootProjectPath}/childProject/some/path:/dst")
  }

  def "should replace %projectDir% marker with real projectDir for child project"() {
    given:
    project.version = '1.0-SNAPSHOT'
    DockerExtension dockerExtension = childProject.extensions['docker']
    dockerExtension.mapProjectPathsToFixedRoot false
    when:
    dockerExtension.bindMount('%projectDir%/some/path', '/dst')
    then:
    dockerExtension.runExtraArgs.join(' ').contains("-v ${childProject.projectDir}/some/path:/dst")
  }
}
