
package com.github.lltyk.wro4j.services;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.HttpServletRequestHandler;
import org.apache.tapestry5.services.assets.ContentTypeAnalyzer;
import org.apache.tapestry5.services.assets.ResourceMinimizer;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.config.Context;
import ro.isdc.wro.config.jmx.WroConfiguration;
import ro.isdc.wro.extensions.model.factory.GroovyModelFactory;
import ro.isdc.wro.extensions.model.factory.JsonModelFactory;
import ro.isdc.wro.manager.WroManager;
import ro.isdc.wro.manager.factory.BaseWroManagerFactory;
import ro.isdc.wro.model.factory.XmlModelFactory;


public class WRO4JModule
{
  private static final Logger log = LoggerFactory.getLogger(WRO4JModule.class);

  public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration)
  {
    configuration.add(WRO4JSymbolConstants.AUTO_ENABLE_WRO_FILTER, "true");
    configuration.add(WRO4JSymbolConstants.ENABLE_LESS_TRANSFORMER, "true");
    configuration.add(WRO4JSymbolConstants.ENABLE_COFFEESCRIPT_TRANSFORMER, "true");
    configuration.add(WRO4JSymbolConstants.ENABLE_SASS_TRANSFORMER, "true");
  }

  public static void bind(ServiceBinder binder)
  {
    binder.bind(HttpServletRequestFilter.class, WroFilterWrapper.class).withId("WroFilterWrapper");
  }

  public static void contributeStreamableResourceSource(MappedConfiguration<String, ResourceTransformer> configuration,
    @Symbol(WRO4JSymbolConstants.ENABLE_LESS_TRANSFORMER) Boolean enableLess,
    @Symbol(WRO4JSymbolConstants.ENABLE_COFFEESCRIPT_TRANSFORMER) Boolean enableCoffee,
    @Symbol(WRO4JSymbolConstants.ENABLE_SASS_TRANSFORMER) Boolean enableSass)
  {
    if (enableLess != null && enableLess) {
      configuration.addInstance("less", LessCssTransformer.class);
    }
    if (enableCoffee != null && enableCoffee) {
      configuration.addInstance("coffee", CoffeeScriptJsTransformer.class);
    }
    if (enableSass != null && enableSass) {
      configuration.addInstance("sass", SassCssTransformer.class);
    }
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
    configuration.addInstance("text/javascript", YuiJSMinimizer.class);
    configuration.addInstance("text/css", YuiCssMinimizer.class);
  }

  public static WroConfiguration buildDefaultConfiguration()
  {
    return new WroConfiguration();
  }

  public static WroManager buildDefaultWroManager()
  {
    try {
      Context.set(Context.standaloneContext());
      return new BaseWroManagerFactory().create();
    } finally {
      Context.unset();
    }
  }

  public static void contributeIgnoredPathsFilter(Configuration<String> configuration)
  {
    configuration.add("/wro/");
  }

  @Contribute(HttpServletRequestHandler.class)
  public static void httpServletRequestHandler(OrderedConfiguration<HttpServletRequestFilter> configuration,
    ApplicationGlobals globals,
    @Symbol(WRO4JSymbolConstants.AUTO_ENABLE_WRO_FILTER) Boolean enableFilter,
    @InjectService("WroFilterWrapper") HttpServletRequestFilter wroFilter)
  {
    boolean disable = enableFilter == null || !enableFilter;
    boolean hasWroFile = hasWroFile(globals);
    if (!disable && hasWroFile) {
      log.info("enabling wro filter");
      configuration.add("WroFilter", wroFilter, "before:IgnoredPaths", "before:GZIP");
    }
  }

  private static boolean hasWroFile(ApplicationGlobals globals)
  {
    // check for existence of a wro configuration by attempting to create a model
    try {
      Context.set(Context.standaloneContext());
      // getting around Context api...
      Field f = Context.class.getDeclaredField("servletContext");
      f.setAccessible(true);
      f.set(Context.get(), globals.getServletContext());
      try {
        return new XmlModelFactory() {
          public boolean hasWroFile() throws IOException {
            getModelResourceAsStream();
            return true;
          }
        }.hasWroFile();
      } catch (Exception e) {
        log.trace("no wro file", e);
      }
      try {
        return new JsonModelFactory() {
          public boolean hasWroFile() throws IOException {
            getModelResourceAsStream();
            return true;
          }
        }.hasWroFile();
      } catch (Exception e) {
        log.trace("no wro file", e);
      }
      try {
        return new GroovyModelFactory() {
          public boolean hasWroFile() throws IOException {
            getModelResourceAsStream();
            return true;
          }
        }.hasWroFile();
      } catch (Exception e) {
        log.trace("no wro file", e);
      }
      log.info("No wro file found, automatic wro filter add disabled");
      return false;
    } catch (Exception e) {
      log.error("unexpected error on hasWroFile", e);
      return false;
    } finally {
      Context.unset();
    }
  }
}
