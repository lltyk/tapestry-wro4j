package com.github.ltyk.wro4j.services;

import junit.framework.Assert;

import org.junit.Test;

import com.github.lltyk.wro4j.services.CoffeeScriptJsTransformer;


public class TestCoffeeScriptJsTransformer extends BaseTest {
  private CoffeeScriptJsTransformer transformer;


  @Test
  public void shouldTransformResourceContent() throws Exception {
    transformer = registry.autobuild(CoffeeScriptJsTransformer.class);
    Assert.assertEquals("(function() {\n  var x;\n\n  x = 2;\n\n}).call(this);\n", transformer.doTransform("resourceName", "x=2"));
  }
}
