#!/bin/bash

# This script will create the distributable for the Naturalis Geneious plugin.
# The distributable is a zip file with extension .gplugin (that's how Geneious
# wants it). The zip file contains a single folder with the same name as the
# main plugin class: nl.naturalis.geneious.NaturalisGeneiousPlugin).
#
# USAGE: distribute.sh [-p|--publish] [git_tag]


# Don't allow builds from non-master branches
expected_branch="v2_master"
# Force users to specify a version (i.e. tag) that starts with this string
expected_version="V2."


# Library versions
commons_compress_version="1.18"
xml_beans_version="3.0.2"
poi_version="4.0.1"
univocity_version="2.7.6"

here=$(pwd)

publish=""
version=""

for arg in "${@}"; do
  if [[ ${arg} = '-p' || ${arg} = '--publish' ]]; then
    publish=1
  else
  	version="${arg}"
  fi
done

curbranch="$(git rev-parse --abbrev-ref HEAD)"
if [ "${curbranch}" != "${expected_branch}" ]
then
  echo "Publishing only allowed on branch ${expected_branch}"
  exit 1
fi

dirty="$(git status --porcelain)"
if [ "${publish}" -a "${dirty}" ]
then
  echo "Working directory not clean"
  echo ${dirty}
  exit 1
fi

if [ "${version}" -a "${version:0:3}" != ${expected_version} ]
then
  echo "Invalid version: ${version}. Versions must start with \"${expected_version}\""
  exit 1
fi

curversion="$(git describe --abbrev=0 --tags)"
if [ -z ${version} ] 
then
  read -p "Do you wish to re-generate the distributable for version ${curversion}? [Y/n] " answer
  [ "${answer}" = "n" -o "${answer}" = "N" ] && exit 0
else
  read -p "Do you wish to create a distributable for version ${version}? [Y/n] " answer
  [ "${answer}" = "n" -o "${answer}" = "N" ] && exit 0
fi


cd ${here}
if [ ! -z ${version} ]
then
  # Need to do this before maven install, so git commit id plugin
  # picks up the new version
	git tag -a "${version}" -m "${version}" || exit 1
	curversion="${version}"
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
echo "Assembling plugin"
echo "************************************************************************"
assembly_dir="${here}/target/nl.naturalis.geneious.NaturalisGeneiousPlugin"
[ -d ${assembly_dir} ] && rm -rf ${assembly_dir}
mkdir ${assembly_dir}
cd "${here}/target"

#Don't quote *.jar !! Will result in error
mv *.jar "${assembly_dir}/"
[ ${?} != 0 ] && exit 1

cd "${here}"
mvn dependency:copy-dependencies "-DexcludeScope=system" "-DoutputDirectory=${assembly_dir}"
[ ${?} != 0 ] && exit 1

#mvn dependency:copy "-Dartifact=nl.naturalis.common:naturalis-common:${naturalis_common_version}" "-DoutputDirectory=${assembly_dir}"
#[ ${?} != 0 ] && exit 1
#mvn dependency:copy "-Dartifact=org.apache.poi:poi:${poi_version}" "-DoutputDirectory=${assembly_dir}"
#[ ${?} != 0 ] && exit 1
#mvn dependency:copy "-Dartifact=org.apache.poi:poi-ooxml:${poi_version}" "-DoutputDirectory=${assembly_dir}"
#[ ${?} != 0 ] && exit 1
#mvn dependency:copy "-Dartifact=org.apache.poi:poi-ooxml-schemas:${poi_version}" "-DoutputDirectory=${assembly_dir}"
#[ ${?} != 0 ] && exit 1
#mvn dependency:copy "-Dartifact=org.apache.commons:commons-compress:${commons_compress_version}" "-DoutputDirectory=${assembly_dir}"
#[ ${?} != 0 ] && exit 1
#mvn dependency:copy "-Dartifact=org.apache.xmlbeans:xmlbeans:${xml_beans_version}" "-DoutputDirectory=${assembly_dir}"
#[ ${?} != 0 ] && exit 1
#mvn dependency:copy "-Dartifact=com.univocity:univocity-parsers:${univocity_version}" "-DoutputDirectory=${assembly_dir}"
#[ ${?} != 0 ] && exit 1

echo
echo
echo "************************************************************************"
echo "Creating distributable"
echo "************************************************************************"
created="$(date +%Y%m%d%H%M)"
name="${curversion}-${created}.gplugin"
cd ${here}/target
zip -r ${name} nl.naturalis.geneious.NaturalisGeneiousPlugin
[ ${?} != 0 ] && exit 1

if [[ -z ${publish} ]]; then
  echo "Distributable: ${here}/target/${name}"
  exit 0
fi

echo
echo
echo "************************************************************************"
echo "Pushing to github"
echo "************************************************************************"
git_repo=$(realpath ${here}/../)
mv "${here}/target/${name}" "${git_repo}/distributable/${name}"

cd ${git_repo}
if [[ -z ${version} ]]; then
	git add --all && git commit -m "Regenerated distributable for version ${curversion}" && git push
	[ ${?} != 0 ] && exit 1
else
	git add --all && git commit -m "Generated distributable for version ${curversion}" && git push && git push --tags
	[ ${?} != 0 ] && exit 1
fi
echo "Distributable: ${git_repo}/distributable/${name}"
exit 0