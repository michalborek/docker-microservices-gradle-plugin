# docker-microservices-gradle-plugin

[![Build Status](https://travis-ci.org/michalborek/docker-microservices-gradle-plugin.svg)](https://travis-ci.org/michalborek/docker-microservices-gradle-plugin)

This is a first draft of gradle plugin that allows to start many docker containers at once in specified order.

Usage TBD

To configure the plugin, use the 'docker' extension block:

    docker {
      //port to be published to host (mandatory)
      port = 8080

      //linked microservices, that should be dockerRun'ed and linked together with this one
      linkedMicroservices = rootProject.getSubprojects().findAll {
        it.name.startsWith('ms-')
      }.collect {it.name}

      //name of the container (default: project.name with '/' replaced with '-')
      containerName = 'microservice'

      //name of the image (default: project.name with '/' replaced with '-')
      imageName = 'microservice'

      //extra arguments passed on the command line to docker run
      runExtraArgs = ['-v', '/host:/inside_docker']

      //run the container in background (default: true)
      runDetached = true
    }
