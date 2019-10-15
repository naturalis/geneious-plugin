# Naturalis Geneious Plugin (version 2.x)

This repository contains the Java source for the Naturalis Geneious Plugin. The Naturalis Geneious Plugin is a plugin for the [Geneious desktop application](https://www.geneious.com/). It provides the following operations (themselves known as "plugins" by most users):

- **AB1/Fasta Import**  Imports nucleotide sequence files of the AB1 and Fasta variety. Geneious already has this functionality straight out of the box, but the plugin also immediately adds some useful annotations to the documents Geneious created for the nucleotide sequences. These annotations are extracted from the name of the AB1 file c.q. the header within the Fasta file.
- **Split Name**  This is like the AB1/Fasta Import operation except that it operates on existing documents, imported via Geneious's own import facility.
- **Sample Sheet Import**  Adds metadata about DNA samples to existing nucleotide sequence documents. If desired, this operation will also create place-holder documents (a.k.a. "dummies") for DNA samples whose nucleotide sequences have yet to be imported into Geneious. As soon as the real sequence is imported it receives the annotations from the place-holder document, and the place-holder document is deleted.
- **CRS Import**  Adds annotations retrieved from CRS to existing nucleotide sequence documents.
- **BOLD Import**  Adds annotations retrieved from BOLD to existing nucleotide sequence documents.

The AB1/Fasta Import operation and the Split Name operations share a substantial code base in the [nl.naturalis.geneious.name](https://github.com/naturalis/geneious-plugin/tree/v2_master/importers/src/main/java/nl/naturalis/geneious/name) package. The Sample Sheet, CRS and BOLD operations are all creating annotations from the rows in a CSV file and/or spreadsheet. They also share a substantial code base in the [nl.naturalis.geneious.csv](https://github.com/naturalis/geneious-plugin/tree/v2_master/importers/src/main/java/nl/naturalis/geneious/csv) package.

## Version 1 vs. Version 2 of the Plugin
Version 2 of the plugin is a complete rewrite of the original plugin. It does not share any code with version 1 of the plugin. This repository is self-contained and has no dependencies on the other Geneious-related repositories.

## Developer Setup
To develop and build the plugin the following is required
1. Java 11 or higher
2. The [Geneious development kit](https://www.geneious.com/api-developers/)
3. Maven 3.6.1

## Building the Plugin
### Clone the git repository for the Geneious plugin
```git clone https://github.com/naturalis/geneious-plugin.git```
### Configure Maven (repository setup)
The Geneious plugin has a dependency on naturalis-common, an in-house Java utilities library. This dependency is resolved through the Naturalis Maven repository. This repository needs to be configured in Maven's settings.xml file. Edit settings.xml as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

	<servers>
		<server>
			<id>naturalis</id>
			<username>admin</username>
			<password>secret</password>
		</server>
	</servers>

	<profiles>
		<profile>
			<id>always-active</id>
			<activation>
				<activeByDefault>true</activeByDefault>
			</activation>
			<repositories>
				<repository>
					<id>naturalis</id>
					<name>Naturalis</name>
					<url>http://145.136.242.169:8081/repository/naturalis/</url>
				</repository>
			</repositories>
			<pluginRepositories>
				<pluginRepository>
					<id>naturalis</id>
					<name>Naturalis</name>
					<url>http://145.136.242.169:8081/repository/naturalis/</url>
				</pluginRepository>
			</pluginRepositories>
		</profile>
	</profiles>
</settings>
```

The password of the Maven repository manager (defined in the ```<server>``` element) can be found in bitwarden.

### Configure Maven (Geneious libraries)
The geneious plugin naturalis also has some dependencies on Geneious libraries. In order to make Maven pick these up properly, add the following extra lines to settings.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

	<servers>
		<server>
			<id>naturalis</id>
			<username>admin</username>
			<password>secret</password>
		</server>
	</servers>

	<profiles>
		<profile>
			<id>always-active</id>
			<activation>
				<activeByDefault>true</activeByDefault>
			</activation>
			<repositories>
				<repository>
					<id>naturalis</id>
					<name>Naturalis</name>
					<url>http://145.136.242.169:8081/repository/naturalis/</url>
				</repository>
			</repositories>
			<pluginRepositories>
				<pluginRepository>
					<id>naturalis</id>
					<name>Naturalis</name>
					<url>http://145.136.242.169:8081/repository/naturalis/</url>
				</pluginRepository>
			</pluginRepositories>
		</profile>
		<profile>
			<id>geneious</id>
			<properties>
				<!-- devkit -->
				<geneious.home>/home/ayco/apps/geneious-2019.2.1-devkit/examples/GeneiousFiles</geneious.home>
				<!-- regular installation -->
				<!--geneious.home>/home/ayco/Geneious_Prime</geneious.home-->
			</properties>
		</profile>
	</profiles>
	<activeProfiles>
		<activeProfile>geneious</activeProfile>
	</activeProfiles>
</settings>
```

The path specified in ```<geneious.home>``` should contain a "lib" directory containing the Geneious Java libraries. For the Geneious development kit this directory is called _GeneiousFiles_ (under the examples directory). You could, however, run your Maven build just as well against a regular Geneious installation, which contains the same lib directory. It's only when running Geneious from within Eclipse that it pays to use the development kit (see below).

### Build the plugin using maven

```mvn clean install```

Note that the pom file does not reside in the root of the git repository but in the ```importers``` directory underneath it, so run the mvn command from there.

## Distributing the Plugin
The final artifact representing the plugin is a zip file with a .gplugin extension, which can be installed from within the Geneious GUI (within Geneious, go to _Tools -> Plugins..._ and press the _Install plugin from a gplugin file..._ button). The zip file contains the main artifact (naturalis-geneious-plugin.jar) plus all its dependencies except those already present in Geneious's lib directory (however, see below).

The easiest way to build and publish the plugin is to run the [distribute.sh](https://github.com/naturalis/geneious-plugin/tree/v2_master/importers/distribute.sh) script in the importers directory. This script will:
+ build naturalis-geneious-plugin using maven
+ collect naturalis-geneious-plugin.jar and its dependencies into a single folder
+ zip the folder and move the zip file to the [distributable](https://github.com/naturalis/geneious-plugin/tree/v2_master/distributable) directory
+ execute a git commit/push (you will still have to provide your github credentials at this point)

```
# Publish a new version:
$ ./distribute.sh --publish V2.0.6
# Publish the current version again. Strictly speaking bad practice (did you change the code or not?), but allowed.
$ ./distribute.sh --publish
```

## Dependency Management
Although the plugin is built using Maven, Geneious itself is not. All of its dependencies are in its lib directory. To minimize the risk of _jar hell_, we try to keep our dependencies in lockstep with Geneious. Therefore there are a lot of system scope dependencies in the pom file (effectively hard-linking to the jar files in the lib directory). Sometimes, however, we do need another version of a library than Geneious provides us with. For example, we use Apache POI for spreadsheet reading, but this library requires a more recent version of commons-compress than the version in the Geneious lib directory. This is not a problem. Geneious sandboxes the plugin through the class loading mechanism, giving precedence to the libraries within the gplugin file over those in its own lib directory. So we just have to include our version of commons-compress in the gplugin file. It is a matter of trial and error to figure out which transitive dependencies you are then forced to include as well.

## Developing in Eclipse
- Import the GeneiousFiles directory inside the Geneious development kit. The GeneiousFiles directory actually is an Eclipse project that you can import using _File -> Import... -> General -> Existing Projects into Workspace_.
- Import naturalis-common using _File -> Import... -> Maven -> Existing Maven Projects_
- Import naturalis-geneious-plugin _File -> Import... -> Maven -> Existing Maven Projects_ (Again, the pom file does not reside in the root of the git repository but in the `importers` directory underneath it)

## Running from within Eclipse
You can run Geneious along with the plugin from within Eclipse. After you imported the naturalis-geneious-plugin project you should have a new Run Configuration called _naturalis-geneious-plugin (64 bit)_. The Run Configuration can be found as a regular Java application in _Run -> Run Configurations..._ This allows you the run Geneious along with the plugin straight away.

**Warning** As shown in the image below, always make sure the Maven-managed dependencies come before the /GeneiousFiles/lib dependencies. This allows you to use your own version of certain libraries.

![Run configuration](https://github.com/naturalis/geneious-plugin/blob/v2_master/docs/run-configuration.png)

## Technical Documentation
The javadocs for the plugin can be found here: [http://naturalis.github.io/geneious-plugin/v2/javadoc/](http://naturalis.github.io/geneious-plugin/v2/javadoc/)

The Javadocs for the Geneious API can be found inside the development kit or here: [https://assets.geneious.com/developer/geneious/javadoc/latest/index.html](https://assets.geneious.com/developer/geneious/javadoc/latest/index.html)


