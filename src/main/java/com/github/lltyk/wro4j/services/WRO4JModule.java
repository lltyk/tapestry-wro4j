
package com.github.lltyk.wro4j.services;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.services.assets.ContentTypeAnalyzer;
import org.apache.tapestry5.services.assets.ResourceMinimizer;
import org.apache.tapestry5.services.assets.ResourceTransformer;


public class WRO4JModule
{
  public static void contributeStreamableResourceSource(MappedConfiguration<String, ResourceTransformer> configuration)
  {
    configuration.addInstance("coffee", CoffeeScriptJsTransformer.class);
    configuration.addInstance("less", LessCssTransformer.class);
    configuration.addInstance("sass", SassCssTransformer.class);
  }

  @Contribute(ContentTypeAnalyzer.class)
  public static void setupContentTypeMappings(MappedConfiguration<String, String> configuration) {
    configuration.add("coffee", "text/javascript");
    configuration.add("less", "text/css");
    configuration.add("sass", "text/css");
  }

  @Contribute(ResourceMinimizer.class)
  @Primary
  public static void contributeMinimizers(MappedConfiguration<String, ResourceMinimizer> configuration)
  {
    configuration.addInstance("text/javascript", GoogleClosureJSMinimizer.class);
    configuration.addInstance("text/css", YuiCssMinimizer.class);
  }
}
