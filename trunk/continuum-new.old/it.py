import continuum
import os
import shutil
import sys
from os import linesep
from time import sleep

def fail( message ):
    print "FAILURE: " + message
    sys.exit( -1 )

def assertEquals( message, expected, actual ):
    if ( expected == None and actual != None ):
        print "Expected None but the actual value was: '" + str( actual ) + "'."
        sys.exit( -1 )

    if ( expected != None and actual == None ):
        assert 0, "Expected '" + str( expected ) + "' but the actual value None."
        sys.exit( -1 )

    if( expected == actual ):
        return
    
    print message + linesep +\
          "Expected " + str( expected ) + linesep +\
          "Actual " + str( actual )

    sys.exit( -1 )

def assertTrue( message, condition ):
    assertEquals( message, str( condition ), "True" )

def assertNotNull( message, condition ):
    if( condition != None ):
        return

    print message

    sys.exit( -1 )

def assertProject( id, name, nagEmailAddress, state, version, builderId, project ):
    assertNotNull( "project.id", id )
    assertEquals( "project.name", name, project.name )
    assertEquals( "project.nagEmailAddress", nagEmailAddress, project.nagEmailAddress )
    assertEquals( "project.state", state, project.state )
    assertEquals( "project.version", version, project.version )
    assertEquals( "project.builderId", builderId, project.builderId )

def assertSuccessfulMaven1Build( buildId ):
    build = waitForBuild( buildId )
    assertEquals( "The build wasn't successful.", 2, build.state )
    buildResult = continuum.getBuildResult( buildId )
    assertNotNull( "Build result was null.", buildResult )
    assertEquals( "The build wasn't successful", buildResult.success, "true" )
    assertTrue( "Standard output didn't contain the 'BUILD SUCCESSFUL' message.", buildResult.standardOutput.find( "BUILD SUCCESSFUL" ) != -1 )
    assertEquals( "Standard error wasn't empty.", 0, len( buildResult.standardError ) )

def assertSuccessfulMaven2Build( buildId ):
    build = waitForBuild( buildId )
    assertEquals( "The build wasn't successful.", 2, build.state )
    buildResult = continuum.getBuildResult( buildId )
    assertNotNull( "Build result was null.", buildResult )
    assertEquals( "The build wasn't successful", buildResult.success, "true" )
    assertTrue( "Standard output didn't contain the 'BUILD SUCCESSFUL' message.", buildResult.standardOutput.find( "BUILD SUCCESSFUL" ) != -1 )
    assertEquals( "Standard error wasn't empty.", 0, len( buildResult.standardError ) )

def assertSuccessfulAntBuild( buildId ):
    build = waitForBuild( buildId )
    assertEquals( "The build wasn't successful.", 2, build.state )
    buildResult = continuum.getBuildResult( buildId )
    assertNotNull( "Build result was null.", buildResult )
    assertEquals( "The build wasn't successful", buildResult.success, "true" )
    assertTrue( "Standard output didn't contain the 'BUILD SUCCESSFUL' message.", buildResult.standardOutput.find( "BUILD SUCCESSFUL" ) != -1 )
    assertEquals( "Standard error wasn't empty.", 0, len( buildResult.standardError ) )

def assertSuccessfulShellBuild( buildId, expectedStandardOutput ):
    build = waitForBuild( buildId )
    assertEquals( "The build wasn't successful.", 2, build.state )
    buildResult = continuum.getBuildResult( buildId )
    assertNotNull( "Build result was null.", buildResult )
    assertEquals( "The build wasn't successful", buildResult.success, "true" )
    assertEquals( "Standard output didn't contain the expected output.", expectedStandardOutput, buildResult.standardOutput )
    assertEquals( "Standard error wasn't empty.", 0, len( buildResult.standardError ) )

def waitForBuild( buildId ):
    timeout = 60
    sleepInterval = 0.1

    build = continuum.getBuild( buildId )

    while( build.state == 5 or build.state == 6 ):
        build = continuum.getBuild( buildId )
        sleep( sleepInterval )
        timeout -= sleepInterval

        if ( timeout <= 0 ):
            fail( "Timeout while waiting for build (id=%(id)s) to complete" % { "id" : buildId } )

    return build

def cleanDirectory( dir ):
    if ( os.path.isdir( dir ) ):
        shutil.rmtree( dir )

def cvsCommit( basedir ):
    cwd = os.getcwd()
    os.chdir( basedir )
    os.system( "cvs commit -m ''" )
    os.chdir( cwd )

def cvsImport( basedir, cvsroot, artifactId ):
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

