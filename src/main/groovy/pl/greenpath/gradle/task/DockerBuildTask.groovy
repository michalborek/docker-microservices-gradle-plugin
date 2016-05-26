package pl.greenpath.gradle.task

import groovy.transform.CompileStatic

@CompileStatic
class DockerBuildTask extends AbstractDockerTask {

  @Override
  protected void prepareExecution() {
    println 'Building image: ' + getImageName()
    args 'build', '-t', getImageName(), new File(project.buildDir, 'docker')
  }
}
