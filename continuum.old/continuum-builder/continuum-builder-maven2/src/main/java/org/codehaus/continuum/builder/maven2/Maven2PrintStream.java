package org.codehaus.continuum.builder.maven2;

/*
 * LICENSE
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: Maven2PrintStream.java,v 1.1 2004-09-07 16:22:16 trygvis Exp $
 */
public class Maven2PrintStream
    extends PrintStream
{
    private ByteArrayOutputStream byteArray;

    private PrintStream buffer;

    private Thread builderThread;

    public Maven2PrintStream( PrintStream originalStream )
    {
        super( originalStream );

        byteArray = new ByteArrayOutputStream( 10000 );

        buffer = new PrintStream( byteArray );
    }

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    /**
     * Sets the thread thats will have it's <code>System.out</code> and 
     * <code>System.err</code> content writen to the buffer.
     * 
     * If the <code>builderThread</code> is <code>null</code> nothing will be logged.
     * 
     * @param builderThread
     */
    public void setBuilderThread( Thread builderThread )
    {
        this.builderThread = builderThread;
    }

    public String getBufferAndResetStream()
    {
        buffer.flush();

        buffer.close();

        String string = byteArray.toString();

        byteArray = new ByteArrayOutputStream( 10000 );

        buffer = new PrintStream( byteArray );

        return string;
    }

    private boolean isBuilderThread()
    {
        if ( builderThread == null )
        {
            return false;
        }

        Thread currentThread = Thread.currentThread();

        return currentThread.equals( builderThread );
    }

    // ----------------------------------------------------------------------
    // PrintStream Implementation
    // ----------------------------------------------------------------------

    public boolean checkError()
    {
        return super.checkError();
    }
/*
    public void print( boolean b )
    {
        if ( isBuilderThread() )
        {
            buffer.print( b );
        }

        super.print( b );
    }

    public void print( char c )
    {
        if ( isBuilderThread() )
        {
            buffer.print( c );
        }

        super.print( c );
    }

    public void print( char[] s )
    {
        if ( isBuilderThread() )
        {
            buffer.print( s );
        }

        super.print( s );
    }

    public void print( double d )
    {
        if ( isBuilderThread() )
        {
            buffer.print( d );
        }

        super.print( d );
    }

    public void print( float f )
    {
        if ( isBuilderThread() )
        {
            buffer.print( f );
        }

        super.print( f );
    }

    public void print( int i )
    {
        if ( isBuilderThread() )
        {
            buffer.print( i );
        }

        super.print( i );
    }

    public void print( long l )
    {
        if ( isBuilderThread() )
        {
            buffer.print( l );
        }

        super.print( l );
    }

    public void print( Object obj )
    {
        if ( isBuilderThread() )
        {
            buffer.print( obj );
        }

        super.print( obj );
    }

    public void print( String s )
    {
        if ( isBuilderThread() )
        {
            buffer.print( s );
        }

        super.print( s );
    }

    public void println()
    {
        if ( isBuilderThread() )
        {
            buffer.println();
        }

        super.println();
    }

    public void println( boolean x )
    {
        if ( isBuilderThread() )
        {
            buffer.print( x );
        }

        super.println( x );
    }

    public void println( char x )
    {
        if ( isBuilderThread() )
        {
            buffer.print( x );
        }

        super.println( x );
    }

    public void println( char[] x )
    {
        if ( isBuilderThread() )
        {
            buffer.print( x );
        }

        super.println( x );
    }

    public void println( double x )
    {
        if ( isBuilderThread() )
        {
            buffer.print( x );
        }

        super.println( x );
    }

    public void println( float x )
    {
        if ( isBuilderThread() )
        {
            buffer.print( x );
        }

        super.println( x );
    }

    public void println( int x )
    {
        if ( isBuilderThread() )
        {
            buffer.print( x );
        }

        super.println( x );
    }

    public void println( long x )
    {
        if ( isBuilderThread() )
        {
            buffer.print( x );
        }

        super.println( x );
    }

    public void println( Object x )
    {
        if ( isBuilderThread() )
        {
            buffer.print( x );
        }

        super.println( x );
    }

    public void println( String x )
    {
        if ( isBuilderThread() )
        {
            buffer.print( x );
        }

        super.println( x );
    }

    protected void setError()
    {
        super.setError();
    }
*/
    public void write( byte[] buf, int off, int len )
    {
        if ( isBuilderThread() )
        {
            buffer.write( buf, off, len );
        }

        super.write( buf, off, len );
    }

    public void write( byte[] b ) throws IOException
    {
        if ( isBuilderThread() )
        {
            buffer.print( b );
        }

        super.write( b );
    }
}
