import continuum
import os
import shutil
import sys
from os import linesep
from time import sleep

def fail( message ):
    assert 0, message

def assertEquals( message, expected, actual ):
    if ( expected == None and actual != None ):
        assert 0, "Expected None but the actual value was: '" + actual + "'."

    if ( expected != None and actual == None ):
        assert 0, "Expected '" + expected + "' but the actual value None."

    assert expected == actual, message + linesep +\
                               "Expected " + expected + linesep +\
                               "Actual " + actual

def assertTrue( message, condition ):
    assert condition, message

def assertNotNull( message, condition ):
    assert condition != None, message

def assertProject( id, name, nagEmailAddress, state, version, builderId, project ):
    assertNotNull( "project.id", id )
    assertEquals( "project.name", name, project.name )
    assertEquals( "project.nagEmailAddress", nagEmailAddress, project.nagEmailAddress )
    assertEquals( "project.state", state, project.state )
    assertEquals( "project.version", version, project.version )
    assertEquals( "project.builderId", builderId, project.builderId )

def assertSuccessfulMaven1Build( buildId ):
    build = waitForBuild( buildId )
    buildResult = continuum.getBuildResult( buildId )
    assertTrue( "The build wasn't successful", buildResult.success )
    assertTrue( "Standard output didn't containe the 'BUILD SUCCESSFUL' message.", buildResult.standardOutput.find( "BUILD SUCCESSFUL" ) != -1 )
    assertEquals( "Standard error wasn't empty.", 0, len( buildResult.standardError ) )

def assertSuccessfulMaven2Build( buildId ):
    build = waitForBuild( buildId )
    buildResult = continuum.getBuildResult( buildId )
    assertTrue( "The build wasn't successful", buildResult.success )
    assertTrue( "Standard output didn't containe the 'BUILD SUCCESSFUL' message.", buildResult.standardOutput.find( "BUILD SUCCESSFUL" ) != -1 )
    assertEquals( "Standard error wasn't empty.", 0, len( buildResult.standardError ) )

def assertSuccessfulAntBuild( buildId ):
    build = waitForBuild( buildId )
    buildResult = continuum.getBuildResult( buildId )
    assertTrue( "The build wasn't successful", buildResult.success )
    assertTrue( "Standard output didn't containe the 'BUILD SUCCESSFUL' message.", buildResult.standardOutput.find( "BUILD SUCCESSFUL" ) != -1 )
    assertEquals( "Standard error wasn't empty.", 0, len( buildResult.standardError ) )

def waitForBuild( buildId ):
    timeout = 60
    sleepInterval = 0.1

    build = continuum.getBuild( buildId )

    while( build.state == "5" or build.state == "6" ):
        build = continuum.getBuild( buildId )
        sleep( sleepInterval )
        timeout -= sleepInterval

        if ( timeout <= 0 ):
            fail( "Timeout while waiting for build (id=%(id)s) to complete" % { "id" : buildId } )

    return build

def cleanDirectory( dir ):
    if ( os.path.isdir( dir ) ):
        shutil.rmtree( dir )

def cvsImport( basedir, cvsroot, artifactId ):
    os.system( "cvs -d " + cvsroot + " init" )
    cwd = os.getcwd()
    os.chdir( basedir )
    os.system( "cvs -d " + cvsroot + " import -m '' " + artifactId + " continuum_test start" )
    os.chdir( cwd )

def initMaven1Project( basedir, cvsroot, artifactId ):
    cleanDirectory( basedir )
    os.makedirs( basedir )
    pom = file( basedir + "/project.xml", "w+" )
    pom.write( """
<project>
  <pomVersion>3</pomVersion>
  <groupId>continuum</groupId>
  <artifactId>%(artifactId)s</artifactId>
  <currentVersion>1.0</currentVersion>
  <name>Maven 1 Project</name>
  <repository>
    <connection>scm:cvs:local:%(cvsroot)s:%(artifactId)s</connection>
  </repository>
  <build>
    <nagEmailAddress>maven-1@foo.com</nagEmailAddress>
  </build>
</project>
""" % { "artifactId" : artifactId, "cvsroot" : cvsroot } )
    pom.close()

    os.makedirs( basedir + "/src/main/java" )
    foo = file( basedir + "/src/main/java/Foo.java", "w+" )
    foo.write( "class Foo { }" )
    foo.close()

    cvsImport( basedir, cvsroot, artifactId )

