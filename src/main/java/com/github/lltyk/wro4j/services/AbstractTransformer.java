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

import ro.isdc.wro.config.Context;
import ro.isdc.wro.config.jmx.WroConfiguration;


/**
 * Takes care of reading the content and setting the WRO context.
 */

public abstract class AbstractTransformer implements ResourceTransformer
{
  private final Logger log;
  private final WroConfiguration config;

  public AbstractTransformer(final Logger log, final WroConfiguration config) {
    this.log = log;
    this.config = config;
  }

  @Override
  public InputStream transform(Resource source, ResourceDependencies dependencies) throws IOException {
    Reader reader = new InputStreamReader(source.openStream(), "UTF-8");
    String content = IOUtils.toString(reader);
    reader.close();
    try {
      Context.set(Context.standaloneContext(), config);
      String transformed = doTransform(source.getPath(), content);
      return new ByteArrayInputStream(transformed.getBytes("UTF-8"));
    } finally {
      Context.unset();
    }
  }
  
  public abstract String doTransform(String resourceName, String content) throws IOException;
}
