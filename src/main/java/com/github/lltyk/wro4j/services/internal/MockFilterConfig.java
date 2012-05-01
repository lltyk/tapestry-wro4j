package com.github.lltyk.wro4j.services.internal;

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.apache.tapestry5.services.ApplicationGlobals;


/**
 * Fake filter config for WroFilter
 */
public class MockFilterConfig implements FilterConfig
{
  private ApplicationGlobals applicationGlobals;

  public MockFilterConfig(ApplicationGlobals applicationGlobals)
  {
    this.applicationGlobals = applicationGlobals;
  }

  public String getFilterName() {
    return "wro";
  }
  public ServletContext getServletContext() {
    return applicationGlobals.getServletContext();
  }
  public String getInitParameter(String name) {
    return null;
  }
  public Enumeration<?> getInitParameterNames()
  {
    return new Vector<Object>(0).elements();
  }
}