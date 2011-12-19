package com.github.lltyk.wro4j.services;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.slf4j.Logger;

import com.yahoo.platform.yui.compressor.CssCompressor;

/**
 * CSS resource minimizer using YUI compressor
 *
 */
public class YuiCssMinimizer extends AbstractMinimizer
{
  private final Logger log;


  public YuiCssMinimizer(final Logger log, OperationTracker tracker)
  {
    super(log, tracker, "YuiCSS");
    this.log = log;
  }

  protected void doMinimize(StreamableResource resource, Writer output) throws IOException
  {
    Reader reader = toReader(resource);
    String content = IOUtils.toString(reader);
    reader.close();
    reader = toReader(resource);
    try {
      final CssCompressor compressor = new CssCompressor(reader);
      CharArrayWriter caw = new CharArrayWriter();
      compressor.compress(caw, -1);
      output.write(caw.toCharArray());
      return;
    } catch (RuntimeException e) {
      final String resourceUri = resource == null ? StringUtils.EMPTY : "[" + resource.getDescription() + "]";
      log.warn("Exception while applying " + getClass().getSimpleName() + " processor on the " + resourceUri
        + " resource, no processing applied...", e);
    } finally {
      reader.close();
    }
    //the fallback to unminimised
    output.write(content);
  }
}