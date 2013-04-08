package com.github.lltyk.wro4j.services;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import ro.isdc.wro.config.Context;
import ro.isdc.wro.config.jmx.WroConfiguration;
import ro.isdc.wro.extensions.processor.js.CoffeeScriptProcessor;
import ro.isdc.wro.model.group.processor.Injector;
import ro.isdc.wro.model.group.processor.InjectorBuilder;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;


/**
 * Transforms <code>.coffee</code> to JavaScript. 
 */

public class CoffeeScriptJsTransformer extends AbstractTransformer
{

  @Inject
  private Logger log;
  
  private final CoffeeScriptProcessor processor;

  private final WroConfiguration configuration;
  

  public CoffeeScriptJsTransformer(WroConfiguration configuration) {
    super("js", "coffee");
    this.configuration = configuration;
    try {
      Context.set(Context.standaloneContext(), configuration);
      Injector injector = new InjectorBuilder().build();
      CoffeeScriptProcessor processor = new CoffeeScriptProcessor();
      injector.inject(processor);
      this.processor = processor;
    } finally {
      Context.unset();
    }
  }

  @Override
  public String doTransform(String name, String content) throws IOException {
    StringWriter writer = new StringWriter();
    try {
      Context.set(Context.standaloneContext(), configuration);
      processor.process(Resource.create(name, ResourceType.JS), new StringReader(content), writer);
    } finally {
        Context.unset();
    }
    return writer.toString();
  }
  
  public String getTransformedContentType() {
    return TapestryWRO4JConstants.CONTENT_TYPE_JAVASCRIPT;
  }
}
