# docker-microservices-gradle-plugin

[![Build Status](https://travis-ci.org/michalborek/docker-microservices-gradle-plugin.svg)](https://travis-ci.org/michalborek/docker-microservices-gradle-plugin) [![Coverage Status](https://coveralls.io/repos/michalborek/docker-microservices-gradle-plugin/badge.svg?branch=master)](https://coveralls.io/r/michalborek/docker-microservices-gradle-plugin?branch=master)

To see how to apply a plugin in your build.gadle [visit plugin page on Gradle Plugins site](https://plugins.gradle.org/plugin/pl.greenpath.gradle.docker.microservices).

This Gradle plugin allows to start many docker containers at once in specified order. Started dockers are linked 
together so they can communicate directly.

This plugin uses `docker` command (not a REST API). But in future releases it may change.

We created it because it was hard for us to start many microservices in development environment when using Spring Boot. 
This plugin was created for our convenience but we are open to any ideas that would make it useful for others.


To configure the plugin, use the `docker` extension block:

    docker {
      // port to be published to host (mandatory)
      port 8080

      // linked microservices, that should be dockerRun'ed and linked together with this one
      linkedMicroservices 'auth', 'orders', 'accounts'

      // name of the container (default: project.name with '/' replaced with '-')
      containerName 'microservice'

      // name of the image (default: project.name with '/' replaced with '-')
      imageName 'microservice'

      // extra arguments passed on the command line to docker run
      runExtraArgs '-v', '/host:/inside_docker'

      // run the container in background (default: true)
      runDetached true

      // define dockerfile manually
      dockerfile {
        from ubuntu:14.04
        expose 8080
        expose 9090
        env 'NAME', 'value'
        copy 'source', 'destination'
        workdir '/path/to/workdir'
        volume  '/var/volume'
        user 'deamon'
        add "some.jar", 'test.jar'
        cmd java "java -jar test.jar"
      }
      
      // use microservice template
      dockerfile microserviceTemplate

      // or you can mix these two approches
      dockerfile {
        template microserviceTemplate
        env 'name', 'value'
        ...
      }
    }

The microserviceTemplate shown above generates dockerfile that works well with default 
Spring Boot configuration, that is:

    FROM java:8
    EXPOSE port # by default docker.port attribute
    ADD jarFile '.'
    CMD java -jar $jarFile

where `jarFile` is a name generated as `projectName.projectVersion.jar`.

**Hint:** If you want to be dependant on all other projects (e.g. you have a web UI microservice that 
is dependent on any other. as `linkedMicroservices` value you can set:

    rootProject.getSubprojects().findAll { it.name != project.name }.collect {it.name}

**Note:** In current plugin implementation you need to copy this jar to `build/docker/` but we have it on to do list.


Feel free to post feature requests and bugs using GitHub.
