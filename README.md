# Naturalis Geneious Plugin

This repository contains the Java source for the Naturalis Geneious Plugin. The Naturalis Geneious Plugin is a plugin for the Geneious
desktop application.

## Developer setup
To develop and build the plugin the following is required
1. Java 11 or higher
2. Geneious Prime 2019.2.1 or higher (you can also use the Geneious development kit, but it is not a required for plugin development)
3. Maven 3.6.1

## Building the plugin
1. Clone this repository.
2. Clone the [naturalis-common](https://github.com/naturalis/sd_java__common_lang) repository.
3. Open the [pom file](importers/pom.xml)
4. Locate the `geneious.home` property near the top of the pom file and make it point to your Geneious installation. If you are using the Geneious development kit, make it point to the "GeneiousFiles" folder inside it. In either case the directory should contain a "lib" dir containing Geneious's dependencies.
5. Build the naturalis-common library using maven
6. Build the plugin using maven. Note that the pom file does not reside in the root of the git repository but in the `importers` directory underneath it.

## Distributing the plugin
The easiest way to build and publish the plugin is to run the [distribute.sh script](importers/distribute.sh) in the importers directory.


## Technical Documentation
The javadocs can be found here: [http://naturalis.github.io/sd_java__geneious_plugin/v2/javadoc/](http://naturalis.github.io/sd_java__geneious_plugin/v2/javadoc/)


