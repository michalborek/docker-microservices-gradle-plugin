package pl.greenpath.gradle.task
import org.gradle.testfixtures.ProjectBuilder
import pl.greenpath.gradle.DockerPlugin

class DockerPushTaskTest extends AbstractDockerTaskTest {

    static final String TASK_NAME = 'dockerPush'
    static final String PROJECT_NAME = 'testProject'

    @Override
    String getTaskName() {
        return TASK_NAME
    }

    def setup() {
        rootProject = ProjectBuilder.builder().withName(PROJECT_NAME).build()
        new DockerPlugin().apply(rootProject)
    }

    def "should run 'docker push' command with name of the image"() {
        given:
        AbstractDockerTask pushTask = getMockedTask()
        rootProject.buildDir = new File('/tmp')
        when:
        pushTask.exec()
        then:
        pushTask.getArgs() == ['push', PROJECT_NAME]
    }
}
