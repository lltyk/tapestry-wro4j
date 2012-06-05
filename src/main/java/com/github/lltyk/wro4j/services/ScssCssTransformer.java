package com.github.lltyk.wro4j.services;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import ro.isdc.wro.extensions.processor.css.SassCssProcessor;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;


/**
 * Transforms <code>.scss</code> to CSS.
 */

public class ScssCssTransformer extends AbstractTransformer
{
  @Inject
  private Logger log;

  public ScssCssTransformer() {
      super("css", "scss");
  }

  @Override
  public String doTransform(final String name, final String content) throws IOException {
    StringWriter writer = new StringWriter();
    new SassCssProcessor().process(Resource.create(name, ResourceType.CSS), new StringReader(content), writer);
    return writer.toString();
  }
}
