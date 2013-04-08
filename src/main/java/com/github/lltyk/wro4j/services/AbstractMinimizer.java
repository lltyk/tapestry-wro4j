package com.github.lltyk.wro4j.services;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.internal.services.assets.BytestreamCache;
import org.apache.tapestry5.internal.services.assets.StreamableResourceImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.assets.AssetChecksumGenerator;
import org.apache.tapestry5.services.assets.CompressionStatus;
import org.apache.tapestry5.services.assets.ResourceMinimizer;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.slf4j.Logger;

import ro.isdc.wro.config.Context;
import ro.isdc.wro.config.jmx.WroConfiguration;

/**
 * Base class for resource minimizers.
 *
 * @since 5.3
 */
public abstract class AbstractMinimizer extends Base implements ResourceMinimizer
{
  private static final double NANOS_TO_MILLIS = 1.0d / 1000000.0d;
  @Inject
  private Logger log;
  @Inject
  private WroConfiguration config;
  @Inject
  private HttpServletRequest request;
  @Inject
  private HttpServletResponse response;
  @Inject
  private AssetChecksumGenerator assetChecksumGenerator;

  private final String resourceType;

  public AbstractMinimizer(String resourceType)
  {
    this.resourceType = resourceType;
  }

  public StreamableResource minimize(final StreamableResource input) throws IOException
  {
    long startNanos = -1l;
    if (log.isDebugEnabled()) {
    	startNanos = System.nanoTime();
    }
    Reader reader = new InputStreamReader(input.openStream(), "UTF-8");
    final String content = IOUtils.toString(reader);
    reader.close();
    try {
      Context.set(Context.webContext(request, response, null), config);
      String minimised = doMinimize(input.getDescription(), content);
      // The content is minimized, but can still be (GZip) compressed.
      StreamableResource output = new StreamableResourceImpl("minimized " + input.getDescription(),
        input.getContentType(), CompressionStatus.COMPRESSABLE,
        input.getLastModified(), new BytestreamCache(minimised.getBytes("UTF-8")),assetChecksumGenerator);
      if (log.isDebugEnabled()) {
        long elapsedNanos = System.nanoTime() - startNanos;
        double elapsedMillis = ((double) elapsedNanos) * NANOS_TO_MILLIS;
        log.debug(String.format("Minimized %s (%,d input bytes of %s to %,d output bytes in %.2f ms)",
          input.getDescription(), input.getSize(), resourceType, output.getSize(), elapsedMillis));
      }
      return output;
    } catch (Exception e) {
      final String resourceUri = input == null ? StringUtils.EMPTY : "[" + input.getDescription() + "]";
      log.warn("Exception while applying " + getClass().getSimpleName() + " processor on the " + resourceUri
        + " resource, no processing applied...", e);
    } finally {
      Context.unset();
    }
    //Return unminimised on exception
    return input;
  }

  protected abstract String doMinimize(String description, String content) throws IOException;
}
