package com.github.lltyk.wro4j.services;


public class WRO4JSymbolConstants {
  /**
   * Whether to add the WRO4J filter to the filter chain.
   * Conditionally defaulted to true if a wro configuration
   * file is found.
   */
  public static final String ENABLE_WRO_FILTER = "wro.enable.filter";
  /** Defaults to true */
  public static final String ENABLE_LESS_TRANSFORMER = "wro.enable.less";
  /** Defaults to true */
  public static final String ENABLE_COFFEESCRIPT_TRANSFORMER = "wro.enable.coffeescript";
  /** Defaults to true */
  public static final String ENABLE_SASS_TRANSFORMER = "wro.enable.sass";
}
