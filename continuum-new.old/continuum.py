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
        self.server = xmlrpclib.Server("http://localhost:9081")

    def do_quit(self, args):
        """Exit the command interpreter.
        Use this command to quit the demo shell."""

        return 1

    def do_version(self, args):
        """Display the version of the shell.
        Prints the version of this software on the command line."""

        print "Version 1.0"
        return None

    def do_viewTpi(self, args):
        """Display trading partner information.
        Use this command to display trading partner information."""

        print self.server.tambora.viewTpi( args[0] )

        return None

    def do_executeActivity(self, args):
        """ Send an RFQ.
        Use this command to send a message."""

        activityId = args[0]
        entity = open( args[1], "r" ).read()
        
        self.server.tambora.executeActivity( activityId, entity )
        
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

