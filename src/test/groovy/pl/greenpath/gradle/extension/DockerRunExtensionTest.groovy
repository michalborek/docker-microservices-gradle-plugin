package pl.greenpath.gradle.extension

import spock.lang.Specification

class DockerRunExtensionTest extends Specification {

  def 'should return a list of all defined arguments as one list'() {
    given:
    def extension = new DockerRunExtension()
    when:
    extension.with {
      extraArgs 'a', 'b'
      detached true
    }
    then:
    extension.getArguments() == ['-d', 'a', 'b']
  }

}
