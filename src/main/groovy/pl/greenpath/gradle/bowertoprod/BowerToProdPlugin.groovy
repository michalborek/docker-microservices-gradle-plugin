package pl.greenpath.gradle.bowertoprod

import org.gradle.api.Plugin
import org.gradle.api.Project
import pl.greenpath.gradle.bowertoprod.task.CopyBowerProductionDependenciesTask

class BowerToProdPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    BowerToProdExtension bowerToProdExtension = project.extensions.create('bowerToProd', BowerToProdExtension, project)

    project.task('copyBowerProductionDependencies', type: CopyBowerProductionDependenciesTask)
  }

}
