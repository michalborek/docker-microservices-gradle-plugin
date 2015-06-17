package pl.greenpath.gradle.task

class DockerBuildTask extends AbstractDockerTask {

  @Override
  protected void prepareExecution() {
    println 'Building image: ' + getImageName()
    args 'build', '-t', getImageName(), new File(project.buildDir, 'docker')
  }
}
