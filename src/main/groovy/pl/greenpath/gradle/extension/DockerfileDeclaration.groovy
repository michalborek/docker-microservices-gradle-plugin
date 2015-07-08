package pl.greenpath.gradle.extension

import org.gradle.api.Project

class DockerfileDeclaration {

  List<String> copyCommands = []
  List<String> envCommands = []
  List<String> addCommands = []

  private String fromCommand
  private String exposeCommand
  private String workDirCommand
  private String volumeCommand
  private String userCommand
  private String cmdCommand
  private Project project

  DockerfileDeclaration(Project project) {
    this.project = project
  }

  static Closure<DockerfileDeclaration> microserviceTemplate = {
    def jarFile = "${project.name}-${project.version}.jar"
    from "ubuntu:14.04"
    expose project.extensions['docker']['port']
    add jarFile, '.'
    cmd "java -jar $jarFile"

  }

  void from(String from) {
    fromCommand = "FROM $from"
  }

  void env(String key, String value) {
    envCommands << "ENV $key $value"
  }

  void add(String source, String destination) {
    addCommands << "ADD $source $destination"
  }

  void copy(String source, String destination) {
    copyCommands << "COPY $source $destination"
  }

  void workdir(String workingDir) {
    workDirCommand = "WORKDIR $workingDir"
  }

  void expose(int port) {
    exposeCommand = "EXPOSE $port"
  }

  void volume(String volumes) {
    volumeCommand = "VOLUME $volumes"
  }

  void user(String user) {
    userCommand = "USER $user"
  }

  void cmd(String command) {
    cmdCommand = "CMD $command"
  }

  String toDockerfile() {
    StringBuilder.newInstance().with {
      append printIfPresent(fromCommand)
      append printIfPresent(exposeCommand)
      append printIfPresent(workDirCommand)
      append printListIfPresent(envCommands)
      append printListIfPresent(addCommands)
      append printListIfPresent(copyCommands)
      append printIfPresent(volumeCommand)
      append printIfPresent(userCommand)
      append printIfPresent(cmdCommand)
    }
  }

  private String printIfPresent(String command) {
    command == null ? '' : command + '\n'

  }

  private String printListIfPresent(List<String> commands) {
    commands.isEmpty() ? '' : commands.join('\n') << '\n'

  }


}
