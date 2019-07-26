#!/bin/bash

# This script will create the distributable for the Naturalis Geneious plugin.
# The distributable is a zip file with extension .gplugin (that's how Geneious
# wants it). The zip file contains a single folder with the same name as the
# main plugin class: nl.naturalis.geneious.NaturalisGeneiousPlugin).

here=$(pwd)

# Constants
naturalis_plugin_version="2.0"
naturalis_common_version="1.0"
poi_version="4.0.1"
univocity_version="2.7.6"
expected_branch="v2_master"


# Check some preconditions:

curbranch="$(git rev-parse --abbrev-ref HEAD)"
if [ "${curbranch}" != "${expected_branch}" ]
then
  echo "Can only create distributable on branch ${expected_branch}"
  exit 1
fi

dirty="$(git status --porcelain)"
if [ ! -z "${dirty}" ]
then
  echo "Working directory not clean"
  echo ${dirty}
  exit 1
fi

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


echo
echo
echo "************************************************************************"
echo "Building nl.naturalis.common"
echo "************************************************************************"
naturalis_common_dir=$(realpath "${here}/../../nl.naturalis.common")
if [ ! -d "${naturalis_common_dir}" ]
then
  echo "Missing directory: ${naturalis_common_dir}"
  echo "Please clone the naturalis-common git repo first"
  exit 1
fi
cd ${naturalis_common_dir}
mvn clean install || exit 1


cd ${here}
if [ ! -z ${newtag} ]
then
	git tag -a "${newtag}" -m "${newtag}" || exit 1
	curtag="${newtag}"
fi
echo
echo
echo "************************************************************************"
echo "Building nl.naturalis.geneious"
echo "************************************************************************"
mvn clean install || exit 1


echo
echo
echo "************************************************************************"
echo "Assembling artifacts"
echo "************************************************************************"
assembly_dir="${here}/target/nl.naturalis.geneious.NaturalisGeneiousPlugin"
[ -d ${assembly_dir} ] && rm -rf ${assembly_dir}
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
created="$(date +%Y%m%d.%H%M)"
name="${curtag}.${created}.gplugin"
cd ${here}/target
zip -r ${name} nl.naturalis.geneious.NaturalisGeneiousPlugin
[ ${?} != 0 ] && exit 1


echo
echo
echo "************************************************************************"
echo "Pushing to github"
echo "************************************************************************"
git_repo=$(realpath ${here}/../)
mv "${here}/target/${name}" "${git_repo}/distributable/${name}"

cd ${git_repo}
if [ -z ${newtag} ]
then
	git add --all && git commit -m "Regenerated distributable for version ${curtag}" && git push
  [ ${?} != 0 ] && exit 1
else
	git add --all && git commit -m "Generated distributable for version ${curtag}" && git push && git push --tags
  [ ${?} != 0 ] && exit 1
fi

echo
echo
echo "Distributable: ${git_repo}/distributable/${name}"
exit 0