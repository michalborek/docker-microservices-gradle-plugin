package pl.greenpath.gradle.extension

import spock.lang.Specification


class DockerfileGeneratorTest extends Specification {

  def dockerfileExtension = new DockerfileGenerator()

  def "should add FROM with given image"() {
    when:
    dockerfileExtension.from 'image:4.03'
    then:
    dockerfileExtension.toDockerfile() == 'FROM image:4.03\n'
  }

  def "should add ENV with parameters"() {
    when:
    dockerfileExtension.env 'key', 'value'
    then:
    dockerfileExtension.toDockerfile() == 'ENV key value\n'
  }

  def "should add ADD with parameters"() {
    when:
    dockerfileExtension.add 'source', 'dest'
    then:
    dockerfileExtension.toDockerfile() == 'ADD source dest\n'
  }

  def "should add COPY with parameters"() {
    when:
    dockerfileExtension.copy 'source', 'dest'
    then:
    dockerfileExtension.toDockerfile() == 'COPY source dest\n'
  }

  def "should add WORKDIR with parameters"() {
    when:
    dockerfileExtension.workdir('/home/user')
    then:
    dockerfileExtension.toDockerfile() == 'WORKDIR /home/user\n'
  }

  def "should add EXPOSE with given port"() {
    when:
    dockerfileExtension.expose(8080)
    then:
    dockerfileExtension.toDockerfile() == 'EXPOSE 8080\n'
  }

  def "should add VOLUME with defined volume"() {
    when:
    dockerfileExtension.volume('/var/volume')
    then:
    dockerfileExtension.toDockerfile() == 'VOLUME /var/volume\n'
  }

  def "should add USER with defined user"() {
    when:
    dockerfileExtension.user('deamon')
    then:
    dockerfileExtension.toDockerfile() == 'USER deamon\n'
  }
}
