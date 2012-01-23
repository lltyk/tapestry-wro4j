package com.github.lltyk.wro4j.services;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import ro.isdc.wro.extensions.processor.js.CoffeeScriptProcessor;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;


/**
 * Transforms <code>.coffee</code> to JavaScript. 
 */

public class CoffeeScriptJsTransformer extends AbstractTransformer
{
  @Inject
  private Logger log;

  public CoffeeScriptJsTransformer() {
    super("js", "coffee");
  }

  @Override
  public String doTransform(String name, String content) throws IOException {
    StringWriter writer = new StringWriter();
    new CoffeeScriptProcessor().process(Resource.create(name, ResourceType.JS), new StringReader(content), writer);
    return writer.toString();
  }
}
