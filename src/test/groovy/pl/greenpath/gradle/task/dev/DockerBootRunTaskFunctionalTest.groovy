package pl.greenpath.gradle.task.dev

import groovy.json.JsonSlurper
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static pl.greenpath.gradle.BuildscriptClasspathDefinitionGenerator.generateBuildscriptClasspathDefinition

class DockerBootRunTaskFunctionalTest extends Specification {

  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()

  File buildFile

  def setup() {
    def settingsFile = testProjectDir.newFile('settings.gradle')
    settingsFile << "rootProject.name = 'myProject'"
    buildFile = testProjectDir.newFile('testApp.build.gradle')
    buildFile << generateBuildscriptClasspathDefinition()
  }

  def 'should run program with attached classpath'() {
    given:
    buildFile << '''
      apply plugin: 'pl.greenpath.gradle.docker.microservices'
      apply plugin: 'java'
      apply plugin: 'application'

      repositories {
        mavenCentral()
      }
      dependencies {
        compile 'com.google.guava:guava:19.0'
      }

      sourceSets {
        main {
          java {
            srcDir 'src/main/java'
          }
          resources {
            srcDir 'src/main/resources'
          }
        }
      }

      mainClassName = 'TestApp'
    '''
    when:
    BuildResult result = GradleRunner.create()
        .withProjectDir(new File(getClass().getResource('/testApp').toURI()))
        .withArguments('dockerRemoveImage', 'clean', 'build', 'dockerBootRun', '--stacktrace')
        .build()
    then:
    result.tasks.first().outcome == TaskOutcome.SUCCESS
    Thread.sleep(1000)
    Process process = new ProcessBuilder().command('docker', 'inspect', 'testApp').start()
    process.waitFor() == 0
    new JsonSlurper().parse(process.inputStream)[0]['State']['ExitCode'] == 0
  }
}
