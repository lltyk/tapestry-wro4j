package com.github.lltyk.wro4j.services;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.slf4j.Logger;

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

  public GoogleClosureJSMinimizer(final Logger log, OperationTracker tracker)
  {
    super(log, tracker, "YuiCompressor");
    this.log = log;
  }

  protected void doMinimize(StreamableResource resource, Writer output) throws IOException
  {
    Reader reader = toReader(resource);
    String content = IOUtils.toString(toReader(resource));
    reader.close();
    reader = toReader(resource);
    try {
      CharArrayWriter caw = new CharArrayWriter();
      new GoogleClosureCompressorProcessor(CompilationLevel.ADVANCED_OPTIMIZATIONS)
        .process(Resource.create(resource.getDescription(), ResourceType.JS), reader, caw);
      output.write(caw.toCharArray());
      return;
    } catch (Exception e) {
      final String resourceUri = resource == null ? StringUtils.EMPTY : "[" + resource.getDescription() + "]";
      log.warn("Exception while applying " + getClass().getSimpleName() + " processor on the " + resourceUri
        + " resource, no processing applied...", e);
    } finally {
      reader.close();
    }
    //the fallback to unminimised
    output.write(content);
  }
}