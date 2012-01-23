package com.github.ltyk.wro4j.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.slf4j.Logger;

import com.github.lltyk.wro4j.services.WRO4JModule;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
@SubModule(WRO4JModule.class)
public class BaseTestModule
{

  public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration)
  {
    configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
    configuration.add(SymbolConstants.MINIFICATION_ENABLED, "true");
  }

  public Logger buildLogger(final Logger log)
  {
    return log;
  }
}
