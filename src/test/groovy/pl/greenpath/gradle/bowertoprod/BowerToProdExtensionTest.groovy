package pl.greenpath.gradle.bowertoprod

import spock.lang.Specification

class BowerToProdExtensionTest extends Specification {

  BowerToProdExtension extension

  def setup() {
    extension = new BowerToProdExtension()
  }

  def 'should store customizations for given files'() {
    when:
    extension.lib name: 'angular', buildDir: 'build', includes: ['angular.js'], destination: 'dest'
    then:
    LibraryDefinition angularLib = extension.getCustomization('angular')
    angularLib.name == 'angular'
    angularLib.buildDir == 'build'
    angularLib.includes == ['angular.js']
    angularLib.destination == 'dest'
  }

  def 'should store ignored dependencies'() {
    when:
    extension.ignore 'a', 'b', 'c'
    then:
    extension.isIgnored('a')
    extension.isIgnored('b')
    extension.isIgnored('c')
    extension.isIgnored('d') == false
  }

  def 'should add ignored dependencies when invoking "ignore" many times'() {
    when:
    extension.ignore 'a', 'b'
    extension.ignore 'c', 'd'
    then:
    extension.isIgnored('a')
    extension.isIgnored('b')
    extension.isIgnored('c')
    extension.isIgnored('d')
    extension.isIgnored('e') == false
  }

  def 'should set destination dir'() {
    when:
    extension.destination new File('abc')
    then:
    extension.destination.name == 'abc'
  }

  def 'should strip build dir from includes'() {
    when:
    extension.lib name: 'angular', buildDir: 'build', includes: ['./build/angular.js', 'build/angular2.js']
    then:
    extension.getCustomization('angular').getCustomFiles() == ['angular.js', 'angular2.js']
  }
}
