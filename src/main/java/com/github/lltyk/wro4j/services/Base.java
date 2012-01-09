package com.github.lltyk.wro4j.services;

import java.lang.reflect.Constructor;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import ro.isdc.wro.manager.WroManager;
import ro.isdc.wro.model.group.processor.Injector;
import ro.isdc.wro.model.group.processor.InjectorBuilder;


/**
 * Takes care of reading the content and setting the WRO context.
 */
public abstract class Base
{
  @Inject
  private Logger log;
  @Inject
  private WroManager wroManager;

  protected <T> T getInjectedProcessor(Class<T> processClass, Object ... constructorArgs) {
    try {
      Injector injector = new InjectorBuilder(wroManager).build();
      Class<?>[] argTypes = new Class[constructorArgs.length];
      for (int i = 0; i < constructorArgs.length; i++) {
        if (constructorArgs[i] == null) {
          throw new NullPointerException("You can't pass nulls in due to potential ambiguity");
        }
        argTypes[i] = constructorArgs[i].getClass();
      }
      Constructor<T> construc = processClass.getConstructor(argTypes);
      T processor = construc.newInstance(constructorArgs);
      injector.inject(processor);
      return processor;
    } catch (Exception e) {
      log.error("", e);
    }
    return null;
  }
}
