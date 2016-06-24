package pl.greenpath.gradle.bowertoprod

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ProductionFilesExtractorFunctionalTest extends Specification {

  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()

  private File buildFile

  private Project project

  def setup() {
    project = ProjectBuilder.builder().build()
    project.setBuildDir(testProjectDir.root)
  }

  def 'should extract production files from bower.json removing ./ if present'() {
    given:
    testProjectDir.newFile('bower.json') << getBowerJson('["./build/a.js", "build/b.js"]')
    ProductionFilesExtractor extractor = new ProductionFilesExtractor(testProjectDir.root.absolutePath + '/', project)
    expect:
    extractor.getProductionFiles() == ['build/a.js', 'build/b.js']
  }

  def 'should extract production files from bower.json when only one file exists'() {
    given:
    testProjectDir.newFile('bower.json') << getBowerJson('"./build/a.js"')
    ProductionFilesExtractor extractor = new ProductionFilesExtractor(testProjectDir.root.absolutePath + '/', project)
    expect:
    extractor.getProductionFiles() == ['build/a.js']

  }

  private static String getBowerJson(String files) {
    """
      {
       "main": ${files}
      }
    """
  }
}
