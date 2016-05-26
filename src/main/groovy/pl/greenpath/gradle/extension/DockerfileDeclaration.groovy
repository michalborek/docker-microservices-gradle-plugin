package pl.greenpath.gradle.extension

import groovy.transform.CompileStatic
import org.gradle.api.Project

@CompileStatic
class DockerfileDeclaration {

  private List<String> toCopy = []
  private List<String> environmentalVariables = []
  private List<String> toAdd = []
  private List<Integer> exposedPorts = []
  private List<String> runCommands = []

  private String baseImageName
  private String workingDir
  private String volume
  private String userVariable
  private String command
  private Project project
  private String stringBasedDockerfile

  DockerfileDeclaration(Project project) {
    this.project = project
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

  void expose(int ... ports) {
    ports.each { expose it as int }
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

  void run(String command) {
    this.runCommands << command
  }

  void stringBasedDockerfile(String dockerfile) {
    this.stringBasedDockerfile = dockerfile.readLines().collect({ it.trim() }).join('\n')
  }

  String toDockerfile() {
    stringBasedDockerfile ?: StringBuilder.newInstance().with {
      append printIfPresent('FROM', baseImageName)
      append printExposeList(exposedPorts)
      append printIfPresent('WORKDIR', workingDir)
      append printListIfPresent('ENV', environmentalVariables)
      append printListIfPresent('ADD', toAdd)
      append printListIfPresent('COPY', toCopy)
      append printListIfPresent('RUN', runCommands)
      append printIfPresent('VOLUME', volume)
      append printIfPresent('USER', userVariable)
      append printIfPresent('CMD', command)
    }
  }

  private static String printIfPresent(String commandName, String command) {
    return command == null ? '' : "$commandName $command\n"
  }

  private static String printListIfPresent(String commandName, List<String> commands) {
    return commands.isEmpty() ? '' : commands.collect { "$commandName $it" }.join('\n') << '\n'
  }

  private static String printExposeList(List<Integer> ports) {
    return ports.isEmpty() ? '' : "EXPOSE ${ports.join(' ')}" << '\n'
  }

}