def initMaven2Project( basedir, cvsroot, artifactId ):
    cleanDirectory( basedir )
    os.makedirs( basedir )
    pom = file( basedir + "/pom.xml", "w+" )
    pom.write( """
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>continuum</groupId>
  <artifactId>%(artifactId)s</artifactId>
  <version>2.0-SNAPSHOT</version>
  <name>Maven 2 Project</name>
  <ciManagement>
    <notifiers>
      <notifier>
        <type>mail</type>
        <address>dev@continuum.codehaus.org</address>
      </notifier>
    </notifiers>
  </ciManagement>
  <scm>
    <connection>scm:cvs:local:%(cvsroot)s:%(artifactId)s</connection>
  </scm>
</project>
""" % { "artifactId" : artifactId, "cvsroot" : cvsroot } )
    pom.close()

    os.makedirs( basedir + "/src/main/java" )
    foo = file( basedir + "/src/main/java/Foo.java", "w+" )
    foo.write( "class Foo { }" )
    foo.close()

    cvsImport( basedir, cvsroot, artifactId )

def initAntProject( basedir, cvsroot, artifactId ):
    cleanDirectory( basedir )
    os.makedirs( basedir )
    buildXml = file( basedir + "/build.xml", "w+" )
    buildXml.write( """
<project>
  <target name="build">
    <property name="classes" value="target/classes"/>
    <mkdir dir="${classes}"/>
    <javac srcdir="src/main/java" destdir="${classes}"/>
  </target>
  <target name="clean">
    <delete dir="${classes}"/>
  </target>
</project>""" )
    buildXml.close()

    os.makedirs( basedir + "/src/main/java" )
    foo = file( basedir + "/src/main/java/Foo.java", "w+" )
    foo.write( "class Foo { }" )
    foo.close()

    cvsImport( basedir, cvsroot, artifactId )

basedir = os.getcwd() + "/test-ci"
cvsroot = basedir + "/cvsroot"
maven1Project = basedir + "/maven-1"
maven2Project = basedir + "/maven-2"
antProject = basedir + "/ant"

cleanDirectory( basedir )
os.mkdir( basedir )
os.mkdir( cvsroot )

print "Initializing Maven 1 CVS project"
initMaven1Project( maven1Project, cvsroot, "maven-1" )

print "Initializing Maven 2 CVS project"

initMaven2Project( maven2Project, cvsroot, "maven-2" )
print "Initializing Ant CVS project"
initAntProject( antProject, cvsroot, "ant" )

print "Adding Maven 1 project"
maven1Id = continuum.addProjectFromUrl( "file:" + maven1Project + "/project.xml", "maven-1" )

print "Adding Maven 2 project"
maven2Id = continuum.addProjectFromUrl( "file:" + maven2Project + "/pom.xml", "maven2" )

print "Adding Ant project"
antId = continuum.addProjectFromScm( "scm:cvs:local:" + basedir + "/cvsroot:ant", "ant", "Ant Project", "foo@bar", "3.0", { "executable": "ant", "targets" : "clean, build"} )

print "Asserting projects"

maven1 = continuum.getProject( maven1Id )
maven2 = continuum.getProject( maven2Id )
ant = continuum.getProject( antId )

############################################################
# Assert the projects
############################################################

assertProject( "1", "Maven 1 Project", "maven-1@foo.com", "1", "1.0", "maven-1", maven1 )
assertProject( "2", "Maven 2 Project", "dev@continuum.codehaus.org", "1", "2.0-SNAPSHOT", "maven2", maven2 )
assertProject( "3", "Ant Project", "foo@bar", "1", "3.0", "ant", ant )

############################################################
# Project building
############################################################

print "Building Maven 1 project"
buildId = continuum.buildProject( maven1.id )
assertSuccessfulMaven1Build( buildId )

print "Building Maven 2 project"
build = continuum.buildProject( maven2.id )
assertSuccessfulMaven2Build( build )

print "Building Ant project"
build = continuum.buildProject( ant.id )
assertSuccessfulAntBuild( build )

print ""
print "##############################################"
print "ALL TESTS PASSED"
print "##############################################"
