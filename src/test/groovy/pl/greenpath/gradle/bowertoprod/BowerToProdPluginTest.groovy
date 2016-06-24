package pl.greenpath.gradle.bowertoprod

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class BowerToProdPluginTest extends Specification {


  def "should define a task for copying production dependencies"() {
    given:
    Project project = ProjectBuilder.builder().build()
    BowerToProdPlugin plugin = new BowerToProdPlugin()
    when:
    plugin.apply(project)
    then:
    project.tasks.copyBowerProductionDependencies instanceof DefaultTask
  }
}
