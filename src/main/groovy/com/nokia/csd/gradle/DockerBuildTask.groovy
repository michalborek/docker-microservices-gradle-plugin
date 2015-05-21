package com.nokia.csd.gradle

import org.gradle.api.tasks.TaskAction

class DockerBuildTask extends AbstractDockerTask {

  @TaskAction
  protected void exec() {
    println 'Building image: ' + getImageName()
    super.args 'build', '-t', getImageName(), new File(project.buildDir, 'docker')
    super.exec()
  }
}
