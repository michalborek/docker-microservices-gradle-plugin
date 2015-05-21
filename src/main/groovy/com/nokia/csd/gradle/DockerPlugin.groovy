package com.nokia.csd.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

class DockerPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    project.extensions.create("docker", DockerExtension)

    project.task('copyDockerfile', type: Copy, dependsOn: 'assemble') {
      from('docker')
      from(new File(project.buildDir, 'libs')) {
        include "${project.name}-${project.version}.jar"
      }
      into new File(project.buildDir, 'docker')
    }
    project.task('dockerStop', type: DockerStopTask)
    project.task('dockerLogs', type: DockerInteractiveLogTask)
    project.task('dockerRun', type: DockerRunTask, dependsOn: 'dockerBuild')
    project.task('dockerRunSingle', type: DockerRunTask, dependsOn: 'dockerBuild')
    project.task('dockerRemoveContainer', type: DockerRemoveContainerTask, dependsOn: 'dockerStop')
    project.task('dockerRemoveImage', type: DockerRemoveImageTask, dependsOn: 'dockerRemoveContainer')
    project.task('dockerBuild', type: DockerBuildTask, dependsOn: ['dockerRemoveImage',
                                                                   'copyDockerfile'])
    project.afterEvaluate {
      configureDependantTasks(project)
    }
  }

  private void configureDependantTasks(Project project) {
    project.getTasksByName('dockerRun', false).each {
      it.dependsOn project.extensions.docker.linkedMicroservices.collect {
        project.getRootProject().findProject(it).getTasksByName('dockerRun', false)
      }
    }
  }


}









