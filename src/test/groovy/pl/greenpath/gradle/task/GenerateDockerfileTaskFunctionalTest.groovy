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
    def settingsFile = testProjectDir.newFile('settings.gradle')
    settingsFile << "rootProject.name = 'myProject'"
    buildFile = testProjectDir.newFile('build.gradle')
    buildFile << generateBuildscriptClasspathDefinition()
    buildFile << "apply plugin: 'pl.greenpath.gradle.docker.microservices'"
  }

  def ':generateDockerfile task should create new dockerfile in build/docker directory'() {
    given:
    buildFile << '''
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

    File dockerfile = new File(testProjectDir.root, 'build/docker/Dockerfile')
    dockerfile.exists()
    dockerfile.text == '''FROM java:8
                |EXPOSE 9090
                |'''.stripMargin()

  }

  def 'should allow to use embedded parameters in string based dockerfile declaration'() {
    given:
    buildFile << '''
            version 1.1
            docker {
              dockerfile """
                FROM java:8
                EXPOSE 9090
                CMD java -jar ${project.name}-${project.version}.jar
              """
            }
        '''
    when:
    BuildResult result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('generateDockerfile')
        .build()
    then:
    result.task(':generateDockerfile').outcome == SUCCESS
    File dockerfile = new File(testProjectDir.root, 'build/docker/Dockerfile')
    dockerfile.exists()
    dockerfile.text == '''
                |FROM java:8
                |EXPOSE 9090
                |CMD java -jar myProject-1.1.jar
                |'''.stripMargin()

  }
}
