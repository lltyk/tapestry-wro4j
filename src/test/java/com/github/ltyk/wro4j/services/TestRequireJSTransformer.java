package com.github.ltyk.wro4j.services;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.github.lltyk.wro4j.services.RequireJSTransformer;


public class TestRequireJSTransformer extends BaseTest {
  private RequireJSTransformer transformer;


  @Test
  public void shouldTransformSubmodules() throws Exception {
    transformer = registry.autobuild(RequireJSTransformer.class);
    File testfile = new File(getClass().getResource("/testrequire.js").getFile());
    String expected = IOUtils.toString(getClass().getResource("/testrequire-expected.js"));
    Assert.assertEquals(expected, transformer.doTransform(testfile.getPath()));
  }

  @Test
  public void shouldTransformTwice() throws Exception {
    transformer = registry.autobuild(RequireJSTransformer.class);
    File testfile = new File(getClass().getResource("/testrequire.js").getFile());
    String expected = IOUtils.toString(getClass().getResource("/testrequire-expected.js"));
    Assert.assertEquals(expected, transformer.doTransform(testfile.getPath()));
    Assert.assertEquals(expected, transformer.doTransform(testfile.getPath()));
  }
}
