package pl.greenpath.gradle.extension

class DockerfileGenerator {

  List<String> copyCommands = []
  List<String> envCommands = []
  List<String> addCommands = []

  private String fromCommand
  private String exposeCommand
  private String workDirCommand
  private String volumeCommand
  private String userCommand
  private String cmdCommand

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

  private List<String> getAsList(String command) {
    command != null ? [command] : []
  }

  public String toDockerfile(DockerfileGenerator generator) {
    StringBuilder.newInstance().with {
      append printIfPresent(owner.fromCommand, generator.&from)
      append printIfPresent(owner.exposeCommand, generator.&expose)
      append printIfPresent(owner.workDirCommand, generator.&workdir)
      append printListIfPresent(owner.envCommands, generator.&env)
      append printListIfPresent(owner.addCommands, generator.&add)
      append printListIfPresent(owner.copyCommands, generator.&copy)
      append printIfPresent(owner.volumeCommand, generator.&volume)
      append printIfPresent(owner.userCommand, generator.&user)
      append printIfPresent(owner.cmdCommand, generator.&cmd)
    }
  }

  private void printIfPresent(String command, Closure<String> method) {
    if (command != null) {
      method(command)
    }
  }

  private void printListIfPresent(List<String> commands, Closure, method) {
    if (!commands.isEmpty()) {
      commands.forEach({ method(it) })
    }

  }

}
