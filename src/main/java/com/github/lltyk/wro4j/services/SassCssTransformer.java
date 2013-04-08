package com.github.lltyk.wro4j.services;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import ro.isdc.wro.extensions.processor.css.RubySassCssProcessor;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;


/**
 * Transforms <code>.sass</code> to CSS.
 */

public class SassCssTransformer extends AbstractTransformer
{
  @Inject
  private Logger log;

  public SassCssTransformer() {
    super("css", "sass");
  }

  @Override
  public String doTransform(final String name, final String content) throws IOException {
    StringWriter writer = new StringWriter();
    new RubySassCssProcessor().process(Resource.create(name, ResourceType.CSS), new StringReader(content), writer);
    return writer.toString();
  }
  
  public String getTransformedContentType() {
    return TapestryWRO4JConstants.CONTENT_TYPE_CSS;
  }
}
