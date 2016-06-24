package pl.greenpath.gradle.bowertoprod

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static pl.greenpath.gradle.bowertoprod.BuildscriptClasspathDefinitionGenerator.generateBuildscriptClasspathDefinition

class BowerToProdPluginFunctionalTest extends Specification {

  public static final String COPY_TASK_NAME = ':copyBowerProductionDependencies'

  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()

  private File buildFile

  def setup() {
    File settingsFile = testProjectDir.newFile('settings.gradle')
    settingsFile << "rootProject.name = 'myProject'"
    buildFile = testProjectDir.newFile('build.gradle')
    buildFile << generateBuildscriptClasspathDefinition()
    testProjectDir.newFile('.bowerrc') << getBowerrc()
    testProjectDir.newFile('bower.json') << getMainBowerJson()
    testProjectDir.newFolder('app', 'components', 'almond')
    testProjectDir.newFolder('app', 'components', 'test', 'release')
    testProjectDir.newFile('app/components/almond/bower.json') << getLibBowerJson('["./build/a.js", "build/b.js"]')
    testProjectDir.newFile('app/components/test/bower.json') << getLibBowerJson('["release/a.js", "release/a.css"]')
    testProjectDir.newFile('app/components/test/release/a.js') << 'dummy'
    testProjectDir.newFile('app/components/test/release/a.css') << 'dummy'
    buildFile << '''
        apply plugin: 'pl.greenpath.gradle.bowertoprod'

        bowerToProd {
          destination file('dest')
        }
    '''
  }

  def 'should copy files defined as main files, skipping common directory prefix'() {
    given:
    createAlmodDirectory()
    when:
    runTask()
    then:
    fileExists('dest/almond/a.js')
    fileExists('dest/almond/b.js')
    fileExists('dest/test/a.js')
    fileExists('dest/test/a.css')
  }

  def 'should copy files defined to custom destination'() {
    given:
    createAlmodDirectory()
    buildFile << '''
        bowerToProd {
          lib name: 'almond', destination: 'test'
        }
    '''
    when:
    BuildResult buildResult = runTask()
    then:
    taskWasExecuted(buildResult)
    fileExists('test/a.js')
    fileExists('test/b.js')
  }

  def 'should skip consecutive builds, when nothing changed'() {
    given:
    createAlmodDirectory()
    when:
    BuildResult firstBuild = runTask()
    BuildResult secondBuild = runTask()
    then:
    taskWasExecuted(firstBuild)
    taskWasUpToDate(secondBuild)
  }

  def 'should not skip consecutive builds when custom destination changed between them'() {
    given:
    createAlmodDirectory()
    buildFile << '''
        bowerToProd {
          lib name: 'almond', destination: 'test'
        }
    '''
    when:
    BuildResult firstBuild = runTask()
    deleteCustomDestination()
    BuildResult secondBuild = runTask()
    then:
    taskWasExecuted(firstBuild)
    taskWasExecuted(secondBuild)
  }

  def 'should strip build dir if build dir defined in extension'() {
    given:
    createAlmodDirectory()
    buildFile << '''
        apply plugin: 'pl.greenpath.gradle.bowertoprod'

        bowerToProd {
          lib name: 'almond', buildDir: 'build'
        }
    '''
    when:
    BuildResult buildResult = runTask()
    then:
    taskWasExecuted(buildResult)
    fileExists('dest/almond/a.js')
    fileExists('dest/almond/b.js')
    fileExists('dest/test/a.js')
    fileExists('dest/test/a.css')
  }

  def 'should not copy dependencies defined as ignored'() {
    given:
    createAlmodDirectory()
    buildFile << '''
        bowerToProd {
          ignore 'test'
        }
    '''
    when:
    BuildResult buildResult = runTask()
    then:
    taskWasExecuted(buildResult)
    fileExists('dest/almond/a.js')
    fileExists('dest/almond/b.js')
    !fileExists('dest/test/a.js')
    !fileExists('dest/test/a.css')
  }

  private void createAlmodDirectory() {
    testProjectDir.newFolder('app', 'components', 'almond', 'build')
    testProjectDir.newFile('app/components/almond/build/b.js')
    testProjectDir.newFile('app/components/almond/build/a.js')
  }

  private boolean taskWasExecuted(BuildResult buildResult) {
    return buildResult.task(COPY_TASK_NAME).outcome == TaskOutcome.SUCCESS
  }

  private boolean fileExists(String projectRelativeFilePath) {
    new File(testProjectDir.getRoot(), projectRelativeFilePath).exists()
  }

  private boolean taskWasUpToDate(BuildResult secondBuild) {
    return secondBuild.task(COPY_TASK_NAME).outcome == TaskOutcome.UP_TO_DATE
  }

  private boolean deleteCustomDestination() {
    new File(testProjectDir.getRoot(), 'test').deleteDir()
  }

  private BuildResult runTask() {
    return GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments(COPY_TASK_NAME, '--stacktrace')
        .build()
  }

  private static String getBowerrc() {
    '''
      {
        "directory": "app/components/"
      }
    '''
  }

  private static String getMainBowerJson() {
    '''
      {
       "dependencies": {
         "almond": "~0.2.1",
         "test": "1.1.1"
        }
      }
    '''

  }

  private static String getLibBowerJson(String mainFiles) {
    return """
      {
       "main": ${mainFiles}
      }
    """
  }

}
