package com.github.lltyk.wro4j.services;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import ro.isdc.wro.extensions.processor.css.YUICssCompressorProcessor;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

/**
 * CSS resource minimizer using YUI compressor
 *
 */
public class YuiCssMinimizer extends AbstractMinimizer
{
  @Inject
  private Logger log;


  public YuiCssMinimizer(final Logger log, OperationTracker tracker)
  {
    super("YuiCSS");
  }

  @Override
  protected String doMinimize(String desc, String content) throws IOException
  {
    StringReader reader = new StringReader(content);
    StringWriter output = new StringWriter();
    getInjectedProcessor(YUICssCompressorProcessor.class).process(Resource.create(desc, ResourceType.CSS), reader, output);
    return output.toString();
  }
}