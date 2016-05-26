package pl.greenpath.gradle.task.dev

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

@CompileStatic
public class SourceSetFinder {

  private Project project

  SourceSetFinder(Project project) {
    this.project = project
  }

  public SourceSet findMainSourceSet() {
    JavaPluginConvention javaPlugin = project.getConvention().getPlugin(JavaPluginConvention.class)
    SourceSetContainer sourceSets = javaPlugin.getSourceSets()
    return sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME)
  }
}
