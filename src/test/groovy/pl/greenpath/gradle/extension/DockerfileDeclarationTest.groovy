package pl.greenpath.gradle.extension

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.DockerPlugin
import spock.lang.Specification

class DockerfileDeclarationTest extends Specification {

  Project project

  DockerfileDeclaration dockerfileDeclaration = new DockerfileDeclaration(project)

  def setup() {
    project = ProjectBuilder.builder().withName('testProject').build()
    DockerPlugin plugin = new DockerPlugin()
    plugin.apply(project)
  }

  def "should add FROM with given image"() {
    when:
    dockerfileDeclaration.from 'image:4.03'
    then:
    dockerfileDeclaration.toDockerfile() == 'FROM image:4.03\n'
  }

  def "should add ENV with parameters"() {
    when:
    dockerfileDeclaration.env 'key', 'value'
    then:
    dockerfileDeclaration.toDockerfile() == 'ENV key value\n'
  }

  def "should add ADD with parameters"() {
    when:
    dockerfileDeclaration.add 'source', 'dest'
    then:
    dockerfileDeclaration.toDockerfile() == 'ADD source dest\n'
  }

  def "should add COPY with parameters"() {
    when:
    dockerfileDeclaration.copy 'source', 'dest'
    then:
    dockerfileDeclaration.toDockerfile() == 'COPY source dest\n'
  }

  def "should add WORKDIR with parameters"() {
    when:
    dockerfileDeclaration.workdir '/home/user'
    then:
    dockerfileDeclaration.toDockerfile() == 'WORKDIR /home/user\n'
  }

  def "should add EXPOSE with given port"() {
    when:
    dockerfileDeclaration.expose 8080
    then:
    dockerfileDeclaration.toDockerfile() == 'EXPOSE 8080\n'
  }

  def "should add RUN with given commands"() {
    when:
    dockerfileDeclaration.run('localedef -i en_US')
    dockerfileDeclaration.run('echo "ok"')
    then:
    dockerfileDeclaration.toDockerfile() == '''RUN localedef -i en_US
                                              |RUN echo "ok"
                                              |'''.stripMargin()
  }

  def "should add EXPOSE with given ports"() {
    when:
    dockerfileDeclaration.expose 8080, 9090
    then:
    dockerfileDeclaration.toDockerfile() == 'EXPOSE 8080 9090\n'
  }

  def "should add VOLUME with defined volume"() {
    when:
    dockerfileDeclaration.volume '/var/volume'
    then:
    dockerfileDeclaration.toDockerfile() == 'VOLUME /var/volume\n'
  }

  def "should add USER with defined user"() {
    when:
    dockerfileDeclaration.user 'deamon'
    then:
    dockerfileDeclaration.toDockerfile() == 'USER deamon\n'
  }
}
