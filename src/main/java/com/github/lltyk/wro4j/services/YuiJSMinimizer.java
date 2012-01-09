package com.github.lltyk.wro4j.services;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import ro.isdc.wro.extensions.processor.js.YUIJsCompressorProcessor;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

/**
 * JavaScript resource minimizer based on YUI Compressor
 *
 */
public class YuiJSMinimizer extends AbstractMinimizer
{
  @Inject
  private Logger log;

  public YuiJSMinimizer()
  {
    super("YuiCompressor");
  }

  @Override
  protected String doMinimize(String desc, String content) throws IOException
  {
    StringReader reader = new StringReader(content);
    StringWriter output = new StringWriter();
    YUIJsCompressorProcessor.doMungeCompressor().process(Resource.create(desc, ResourceType.JS), reader, output);
    return output.toString();
  }
}