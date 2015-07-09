package pl.greenpath.gradle.extension

import org.gradle.api.Project

class DockerfileDeclaration {

  private List<String> toCopy = []
  private List<String> environmentalVariables = []
  private List<String> toAdd = []
  private List<Integer> exposedPorts = []

  private String baseImageName
  private String workingDir
  private String volume
  private String userVariable
  private String command
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

  void template(Closure<DockerfileDeclaration> templateClosure) {
    with templateClosure
  }

  void from(String from) {
    baseImageName = from
  }

  void env(String key, String value) {
    environmentalVariables << "$key $value"
  }

  void add(String source, String destination) {
    toAdd << "$source $destination"
  }

  void copy(String source, String destination) {
    toCopy << "$source $destination"
  }

  void workdir(String workingDir) {
    this.workingDir = workingDir
  }

  void expose(int port) {
    if (!(port in exposedPorts)) {
      exposedPorts << port
    }
  }

  void volume(String volumes) {
    volume = volumes
  }

  void user(String user) {
    userVariable = user
  }

  void cmd(String command) {
    this.command = command
  }

  String toDockerfile() {
    StringBuilder.newInstance().with {
      append printIfPresent('FROM', baseImageName)
      append printInlineListIfPresent('EXPOSE', exposedPorts)
      append printIfPresent('WORKDIR', workingDir)
      append printListIfPresent('ENV', environmentalVariables)
      append printListIfPresent('ADD', toAdd)
      append printListIfPresent('COPY', toCopy)
      append printIfPresent('VOLUME', volume)
      append printIfPresent('USER', userVariable)
      append printIfPresent('CMD', command)
    }
  }

  private String printIfPresent(String commandName, String command) {
    command == null ? '' : "$commandName $command\n"
  }

  private String printListIfPresent(String commandName, List<String> commands) {
    commands.isEmpty() ? '' : commands.collect { "$commandName $it" }.join('\n') << '\n'
  }

  private String printInlineListIfPresent(String commandName, List<String> commands) {
    commands.isEmpty() ? '' : "$commandName ${commands.join(' ')}" << '\n'
  }

}
