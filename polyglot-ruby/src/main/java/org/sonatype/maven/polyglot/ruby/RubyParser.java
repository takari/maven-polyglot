/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.ruby;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.maven.model.Model;
import org.jruby.embed.IsolatedScriptingContainer;
import org.jruby.embed.PathType;

import org.sonatype.maven.polyglot.execute.ExecuteManager;
import org.sonatype.maven.polyglot.ruby.execute.RubyExecuteTaskFactory;

/**
 * Parses the ruby into a Maven model.
 *
 * @author christian
 */
public class RubyParser {

    private final IsolatedScriptingContainer jruby;

    private final Object parser;

    private final ExecuteManager executeManager;

    private final RubyExecuteTaskFactory factory;

    public RubyParser( ExecuteManager executeManager ) throws IOException
    {
        this.executeManager = executeManager;
        this.jruby = new IsolatedScriptingContainer();
        DualClassLoader loader = new DualClassLoader(jruby.getClassLoader())
        jruby.setClassLoader(loader);
        this.parser = jruby.runScriptlet( PathType.CLASSPATH, "parser.rb" );
        this.factory = new RubyExecuteTaskFactory( jruby, loader );
    }

    // synchronize it since it is not clear how threadsafe everything is
    public synchronized Model parse( String ruby, File source, Map<String, ?> options )
    {
        Model model = this.jruby.callMethod( this.parser,
                    "parse",
                    new Object[] { ruby,
                                   this.factory,
                                   source != null ? source.getAbsolutePath() : null },
                    Model.class );
        model.setPomFile( source );
        executeManager.register( model, this.factory.getExecuteTasks() );
        executeManager.install( model, options );
        return model;
    }
}
