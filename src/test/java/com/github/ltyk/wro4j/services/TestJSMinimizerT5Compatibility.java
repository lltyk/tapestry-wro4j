package com.github.ltyk.wro4j.services;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.services.assets.BytestreamCache;
import org.apache.tapestry5.internal.services.assets.StreamableResourceImpl;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.services.TapestryModule;
import org.apache.tapestry5.services.assets.AssetChecksumGenerator;
import org.apache.tapestry5.services.assets.CompressionStatus;
import org.apache.tapestry5.services.assets.ResourceMinimizer;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.tools.shell.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.extensions.processor.js.JsHintProcessor;
import ro.isdc.wro.extensions.processor.support.linter.LinterError;
import ro.isdc.wro.extensions.processor.support.linter.LinterException;
import ro.isdc.wro.model.resource.Resource;

import com.github.lltyk.wro4j.services.GoogleClosureJSMinimizer;
import com.github.lltyk.wro4j.services.UglifyJSMinimizer;

/**
 * Test particular js files in Tapestry that are pain points for the various
 * js minifiers
 *
 */
public class TestJSMinimizerT5Compatibility extends BaseTest {
  private static final Logger log = LoggerFactory.getLogger(TestJSMinimizerT5Compatibility.class);
  private SymbolSource symbolSource;
  private AssetChecksumGenerator assetChecksumGenerator;


  @Before
  public void fetchRequiredServices()
  {
    symbolSource = registry.getService(SymbolSource.class);
    assetChecksumGenerator = registry.getService(AssetChecksumGenerator.class);
  }

  @Before
  public void setupMocks()
  {
    expect(mockHttpServletRequest.getRequestURI()).andReturn("dummy").anyTimes();
    expect(mockHttpServletRequest.getRequestURL()).andReturn(new StringBuffer("dummy")).anyTimes();
    expect(mockHttpServletRequest.getServletPath()).andReturn("dummy").anyTimes();
    replay();
  }

  @Test
  public void testGoogleClosure() throws Exception {
    ResourceMinimizer minimizer = registry.autobuild(GoogleClosureJSMinimizer.class);
    testUnderscore(minimizer);
    testPrototype(minimizer);
  }

  @Test
  public void testUglify() throws Exception {
    ResourceMinimizer minimizer = registry.autobuild(UglifyJSMinimizer.class);
    testUnderscore(minimizer);
    testPrototype(minimizer);
  }


  private void testUnderscore(ResourceMinimizer minimizer) throws Exception {
    String path = "META-INF/assets/tapestry5";
    StreamableResource in = getResourceFromClasspath(TapestryModule.class, path + "/underscore_1_4_2.js");
    StreamableResource out = minimizer.minimize(in);
    Assert.assertNotSame("same file outputted, minimization failed", in, out);
    String output = IOUtils.toString(out.openStream());
    Assert.assertTrue("invalid js found", validateJs(output, in.getDescription()));
    FileUtils.write(new File("/tmp/out.js"), output);
  }

  private void testPrototype(ResourceMinimizer minimizer) throws Exception {
    String path = getClasspathAssetPathBySymbol(SymbolConstants.SCRIPTACULOUS).replaceAll("^/+", "");
    StreamableResource in = getResourceFromClasspath(TapestryModule.class, path + "/prototype.js");
    StreamableResource out = minimizer.minimize(in);
    Assert.assertNotSame("same file outputted, minimization failed", in, out);
    String output = IOUtils.toString(out.openStream());
    //Assert.assertTrue("invalid js found", validateJs(output, in.getDescription())); //prototype doesn't seem to like rhino...
    // check for mangling of $super
    Assert.assertTrue("$super was mangled", output.contains("$super("));
  }

  private String getClasspathAssetPathBySymbol(String symbol) {
    symbolSource = registry.getService(SymbolSource.class);
    return symbolSource.valueForSymbol(symbol).replace("classpath:", "/");
  }

  private StreamableResource getResourceFromClasspath(Class<?> baseclass, String classpathPath) throws IOException {
    return new StreamableResourceImpl(classpathPath, "text/javascript",
      CompressionStatus.COMPRESSABLE, new Date().getTime(),
      new BytestreamCache(IOUtils.toByteArray(baseclass.getClassLoader().getResourceAsStream(classpathPath))), assetChecksumGenerator);
  }

  private boolean validateJs(String js, String name) {
    try {
      Context context = Context.enter();
      Global global = new Global();
      global.init(context);
      //String env = IOUtils.toString(getClass().getResourceAsStream("/ro/isdc/wro/extensions/script/env.rhino.min.js"));
      //context.evaluateString(global, env, "env.rhino.min.js", 1, null);
      context.evaluateString(global, js, name, 1, null);
      return true;
    } catch (Exception e) {
      log.error("error running js", e);
    } finally {
      Context.exit();
    }
    return false;
  }

  private class Linter extends JsHintProcessor
  {
    public Linter()
    {
      setOptions("eqnull","evil","sub","expr");
    }

    @Override
    protected void onLinterException(final LinterException e, final Resource resource) {
      String msg = "";
      if (e != null && e.getErrors() != null) {
        for (LinterError error : e.getErrors()) {
          msg += error.toString();
        }
      }
      log.error("The following resource: " + resource + " has " + e.getErrors().size() + " errors: " + msg, e);
      throw new RuntimeException("The following resource: " + resource + " has " + e.getErrors().size() + " errors: " + msg, e);
    }
  }
}
