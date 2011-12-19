package com.github.lltyk.wro4j.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.slf4j.Logger;

import ro.isdc.wro.extensions.processor.support.ObjectPoolHelper;
import ro.isdc.wro.extensions.processor.support.coffeescript.CoffeeScript;
import ro.isdc.wro.util.ObjectFactory;


/**
 * Transforms <code>.coffee</code> to JavaScript. 
 */

public class CoffeeScriptJsTransformer implements ResourceTransformer
{
  private final Logger log;
  private final ObjectPoolHelper<CoffeeScript> enginePool;

  public CoffeeScriptJsTransformer(final Logger log) {
    this.log = log;
    enginePool = new ObjectPoolHelper<CoffeeScript>(new ObjectFactory<CoffeeScript>() {
      @Override
      public CoffeeScript create() {
        return new CoffeeScript();
      }
    });
  }

  @Override
  public InputStream transform(Resource source, ResourceDependencies dependencies) throws IOException {
    Reader reader = new InputStreamReader(source.openStream(), "UTF-8");
    String content = IOUtils.toString(reader);
    reader.close();
    CoffeeScript coffeeScript = enginePool.getObject();
    try {
      return new ByteArrayInputStream(coffeeScript.compile(content).getBytes("UTF-8"));
    } finally {
      enginePool.returnObject(coffeeScript);
    }
  }
}
