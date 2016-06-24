package pl.greenpath.gradle.bowertoprod

import org.apache.commons.lang.StringUtils
import org.gradle.api.Project

class ProductionFilesCopier {

  private String bowerComponentsPath
  private Project project
  private BowerToProdExtension bowerToProdExtension

  ProductionFilesCopier(String bowerComponentsPath, Project project) {
    this.project = project
    this.bowerComponentsPath = bowerComponentsPath
    bowerToProdExtension = project.getExtensions().getByType(BowerToProdExtension)
  }

  void copy(final String libraryName) {
    List<String> filesToCopy = getFilesToCopy(libraryName)
    String libraryPath = getLibraryPath(libraryName)
    String buildDir = bowerToProdExtension.getBuildDirPath(libraryName)
    File destinationDirectory = getDestination(libraryName)
    File sourcesDirectory = new File(project.file(libraryPath), buildDir)
    (sourcesDirectory, filesToCopy) = stripCommonPrefixDirectory(sourcesDirectory, filesToCopy)
    doCopy(destinationDirectory, sourcesDirectory, filesToCopy)
  }

  private Tuple2<File, List<String>> stripCommonPrefixDirectory(File sourcesDirectory, List<String> filesPahtsToCopy) {
    List<String> parentDirectoriesToCopy = filesPahtsToCopy.collect {
      URI parentDirectoryUri = new File(sourcesDirectory, it).getParentFile().toURI()
      sourcesDirectory.toURI().relativize(parentDirectoryUri).getPath()
    }

    if (filesPahtsToCopy.size() > 1) {
      String commonPrefix = StringUtils.getCommonPrefix(parentDirectoriesToCopy.toArray(new String[parentDirectoriesToCopy.size()]))
      if (!commonPrefix.isEmpty()) {
        return [new File(sourcesDirectory, commonPrefix),
                filesPahtsToCopy.collect { StringUtils.removeStart(it, commonPrefix) }]
      }
    } else if (filesPahtsToCopy.size() == 1) {
      File fileToCopy = new File(sourcesDirectory, filesPahtsToCopy.first())
      if (fileToCopy.exists()) {
        return [fileToCopy.parentFile, [fileToCopy.getName()]]
      }
    }
    return [sourcesDirectory, filesPahtsToCopy]
  }

  private void doCopy(final File destination, final File sourcesDirectory, final List<String> filesToCopy) {
    new AntBuilder().copy(todir: destination) {
      fileset(dir: sourcesDirectory) {
        for (String path : filesToCopy) {
          include(name: path)
        }
      }
    }
  }

  private List<String> getFilesToCopy(String libraryName) {
    ProductionFilesExtractor filesExtractor = new ProductionFilesExtractor(getLibraryPath(libraryName), project)
    if (!bowerToProdExtension.hasCustomization(libraryName)) {
      return filesExtractor.getProductionFiles()
    }
    LibraryDefinition customization = bowerToProdExtension.getCustomization(libraryName)
    if (customization.customFiles.empty) {
      return filesExtractor.getProductionFiles(customization.buildDir)
    }
    return customization.getCustomFiles()

  }

  private String getLibraryPath(String libraryName) {
    return bowerComponentsPath + '/' + libraryName + '/'
  }

  private File getDestination(String libraryName) {
    LibraryDefinition customization = bowerToProdExtension.getCustomization(libraryName)
    if (customization != null && customization.destination != null) {
      return project.file(customization.destination)
    }

    return new File(bowerToProdExtension.destination, libraryName)
  }

}
