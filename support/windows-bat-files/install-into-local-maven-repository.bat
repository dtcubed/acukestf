ECHO OFF

SET FILE_TO_INSTALL=target\acukestf-1.3-SNAPSHOT.jar

SET MAVEN_ARTIFACT_ID=acukestf

SET MAVEN_GROUP_ID=com.github.dtcubed.acukestf

SET MAVEN_PACKAGING=jar

SET MAVEN_VERSION=1.3-SNAPSHOT

ECHO "================================================"
ECHO "Installing Framework Into Local Maven Repository"
ECHO "================================================"
ECHO ON
mvn install:install-file -Dfile=%FILE_TO_INSTALL% -DgroupId=%MAVEN_GROUP_ID% -DartifactId=%MAVEN_ARTIFACT_ID% -Dversion=%MAVEN_VERSION% -Dpackaging=%MAVEN_PACKAGING%

GOTO AROUND
ECHO "Display Version Information for Maven"
mvn -version
:AROUND
