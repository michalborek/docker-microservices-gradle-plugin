package pl.greenpath.gradle.bowertoprod

import groovy.json.JsonSlurper
import org.gradle.api.Project

class ProductionFilesExtractor {

  public static final String BOWER_JSON_RELATIVE_PATH = 'bower.json'

  private String libraryPath
  private Project project

  ProductionFilesExtractor(String libraryPath, Project project) {
    this.project = project
    this.libraryPath = libraryPath
  }

  public List<String> getProductionFiles(String buildDir = '') {
    File bowerJsonFile = project.file(libraryPath + BOWER_JSON_RELATIVE_PATH)

    def mainDefinition = new JsonSlurper().parse(bowerJsonFile)['main']
    List<String> productionFiles = mainDefinition instanceof String ? [mainDefinition] : mainDefinition
    return productionFiles*.replaceAll("^(\\./)?${buildDir ?: ''}(/)?", '')
  }
}