def initShellProject( basedir, cvsroot, artifactId ):
    cleanDirectory( basedir )
    os.makedirs( basedir )
    script = file( basedir + "/script.sh", "w+" )
    script.write( """#!/bin/sh
for arg in "$@"
do
  echo $arg
  done
""" )
    script.close()
    os.system( "chmod +x " + basedir + "/script.sh" )

    cvsImport( basedir, cvsroot, artifactId )

basedir = os.getcwd() + "/target/test-ci"
cvsroot = basedir + "/cvsroot"
maven1Project = basedir + "/maven-1"
maven2Project = basedir + "/maven-2"
antProject = basedir + "/ant"
shellProject = basedir + "/shell"

cleanDirectory( basedir )
os.mkdir( basedir )
os.mkdir( cvsroot )
os.system( "cvs -d " + cvsroot + " init" )

print "Initializing Maven 1 CVS project"
initMaven1Project( maven1Project, cvsroot, "maven-1" )

print "Initializing Maven 2 CVS project"

initMaven2Project( maven2Project, cvsroot, "maven-2" )

print "Initializing Ant CVS project"
initAntProject( antProject, cvsroot, "ant" )

print "Initializing Shell CVS project"
initShellProject( shellProject, cvsroot, "shell" )

print "Adding Maven 1 project"
maven1Id = continuum.addProjectFromUrl( "file:" + maven1Project + "/project.xml", "maven-1" )

print "Adding Maven 2 project"
maven2Id = continuum.addProjectFromUrl( "file:" + maven2Project + "/pom.xml", "maven2" )

print "Adding Ant project"
antId = continuum.addProjectFromScm( "scm:cvs:local:" + basedir + "/cvsroot:ant", "ant", "Ant Project", "foo@bar", "3.0", { "executable": "ant", "targets" : "clean, build"} )

print "Adding Shell project"
prefix = os.getcwd() + "/continuum-plexus-application/target/plexus-test-runtime/apps/continuum-plexus-application-1.0-alpha-1-SNAPSHOT-application/temp/Shell-Project/"
shellId = continuum.addProjectFromScm( "scm:cvs:local:" + basedir + "/cvsroot:shell", "shell", "Shell Project", "foo@bar", "3.0", 
        { "script": prefix + "script.sh", "arguments" : ""} )

print "Asserting projects"

maven1 = continuum.getProject( maven1Id )
maven2 = continuum.getProject( maven2Id )
ant = continuum.getProject( antId )
shell = continuum.getProject( shellId )

############################################################
# Assert the projects
############################################################

assertProject( "1", "Maven 1 Project", "maven-1@foo.com", 1, "1.0", "maven-1", maven1 )
assertProject( "2", "Maven 2 Project", "dev@continuum.codehaus.org", 1, "2.0-SNAPSHOT", "maven2", maven2 )
assertProject( "3", "Ant Project", "foo@bar", 1, "3.0", "ant", ant )
assertProject( "4", "Shell Project", "foo@bar", 1, "3.0", "shell", shell )

############################################################
# Project building
############################################################

if 1:
    print "Building Maven 1 project"
    buildId = continuum.buildProject( maven1.id )
    assertSuccessfulMaven1Build( buildId )

    print "Testing that the POM is updated before each build."
    # Test that the POM is updated before each build
    pom = file( maven1Project + "/project.xml", "r" )
    str = pom.read()
    pom.close()

    str = str.replace( "Maven 1 Project", "Maven 1 Project - Changed" )
    str = str.replace( "1.0", "1.1" )

    pom = file( maven1Project + "/project.xml", "w+" )
    pom.write( str )
    pom.close()

    cvsImport( maven1Project, cvsroot, "maven-1" )

    continuum.updateProjectFromScm( maven1.id )
    maven1 = continuum.getProject( maven1.id )
    assertEquals( "The project name wasn't changed.", "Maven 1 Project - Changed", maven1.name )
    assertEquals( "The project version wasn't changed.", "1.1", maven1.version )

if 1:
    print "Building Maven 2 project"
    build = continuum.buildProject( maven2.id )
    assertSuccessfulMaven2Build( build )

if 1:
    print "Building Ant project"
    build = continuum.buildProject( ant.id )
    assertSuccessfulAntBuild( build )

if 1:
    print "Building Shell project"
    build = continuum.buildProject( shell.id )
    assertSuccessfulShellBuild( build, "" )

    # Test project reconfiguration
    configuration = shell.configuration
    configuration[ "arguments" ] = "a b";
    continuum.updateProjectConfiguration( shell.id, configuration );
    shell = continuum.getProject( shell.id )
    build = continuum.buildProject( shell.id )
    assertSuccessfulShellBuild( build, """a
b
""" )

# TODO: Add project failure tests

print ""
print "##############################################"
print "ALL TESTS PASSED"
print "##############################################"
