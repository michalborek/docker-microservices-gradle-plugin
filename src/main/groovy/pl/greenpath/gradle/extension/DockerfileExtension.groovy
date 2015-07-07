package pl.greenpath.gradle.extension

class DockerfileExtension {

  List<String> copyParameters = []
  List<String> envParameters = []
  List<String> addParameters = []

  private String fromParameter
  private String exposeParameter
  private String workDirParameter
  private String volumeParameter
  private String userParameter
  private String cmdParameter

  void from(String from) {
    fromParameter = "FROM $from"
  }

  void env(String key, String value) {
    envParameters << "ENV $key $value"
  }

  void add(String source, String destination) {
    addParameters << "ADD $source $destination"
  }

  void copy(String source, String destination) {
    copyParameters << "COPY $source $destination"
  }

  void workdir(String workingDir) {
    workDirParameter = "WORKDIR $workingDir"
  }

  void expose(int port) {
    exposeParameter = "EXPOSE $port"
  }

  void volume(String volumes) {
    volumeParameter = "VOLUME $volumes"
  }

  void user(String user) {
    userParameter = "USER $user"
  }

  void cmd(String Parameter) {
    cmdParameter = "CMD $Parameter"
  }
}
