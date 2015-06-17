package pl.greenpath.gradle.extension

class DockerfileExtension {

  List<String> copyCommands = []
  List<String> envCommands = []
  List<String> addCommands = []

  private String exposeCommand
  private String fromCommand
  private String workDirCommand
  private String volumeCommand
  private String userCommand
  private String template

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

  void predefined(String templateName) {
    this.template = templateName
  }

  List<String> getCommands() {
    return (getAsList(fromCommand) + addCommands + copyCommands + envCommands + getAsList(exposeCommand) + getAsList(workDirCommand)
        + getAsList(volumeCommand) + getAsList(userCommand))
  }

  private List<String> getAsList(String command) {
    command != null ? [command] : []
  }

}
