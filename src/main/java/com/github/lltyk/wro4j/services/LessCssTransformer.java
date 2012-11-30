package com.github.lltyk.wro4j.services;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import ro.isdc.wro.extensions.processor.css.LessCssProcessor;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;
import ro.isdc.wro.model.resource.processor.impl.css.CssImportPreProcessor;


/**
 * Transforms <code>.less</code> to CSS.
 */

public class LessCssTransformer extends AbstractTransformer
{
  @Inject
  private Logger log;

  public LessCssTransformer() {
    super("css", "less");
  }

  @Override
  public String doTransform(String url, String content) throws IOException {
    StringWriter writer = new StringWriter();
    Resource r = Resource.create(url, ResourceType.CSS);
    //CssImportPreProcessor preproc = getInjectedProcessor(CssImportPreProcessor.class);
    //preproc.process(r, new StringReader(content), writer);
    //content = writer.toString();
    writer = new StringWriter();
    LessCssProcessor lessProcessor = getInjectedProcessor(LessCssProcessor.class);
    lessProcessor.process(r, new StringReader(content), writer);
    return writer.toString();
  }
}
