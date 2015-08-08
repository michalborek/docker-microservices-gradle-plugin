package pl.greenpath.gradle.task

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static pl.greenpath.gradle.BuildscriptClasspathDefinitionGenerator.generateBuildscriptClasspathDefinition

class GenerateDockerfileTaskFunctionalTest extends Specification {

  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()

  File buildFile

  def setup() {
    buildFile = testProjectDir.newFile('build.gradle')
    buildFile << generateBuildscriptClasspathDefinition()
  }

  def ':generateDockerfile task should create new dockerfile in build/docker directory'() {
    given:
    buildFile << '''
            apply plugin: 'pl.greenpath.gradle.docker.microservices'

            docker {
              dockerfile {
                from 'java:8'
                expose 9090
              }
            }
        '''
    when:
    BuildResult result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('generateDockerfile')
        .build()
    then:
    result.task(':generateDockerfile').outcome == SUCCESS
    result.standardError.isEmpty()
    new File(testProjectDir.root, 'build/docker/Dockerfile').with {
      !exists()
      text == '''FROM java:8
                |EXPOSE 9090
                |'''.stripMargin()
    }
  }
}
