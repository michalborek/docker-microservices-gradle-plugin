package pl.greenpath.gradle.task.dev

import groovy.json.JsonSlurper
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification

import static pl.greenpath.gradle.BuildScriptClasspathDefinitionGenerator.generateBuildScriptClasspathDefinition

class DockerBootRunTaskFunctionalTest extends Specification {

  File buildFile

  File testAppDirectory

  def setup() {
    testAppDirectory = new File(getClass().getResource('/testApp').toURI())
    File settingsFile = new File(testAppDirectory, 'settings.gradle')
    settingsFile.deleteOnExit()
    settingsFile.createNewFile()
    settingsFile << "rootProject.name = 'testApp'"
    buildFile = new File(testAppDirectory, 'build.gradle')
    buildFile.createNewFile()
    buildFile.deleteOnExit()
    buildFile << generateBuildScriptClasspathDefinition()
  }

  def 'should run program with attached classpath'() {
    given:
    buildFile << '''
      buildscript {
        repositories {
          jcenter()
        }

        dependencies {
          classpath 'org.springframework:springloaded:1.2.5.RELEASE'
        }
      }
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
        .forwardOutput()
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
