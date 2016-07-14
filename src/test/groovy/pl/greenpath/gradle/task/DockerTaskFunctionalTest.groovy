package pl.greenpath.gradle.task

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static pl.greenpath.gradle.BuildScriptClasspathDefinitionGenerator.generateBuildScriptClasspathDefinition

class DockerTaskFunctionalTest extends Specification {

  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()

  File buildFile

  def setup() {
    def settingsFile = testProjectDir.newFile('settings.gradle')
    settingsFile << "rootProject.name = 'myProject'"
    buildFile = testProjectDir.newFile('build.gradle')
    buildFile << generateBuildScriptClasspathDefinition()
    buildFile << "apply plugin: 'pl.greenpath.gradle.docker.microservices'"
  }

  def 'should be able to create task of type DockerTask'() {
    given:
    buildFile << '''
            import pl.greenpath.gradle.task.DockerTask
            task testDocker(type: DockerTask) {
              args 'ps', '-a'
            }
        '''
    when:
    BuildResult result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('testDocker')
        .build()
    then:
    result.task(':testDocker').outcome == SUCCESS
  }
}
