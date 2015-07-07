package pl.greenpath.gradle

import org.gradle.api.Project
import pl.greenpath.gradle.extension.DockerfileGenerator

/**
 * A Docker plugin that has a predefined Dockerfile structure.
 *
 * The structure may be changed using dockerfile extension (@see DockerfileExtension)
 */
class DockerBootPlugin extends DockerPlugin {


  @Override
  void apply(Project project) {
    super.apply(project)
    project.getTasksByName("generateDockerfile", true)*.template(springBootTemplate())
  }

  public static Closure<DockerfileGenerator> springBootTemplate() {
    Closure<DockerfileGenerator> closure = {
      def jarFile = "${owner.project.name}-${owner.project.version}.jar"
      delegate.from "ubuntu:14.04"
      delegate.expose owner.project.extensions.docker.port
      delegate.add jarFile, '.'
      delegate.cmd "java -jar $jarFile"
    }
    closure
  }
}
