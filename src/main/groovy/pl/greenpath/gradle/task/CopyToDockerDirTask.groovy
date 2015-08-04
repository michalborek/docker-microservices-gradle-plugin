package pl.greenpath.gradle.task

import org.gradle.api.tasks.Copy

public class CopyToDockerDirTask extends Copy {

  @Override
  protected void copy() {
    List<File> filesToCopy = getProject().extensions.docker.copyToDockerDir
    if (filesToCopy.empty) {
      from(new File(project.buildDir, 'libs')) {
        include "${project.name}-${project.version}.jar"
      }
    } else {
      filesToCopy.each {
        owner.from(it)
      }
    }
    into new File(project.buildDir, 'docker')
    super.copy()
  }
}
