package com.github.lltyk.wro4j.services;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.tapestry5.ioc.annotations.Inject;
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
  @Inject
  private Logger log;

  public GoogleClosureJSMinimizer()
  {
    super("GoogleClosureCompiler");
  }

  @Override
  protected String doMinimize(String desc, String content) throws IOException
  {
    StringReader reader = new StringReader(content);
    StringWriter output = new StringWriter();
    getInjectedProcessor(GoogleClosureCompressorProcessor.class, CompilationLevel.SIMPLE_OPTIMIZATIONS)
      .process(Resource.create(desc, ResourceType.JS), reader, output);
    return output.toString();
  }
}