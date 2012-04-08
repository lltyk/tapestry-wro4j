package com.github.lltyk.wro4j.services;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;
import org.slf4j.Logger;


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

  @Override
  public InputStream transform(Resource source, ResourceDependencies dependencies) throws IOException {
    if (hasEnableTag(source)) {
      try {
        String transformed = doTransform(request.getRealPath(source.getPath()));
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

  public String doTransform(final String file) throws IOException {
    Context context = Context.enter();
    Global global = new Global();
    global.init(context);
    File out = File.createTempFile("rjsout", ".js");
    String baseUrl = FilenameUtils.getFullPath(file);
    if (baseUrl.contains("/js/")) {
      baseUrl = request.getRealPath("js/"); // hack, need to make baseUrl a param
    }
    Object[] args = new Object[] { "-o", "name=" + FilenameUtils.getBaseName(file),
        "baseUrl=" + baseUrl, "out=" + out.getPath(),
        "optimize=none"
    };
    Scriptable argsObj = context.newArray(global, args);
    global.defineProperty("arguments", argsObj, ScriptableObject.DONTENUM);
    context.evaluateString(global, IOUtils.toString(getClass().getResourceAsStream("r.js")), "r.js", 1, null);
    StringWriter writer = new StringWriter();
    IOUtils.write(FileUtils.readFileToString(out, "UTF-8"), writer);
    out.delete();
    Context.exit();
    return writer.toString();
  }
}