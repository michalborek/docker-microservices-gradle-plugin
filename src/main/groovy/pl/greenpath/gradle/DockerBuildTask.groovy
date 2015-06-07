package pl.greenpath.gradle

class DockerBuildTask extends AbstractDockerTask {

  @Override
  protected void prepareExecution() {
    println 'Building image: ' + getImageName()
    super.args 'build', '-t', getImageName(), new File(project.buildDir, 'docker')
  }
}
