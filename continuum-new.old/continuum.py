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

def decodeState( state ):
    if ( state == 1 ):
        return "new"
    elif ( state == 2 ):
        return "ok"
    elif ( state == 3 ):
        return "failed"
    elif ( state == 4 ):
        return "error"
    elif ( state == 5 ):
        return "build signaled"
    elif ( state == 6 ):
        return "building"
    else:
        return "UNKNOWN STATE '" + state + "'."

def addProjectFromUrl( url, builderId ):
    result = checkResult( server.continuum.addProjectFromUrl( url, builderId ) )

    return result[ "projectId" ]

def addProjectFromScm( scmUrl, builderId, name, nagEmailAddress, version, configuration ):
    result = checkResult( server.continuum.addProjectFromScm( scmUrl, builderId, name, nagEmailAddress, version, configuration ) )

    return result[ "projectId" ]

def getProject( projectId ):
    result = checkResult( server.continuum.getProject( projectId ) )

    return Project( result[ "project" ] )

def updateProjectFromScm( projectId ):
    checkResult( server.continuum.updateProjectFromScm( projectId ) )

def updateProjectConfiguration( projectId, configuration ):
    checkResult( server.continuum.updateProjectConfiguration( projectId, configuration ) )

def getAllProjects():
    result = checkResult( server.continuum.getAllProjects() )

    return result[ "projects" ]

def buildProject( projectId ):
    result = checkResult( server.continuum.buildProject( projectId ) )

    return result[ "buildId" ]

def getBuildsForProject( projectId, start, end ):
    result = checkResult( server.continuum.getBuildsForProject( projectId, start, end ) )

    builds = []
    for build in result[ "builds" ]:
        builds.append( Build( build ) )

    return builds;

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
        self.map = map
        self.id = map[ "id" ]
        self.name = map[ "name" ]
        self.nagEmailAddress = map[ "nagEmailAddress" ]
        self.state = int( map[ "state" ] )
        self.version = map[ "version" ]
        self.builderId = map[ "builderId" ]
        self.configuration = map[ "configuration" ]

    def __str__( self ):
        str = "id: " + self.id + os.linesep +\
              "name: " + self.name + os.linesep +\
              "nagEmailAddress: " + self.nagEmailAddress + os.linesep +\
              "state: " + decodeState( self.state ) + os.linesep +\
              "version: " + self.version + os.linesep +\
              "builder id: " + self.builderId + os.linesep

        if ( len( self.configuration.keys() ) > 0 ):
            conf = ""
            for key in self.configuration.keys():
                conf += os.linesep + key + "=" + self.configuration[ key ]
            str += conf

        return str

class Build:
    def __init__( self, map ):
        map[ "totalTime" ] = int( map[ "endTime" ] )/ 1000 - int( map[ "startTime" ] ) / 1000
        map[ "startTime" ] = strftime("%a, %d %b %Y %H:%M:%S +0000", gmtime( int( map[ "startTime" ] ) / 1000 ) )
        map[ "endTime" ] = strftime("%a, %d %b %Y %H:%M:%S +0000", gmtime( int( map[ "endTime" ] ) / 1000 ) )
        map[ "totalTime" ] = map[ "totalTime" ]

        self.id = map[ "id" ]
        self.state = int( map[ "state" ] )
        self.startTime = map[ "startTime" ]
        self.endTime = map[ "endTime" ]
        self.totalTime = map[ "totalTime" ]
        self.error = map.get( "error" )
        self.map = map

        if ( self.error == None ):
            self.error = ""
            map[ "error" ] = ""

    def __str__( self ):
        map = self.map

        map[ "state" ] = decodeState( self.state )

        return  \
"""Id: %(id)s
State: %(state)s
Start time: %(startTime)s
End time: %(endTime)s
Build time: %(totalTime)ss
error: %(error)s""" % map

class BuildResult:
    def __init__( self, map ):
        self.success = map[ "success" ]
        self.exitCode = int( map[ "exitCode" ] )
        self.standardOutput = map[ "standardOutput" ]
        self.standardError = map[ "standardError" ]

    def __str__( self ):
        value = "Success: " + self.success + os.linesep +\
              "Exit code: " + str( self.exitCode ) + os.linesep

        if ( len( self.standardOutput ) > 0 ):
              value += "Standard output: " + self.standardOutput + os.linesep

        if ( len( self.standardError ) > 0 ):
               value += "Standard error: " + self.standardError + os.linesep

        return value
