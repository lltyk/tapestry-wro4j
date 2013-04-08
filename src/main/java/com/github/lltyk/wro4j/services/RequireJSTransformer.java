package com.github.lltyk.wro4j.services;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.slf4j.Logger;

import ro.isdc.wro.model.resource.ResourceType;


/**
 * Runs the RequireJS optimizer to combine files. Does not minify.
 */
public class RequireJSTransformer extends Base implements ResourceTransformer
{
  @Inject
  private Logger log;
  @Inject
  private HttpServletRequest request;


  public RequireJSTransformer() {

  }

  public InputStream transform(Resource source, ResourceDependencies dependencies) throws IOException {
    if (hasEnableTag(source)) {
      try {
        String transformed = doTransform(source.openStream(), request.getRealPath(source.getPath()));
        return new ByteArrayInputStream(transformed.getBytes("UTF-8"));
      } catch (Exception e) {
        log.error("Could not transform", e);
      }
    }
    // Stream file as is for unhandled files and exception throws
    return source.openStream();
  }

  private boolean hasEnableTag(Resource source) {
    try {
      return IOUtils.readLines(source.openStream()).get(0).contains("RequireJSTransformer=on");
    } catch (Exception e) {
      log.error("", e);
    }
    return false;
  }

  public String doTransform(InputStream sourceInput, String file) throws IOException {
    StringWriter writer = new StringWriter();
    String baseUrl = FilenameUtils.getFullPath(file);
    if (baseUrl.contains("/js/")) {
      int end = baseUrl.indexOf("/js/") + 4;
      baseUrl = baseUrl.substring(0, end); // hack, need to make baseUrl a param
      log.trace(baseUrl);
    }
    ro.isdc.wro.model.resource.Resource resource = ro.isdc.wro.model.resource.Resource.create(file, ResourceType.JS);
    new RJSProcessor(baseUrl, getPathsMap(), getAdditionalOptions()).process(
      resource, new InputStreamReader(sourceInput), writer);
    return writer.toString();
  }

  protected Map<String, String> getPathsMap() {
    return Collections.emptyMap();
  }
  
  protected String[] getAdditionalOptions() {
    return null;
  }
  
  public String getTransformedContentType() {
    return TapestryWRO4JConstants.CONTENT_TYPE_JAVASCRIPT;
  }
}