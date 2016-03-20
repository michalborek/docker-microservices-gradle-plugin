package pl.greenpath.gradle.task.dev

import groovy.json.JsonSlurper
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification

import static pl.greenpath.gradle.BuildscriptClasspathDefinitionGenerator.generateBuildscriptClasspathDefinition

class DockerBootRunTaskFunctionalTest extends Specification {

  File buildFile

  File testAppDirectory

  def setup() {
    testAppDirectory = new File(getClass().getResource('/testApp').toURI())
    File settingsFile = new File(testAppDirectory, 'settings.gradle')
    settingsFile.deleteOnExit()
    settingsFile.createNewFile()
    settingsFile << "rootProject.name = 'myProject'"
    buildFile = new File(testAppDirectory, 'build.gradle')
    buildFile.createNewFile()
    buildFile.deleteOnExit()
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
        .withProjectDir(testAppDirectory)
        .withDebug(true)
        .withArguments('dockerRemoveImage', 'clean', 'build', 'dockerBootRun', '--stacktrace')
        .build()
    then:
    result.tasks.first().outcome == TaskOutcome.SUCCESS
    Thread.sleep(1000)
    Process process = new ProcessBuilder().command('docker', 'ps', '-a').start()
    process.waitFor()
    println process.errorStream.text
    println process.inputStream.text
    new JsonSlurper().parse(process.inputStream)[0]['State']['ExitCode'] == 0
  }
}
