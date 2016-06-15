package pl.greenpath.gradle

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import pl.greenpath.gradle.extension.DockerExtension
import pl.greenpath.gradle.task.*
import pl.greenpath.gradle.task.dev.DockerBootRunTask

/**
 * This plugin is eases usage of docker with microservices.
 *
 * <p>Using this plugin it is possible to automatically invoke all tasks starting fromParameter
 * creating an image, a container and finishing with running it.</p>
 *
 * <p>It is also possible to restart a service and during that operation previous containers
 * and images are removed.</p>
 *
 * <p>When one microservice is dependent on another, the latter one is always run first.</p>
 *
 * <p><b>Note:</b> By now there is a need to have a "docker" directory with Dockerfile in it.
 * This file is copied along with a jar generated in "libs" directory into 'build/docker' directory.
 * All operations on docker are invoked on that directory.</p>
 */
@CompileStatic
class DockerPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    attachExtensions project

    project.task('generateDockerfile', type: GenerateDockerfileTask)
    createCopyJarToDockerDirTask(project)
    project.task('dockerStop', type: DockerStopTask)
    project.task('dockerLogs', type: DockerInteractiveLogTask)
    project.task('dockerRun', type: DockerRunTask, dependsOn: 'dockerBuild')
    project.task('dockerBootRun', type: DockerBootRunTask, dependsOn: ['dockerRemoveContainer', 'assemble'])
    project.task('dockerRunSingle', type: DockerRunTask, dependsOn: 'dockerBuild')
    project.task('dockerRemoveContainer', type: DockerRemoveContainerTask, dependsOn: 'dockerStop')
    project.task('dockerRemoveImage', type: DockerRemoveImageTask, dependsOn: 'dockerRemoveContainer')
    project.task('dockerBuild', type: DockerBuildTask, dependsOn:
        ['dockerRemoveImage', 'generateDockerfile', 'copyJarToDockerDir'])
    project.task('dockerPush', type: DockerPushTask, dependsOn: 'dockerBuild')
    project.afterEvaluate {
      configureDependantTasks project
    }
  }

  @CompileDynamic
  private void createCopyJarToDockerDirTask(Project project) {
    project.task('copyJarToDockerDir', type: Copy, dependsOn: 'assemble') {
      from(new File(project.buildDir, 'libs')) {
        include "${project.name}-${project.version}.jar"
      }
      into new File(project.buildDir, 'docker')
    }
  }

  protected static void configureDependantTasks(Project project) {
    project.getTasksByName('dockerRun', false).each {
      it.dependsOn project.extensions['docker']['linkedMicroservices'].collect {
        project.getRootProject().findProject(it as String).getTasksByName('dockerRun', false)
      }
    }
  }

  private static void attachExtensions(Project project) {
    project.extensions.create('docker', DockerExtension, project)
  }
}









