package com.github.lltyk.wro4j.services;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.HttpServletRequestHandler;
import org.slf4j.Logger;

import ro.isdc.wro.http.WroFilter;

import com.github.lltyk.wro4j.services.internal.MockFilterConfig;

/**
 * Adapt WroFilter to the Tapestry HttpServletRequestFilter chain
 */
public class WroFilterWrapper implements HttpServletRequestFilter {
  @Inject
  private Logger log;
  @Inject
  @Symbol(SymbolConstants.PRODUCTION_MODE)
  boolean productionMode;
  @Inject
  private  ApplicationGlobals applicationGlobals;
  private final WroFilter filter;
  private boolean needsInit = true;


  public WroFilterWrapper() {
    filter = new WroFilter();

  }

  private void init() throws ServletException
  {
    if (needsInit) {
      filter.init(new MockFilterConfig(applicationGlobals));
      needsInit = false;
    }
  }

  public boolean service(HttpServletRequest request, HttpServletResponse response, HttpServletRequestHandler handler) throws IOException {
    try {
      init();
      filter.doFilter(request, response, new FilterChain() {
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
          // TODO Auto-generated method stub
        }
      });
    } catch (ServletException e) {
      log.error("wro", e);
    }
    return handler.service(request, response);
  }
}
