package com.github.lltyk.wro4j.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.slf4j.Logger;

import ro.isdc.wro.config.Context;
import ro.isdc.wro.config.jmx.WroConfiguration;


/**
 * Takes care of reading the content and setting the WRO context.
 */

public abstract class AbstractTransformer extends Base implements ResourceTransformer
{
  @Inject
  private Logger log;
  @Inject
  private WroConfiguration config;
  @Inject
  private HttpServletRequest request;
  @Inject
  private HttpServletResponse response;
  private final String externalSuffix;
  private final String internalSuffix;


  public AbstractTransformer(final String externalSuffix, final String internalSuffix) {
    this.externalSuffix = externalSuffix;
    this.internalSuffix = internalSuffix;
  }

  public InputStream transform(Resource source, ResourceDependencies dependencies) throws IOException {
    if (externalSuffix != null && source.getPath().toLowerCase().endsWith("." + externalSuffix)) {
      //Replace input of a .css or .js file with a .less or .coffee version
      Resource newSource = source.withExtension(internalSuffix);
      if (newSource.exists()) {
        source = newSource;
      }
    }
    if (source.getPath().toLowerCase().endsWith("." + internalSuffix)) {
      Reader reader = new InputStreamReader(source.openStream(), "UTF-8");
      String content = IOUtils.toString(reader);
      reader.close();
      try {
        Context.set(Context.webContext(request, response, null), config);
        String transformed = doTransform(source.toURL().toString(), content);
        return new ByteArrayInputStream(transformed.getBytes("UTF-8"));
      } finally {
        Context.unset();
      }
    } else {
      return source.openStream();
    }
  }

  public abstract String doTransform(String url, String content) throws IOException;
}
