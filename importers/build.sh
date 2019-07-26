#!/bin/bash

naturalis_plugin_version="2.0"
naturalis_common_version="1.0"
poi_version="4.0.1"
univocity_version="2.7.6"

curtag="$(git describe --abbrev=0 --tags)"
newtag="${1}"
if [ -z ${newtag} ] 
then
  read -p "Do you wish to regenerate the distributable for ${curtag}? [Y/n] " answer
  if [ "${answer}" = "n" -o "${answer}" = "N" ]
  then
    echo "Please provide a tag (USAGE: ${0} <new_tag>)"
    exit 0
  fi
fi


here=$(pwd)
# Geneious expects a zip file with extension .gplugin containing one folder
# with the same name as the fully-qualified name of the main plugin class:
assembly_dir="${here}/target/nl.naturalis.geneious.NaturalisGeneiousPlugin"
naturalis_common_dir="${here}/../../nl.naturalis.common"


echo
echo
echo "************************************************************************"
echo "Building nl.naturalis.common"
echo "************************************************************************"
cd ${naturalis_common_dir}
mvn clean install
[ ${?} != 0 ] && exit 1

cd ${here}
if [ ! -z ${newtag} ] 
then
	git tag -a "${newtag}" -m "${newtag}"
	[ ${?} != 0 ] && exit 1
	curtag="${newtag}"
fi
echo
echo
echo "************************************************************************"
echo "Building nl.naturalis.geneious"
echo "************************************************************************"
mvn clean install
[ ${?} != 0 ] && exit 1


echo
echo
echo "************************************************************************"
echo "Assembling artifacts"
echo "************************************************************************"	
if [ -d ${assembly_dir} ]
then
  rm -rf ${assembly_dir}
fi
mkdir ${assembly_dir}
mv "${here}/target/naturalis-geneious-plugin-${naturalis_plugin_version}.jar" "${assembly_dir}/"
[ ${?} != 0 ] && exit 1
mvn dependency:copy "-Dartifact=nl.naturalis.common:naturalis-common:${naturalis_common_version}" "-DoutputDirectory=${assembly_dir}"
[ ${?} != 0 ] && exit 1
mvn dependency:copy "-Dartifact=org.apache.poi:poi:${poi_version}" "-DoutputDirectory=${assembly_dir}"
[ ${?} != 0 ] && exit 1
mvn dependency:copy "-Dartifact=org.apache.poi:poi-ooxml:${poi_version}" "-DoutputDirectory=${assembly_dir}"
[ ${?} != 0 ] && exit 1
mvn dependency:copy "-Dartifact=com.univocity:univocity-parsers:${univocity_version}" "-DoutputDirectory=${assembly_dir}"
[ ${?} != 0 ] && exit 1


echo
echo
echo "************************************************************************"
echo "Creating distributable"
echo "************************************************************************"
created="$(date +%Y%m%d%H%M)"
name="nbc-geneious-plugin.${curtag}.${created}.gplugin"
cd ${here}/target
zip -r ${name} nl.naturalis.geneious.NaturalisGeneiousPlugin

exit 0