package pl.greenpath.gradle.bowertoprod.task

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin
import pl.greenpath.gradle.bowertoprod.BowerToProdExtension
import pl.greenpath.gradle.bowertoprod.ProductionFilesCopier

class CopyBowerProductionDependenciesTask extends DefaultTask {

  BowerToProdExtension bowerToProdExtension

  CopyBowerProductionDependenciesTask() {
    setGroup(LifecycleBasePlugin.BUILD_GROUP)
    bowerToProdExtension = project.getExtensions().getByType(BowerToProdExtension)

    project.afterEvaluate {
      inputs.dir project.file(getBowerFilesDirectory(project))
      bowerToProdExtension.getCustomDestinations().forEach { outputs.dir it }
      outputs.dir bowerToProdExtension.destination
    }
  }

  @TaskAction
  void copy() {
    String bowerComponentsDirectory = getBowerFilesDirectory(project)
    ProductionFilesCopier copier = new ProductionFilesCopier(bowerComponentsDirectory, project)
    getBowerDependencies(project).findAll {
      !bowerToProdExtension.isIgnored(it)
    }.forEach {
      copier.copy(it)
    }
  }

  String getBowerFilesDirectory(Project project) {
    String defaultDirectory = 'bower_components'
    File file = project.file('.bowerrc')
    if (!file.exists()) {
      return defaultDirectory
    }
    return new JsonSlurper().parse(file)['directory'] ?: defaultDirectory
  }

  private List<String> getBowerDependencies(Project project) {
    File file = project.file('bower.json')
    if (!file.exists()) {
      return []
    }

    Map dependencies = new JsonSlurper().parse(file)['dependencies']
    return dependencies.keySet().toList()
  }
}

