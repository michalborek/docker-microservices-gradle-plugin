package pl.greenpath.gradle.extension

import spock.lang.Specification


class DockerfileExtensionTest extends Specification {

  def dockerfileExtension = new DockerfileExtension()

  def "should add FROM with given image"() {
    when:
    dockerfileExtension.from 'image:4.03'
    then:
    dockerfileExtension.getCommands() == ['FROM image:4.03']
  }

  def "should add ENV with parameters"() {
    when:
    dockerfileExtension.env 'key', 'value'
    then:
    dockerfileExtension.getCommands() == ['ENV key value']
  }

  def "should add ADD with parameters"() {
    when:
    dockerfileExtension.add 'source', 'dest'
    then:
    dockerfileExtension.getCommands() == ['ADD source dest']
  }

  def "should add COPY with parameters"() {
    when:
    dockerfileExtension.copy 'source', 'dest'
    then:
    dockerfileExtension.getCommands() == ['COPY source dest']
  }

  def "should add WORKDIR with parameters"() {
    when:
    dockerfileExtension.workdir('/home/user')
    then:
    dockerfileExtension.getCommands() == ['WORKDIR /home/user']
  }

  def "should add EXPOSE with given port"() {
    when:
    dockerfileExtension.expose(8080)
    then:
    dockerfileExtension.getCommands() == ['EXPOSE 8080']
  }

  def "should add VOLUME with defined volume"() {
    when:
    dockerfileExtension.volume('/var/volume')
    then:
    dockerfileExtension.getCommands() == ['VOLUME /var/volume']
  }

  def "should add USER with defined user"() {
    when:
    dockerfileExtension.user('deamon')
    then:
    dockerfileExtension.getCommands() == ['USER deamon']
  }
}
