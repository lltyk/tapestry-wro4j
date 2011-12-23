package com.github.lltyk.wro4j.services;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.slf4j.Logger;

import ro.isdc.wro.config.jmx.WroConfiguration;
import ro.isdc.wro.extensions.processor.css.SassCssProcessor;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;


/**
 * Transforms <code>.sass</code> to CSS.
 */

public class SassCssTransformer extends AbstractTransformer
{
  private final Logger log;

  public SassCssTransformer(final Logger log, final WroConfiguration config) {
    super(log, config);
    this.log = log;
  }

  @Override
  public String doTransform(String name, String content) throws IOException {
    StringWriter writer = new StringWriter();
    new SassCssProcessor().process(Resource.create(name, ResourceType.CSS), new StringReader(content), writer);
    return writer.toString();
  }
}
