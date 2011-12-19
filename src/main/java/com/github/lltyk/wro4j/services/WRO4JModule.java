
package com.github.lltyk.wro4j.services;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.services.assets.ResourceTransformer;


public class WRO4JModule
{
  public static void contributeStreamableResourceSource(MappedConfiguration<String, ResourceTransformer> configuration)
  {
    configuration.addInstance("coffee", CoffeeScriptJsTransformer.class);
    configuration.addInstance("less", LessCssTransformer.class);
    configuration.addInstance("sass", SassCssTransformer.class);
  }
}
