#!/usr/bin/python

import cli
import sys
import xmlrpclib

##########################################################
# Build your commands in this class.  Each method that
# starts with "do_" is exposed as a shell command, and
# its doc string is used when the user types 'help' or
# 'man'.  A command that does not return None, will
# cause the shell to terminate.
#
# The first line of the docstring is used when help is
# typed by itself to give a summary, and then if the
# user requests specific help on a command, the full
# text is supplied.
#
# If your system has the GNU readline stuff on it, then
# pressing tab will do tab completion of the commands.
# You will also get much nicer command line editing
# just like using your shell, as well as command
# history.
##########################################################

class TamboraXmlRpcClient(cli.cli):
    def __init__(self):
        cli.cli.__init__(self)
        self.server = xmlrpclib.Server("http://localhost:8000")

    def do_quit(self, args):
        """Exit the command interpreter.
        Use this command to quit the demo shell."""

        return 1

    def do_version(self, args):
        """Display the version of the shell.
        Prints the version of this software on the command line."""

        print "Version 1.0"
        return None

    def do_addProject(self, args):
        """Add a Continuum project.
        Use this command to add a project to Continuum."""

        print self.server.continuum.addProjectFromUrl( args[0], args[1] )

        return None

    def do_buildProject(self, args):
        """Build a Continuum project.
        Use this command to signal a build for a Continuum project."""

        self.server.continuum.buildProject( args[0] )
        
        return
    
    
    def do_run(self, args):
        """Run a script of commands.
        Use this command to run a script of commands."""
        
        commands = open( args[0], "r" ).readlines()
        
        for command in commands:
            cli.cli.onecmd( self, command )
        
        return None
    
##########################################################
# Main loop
##########################################################

try:
    TamboraXmlRpcClient().cmdloop()

except Exception, e:
    print "Error:", e

