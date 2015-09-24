package pl.greenpath.gradle.extension;

class DockerRunExtension {
  private boolean detached
  private List<String> extraArgs = []

  void detached(boolean runDetached) {
    this.detached = runDetached
  }

  void extraArgs(String... args) {
    this.extraArgs = args.toList()
  }

  List<String> getArguments() {
    ([getDetachedArgument()] + extraArgs).findAll { !it.empty }
  }


  private String getDetachedArgument() {
    detached ? '-d' : ''
  }
}
