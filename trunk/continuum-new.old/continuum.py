import cli
import os
import socket
import sys
from time import strftime, gmtime
import xmlrpclib

#def __init__( self ):
#    server = xmlrpclib.Server("http://localhost:8000")
#    try:
#        self.server.continuum.getAllProjects()
#    except socket.error, msg:
#        print "Error while connecting to the XML-RPC server"
#        print msg
#        sys.exit( -1 )
server = xmlrpclib.Server("http://localhost:8000")
try:
    server.continuum.getAllProjects()
except socket.error, msg:
    print "Error while connecting to the XML-RPC server"
    print msg
    sys.exit( -1 )

def checkResult( map ):
    if ( map[ "result" ] == "ok" ):
        return map

    print "Error while executing method."
    print "Method: " + map[ "method" ]
    print "Message: " + map[ "message" ]
    print "Stack trace: " + map[ "stackTrace" ]

    raise Exception( "Error while executing method" )

def addProjectFromUrl( url, builderId ):
    result = checkResult( server.continuum.addProjectFromUrl( url, builderId ) )

    return result[ "projectId" ]

def addProjectFromScm( scmUrl, builderId, name, nagEmailAddress, version, configuration ):
    result = checkResult( server.continuum.addProjectFromScm( scmUrl, builderId, name, nagEmailAddress, version, configuration ) )

    return result[ "projectId" ]

def getProject( projectId ):
    result = checkResult( server.continuum.getProject( projectId ) )

    return Project( result[ "project" ] )

def buildProject( projectId ):
    result = checkResult( server.continuum.buildProject( projectId ) )

    return result[ "buildId" ]

def getAllProjects():
    result = checkResult( server.continuum.getAllProjects() )

    return result[ "projects" ]

def getBuild( buildId ):
    result = checkResult( server.continuum.getBuild( buildId ) )

    return Build( result[ "build" ] )

def getBuildResult( buildId ):
    result = checkResult( server.continuum.getBuildResult( buildId ) )

    buildResult = result[ "buildResult" ]

    if ( len( buildResult ) == 0 ):
        return None

    return BuildResult( buildResult )

class Project:
    def __init__( self, map ):
        self.id = map[ "id" ]
        self.name = map[ "name" ]
        self.nagEmailAddress = map[ "nagEmailAddress" ]
        self.state = map[ "state" ]
        self.version = map[ "version" ]
        self.builderId = map[ "builderId" ]

    def __str__( self ):
        return "id: " + self.id + os.linesep +\
               "name: " + self.name + os.linesep +\
               "nagEmailAddress: " + self.nagEmailAddress + os.linesep +\
               "state: " + self.state + os.linesep +\
               "version: " + self.version + os.linesep +\
               "builder id: " + self.builderId + os.linesep

class Build:
    def __init__( self, map ):
        map[ "totalTime" ] = int( map[ "endTime" ] )/ 1000 - int( map[ "startTime" ] ) / 1000
        map[ "startTime" ] = strftime("%a, %d %b %Y %H:%M:%S +0000", gmtime( int( map[ "startTime" ] ) / 1000 ) )
        map[ "endTime" ] = strftime("%a, %d %b %Y %H:%M:%S +0000", gmtime( int( map[ "endTime" ] ) / 1000 ) )
        map[ "totalTime" ] = map[ "totalTime" ]

        self.id = map[ "id" ]
        self.state = map[ "state" ]
        self.startTime = map[ "startTime" ]
        self.endTime = map[ "endTime" ]
        self.totalTime = map[ "totalTime" ]
        self.error = map.get( "error" )
        self.map = map

        if ( self.error == None ):
            self.error = ""
            map[ "error" ] = ""

    def __str__( self ):
        return  \
"""id: %(id)s
State: %(state)s
Start time: %(startTime)s
End time: %(endTime)s
Build time: %(totalTime)ss
error: %(error)s""" % self.map

class BuildResult:
    def __init__( self, map ):
        self.success = map[ "success" ]
        self.exitCode = map[ "exitCode" ]
        self.standardOutput = map[ "standardOutput" ]
        self.standardError = map[ "standardError" ]

    def __str__( self ):
        return "success: " + self.success + os.linesep +\
               "exitCode: " + self.exitCode + os.linesep +\
               "standardOutput: " + self.standardOutput + os.linesep +\
               "standardError: " + self.standardError + os.linesep
