package pl.greenpath.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.extension.DockerfileGenerator
import spock.lang.Specification

class DockerBootPluginTest extends Specification {

  Project rootProject

  def setup() {
    rootProject = ProjectBuilder.builder().withName('testProject').build()
    rootProject.version = '1.2'
  }

  def "should define predefined dockerfile template for Spring Boot application"() {
    given:
    def plugin = new DockerBootPlugin()
    when:
    plugin.apply(rootProject)
    then:
    DockerfileGenerator dockerfile = rootProject.extensions['dockerfile']
    dockerfile.toDockerfile() == """FROM ubuntu:14.04
                                   |EXPOSE 8080
                                   |ADD testProject-1.2.jar .
                                   |CMD java -jar testProject-1.2.jar
                                   |""".stripMargin()
  }

  def "should define predefined dockerfile with changed expose port"() {
    given:
    def plugin = new DockerBootPlugin()
    when:
    plugin.apply(rootProject)
    then:
    DockerfileGenerator dockerfile = rootProject.extensions['dockerfile']
    dockerfile.expose 8090
    dockerfile.toDockerfile() == """FROM ubuntu:14.04
                                   |EXPOSE 8090
                                   |ADD testProject-1.2.jar .
                                   |CMD java -jar testProject-1.2.jar
                                   |""".stripMargin()
  }

}
