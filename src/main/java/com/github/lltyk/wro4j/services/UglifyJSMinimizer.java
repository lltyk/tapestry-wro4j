package com.github.lltyk.wro4j.services;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.slf4j.Logger;

import ro.isdc.wro.extensions.processor.support.ObjectPoolHelper;
import ro.isdc.wro.extensions.processor.support.uglify.UglifyJs;
import ro.isdc.wro.util.ObjectFactory;

/**
 * JavaScript resource minimizer based on UglifyJS
 *
 */
public class UglifyJSMinimizer extends AbstractMinimizer
{
  private final Logger log;
  private final ObjectPoolHelper<UglifyJs> enginePool;


  public UglifyJSMinimizer(final Logger log, OperationTracker tracker)
  {
    super(log, tracker, "UglifyJS");
    this.log = log;
    enginePool = new ObjectPoolHelper<UglifyJs>(new ObjectFactory<UglifyJs>() {
      @Override
      public UglifyJs create() {
        return UglifyJs.uglifyJs();
      }
    });
  }

  protected void doMinimize(StreamableResource resource, Writer output) throws IOException
  {
    Reader reader = toReader(resource);
    String content = IOUtils.toString(reader);
    reader.close();
    UglifyJs engine = enginePool.getObject();
    try {
      output.write(engine.process("resource.js", content));
      return;
    } catch (final RuntimeException e) {
      final String resourceUri = resource == null ? StringUtils.EMPTY : "[" + resource.getDescription() + "]";
      log.warn("Exception while applying " + getClass().getSimpleName() + " processor on the " + resourceUri
        + " resource, no processing applied...", e);
    } finally {
      output.close();
      enginePool.returnObject(engine);
    }
    //the fallback to unminimised
    output.write(content);
  }
}