package com.github.lltyk.wro4j.services;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.slf4j.Logger;

import ro.isdc.wro.config.Context;
import ro.isdc.wro.config.jmx.WroConfiguration;
import ro.isdc.wro.extensions.processor.js.GoogleClosureCompressorProcessor;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

import com.google.javascript.jscomp.CompilationLevel;

/**
 * JavaScript resource minimizer based on the Google Closure Compiler
 *
 */
public class GoogleClosureJSMinimizer extends AbstractMinimizer
{
  private final Logger log;
  private final WroConfiguration config;

  public GoogleClosureJSMinimizer(final Logger log, OperationTracker tracker, final WroConfiguration config)
  {
    super(log, tracker, "GoogleClosureCompiler");
    this.log = log;
    this.config = config;
  }

  protected void doMinimize(StreamableResource resource, Writer output) throws IOException
  {
    Reader reader = toReader(resource);
    String content = IOUtils.toString(reader);
    reader.close();
    try {
      CharArrayWriter caw = new CharArrayWriter();
      StringReader contentReader = new StringReader(content);
      Context.set(Context.standaloneContext(), config);
      new GoogleClosureCompressorProcessor(CompilationLevel.SIMPLE_OPTIMIZATIONS)
        .process(Resource.create(resource.getDescription(), ResourceType.JS), contentReader, caw);
      output.write(caw.toCharArray());
      return;
    } catch (Exception e) {
      final String resourceUri = resource == null ? StringUtils.EMPTY : "[" + resource.getDescription() + "]";
      log.warn("Exception while applying " + getClass().getSimpleName() + " processor on the " + resourceUri
        + " resource, no processing applied...", e);
    } finally {
      Context.unset();
    }
    //the fallback to unminimised
    output.write(content);
  }
}