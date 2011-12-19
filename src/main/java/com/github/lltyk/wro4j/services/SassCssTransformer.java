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
import ro.isdc.wro.extensions.processor.support.sass.SassCss;
import ro.isdc.wro.util.ObjectFactory;


/**
 * Transforms <code>.sass</code> to CSS.
 */

public class SassCssTransformer implements ResourceTransformer
{
  private final Logger log;
  private final ObjectPoolHelper<SassCss> enginePool;

  public SassCssTransformer(final Logger log) {
    this.log = log;
    enginePool = new ObjectPoolHelper<SassCss>(new ObjectFactory<SassCss>() {
      @Override
      public SassCss create() {
        return new SassCss();
      }
    });
  }

  @Override
  public InputStream transform(Resource source, ResourceDependencies dependencies) throws IOException {
    Reader reader = new InputStreamReader(source.openStream(), "UTF-8");
    String content = IOUtils.toString(reader);
    reader.close();
    SassCss sassCss = enginePool.getObject();
    try {
      return new ByteArrayInputStream(sassCss.process(content).getBytes("UTF-8"));
    } finally {
      enginePool.returnObject(sassCss);
    }
  }
}
