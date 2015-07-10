package pl.greenpath.gradle.extension

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.DockerPlugin
import spock.lang.Specification

import static pl.greenpath.gradle.extension.DockerExtension.microserviceTemplate

class DockerExtensionTest extends Specification {

  Project project

  def setup() {
    project = ProjectBuilder.builder().withName('testProject').build()
    def plugin = new DockerPlugin()
    plugin.apply(project)
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

}
