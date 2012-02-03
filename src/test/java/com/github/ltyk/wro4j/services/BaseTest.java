// Copyright 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.github.ltyk.wro4j.services;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.TapestryModule;
import org.apache.tapestry5.test.TapestryTestCase;
import org.junit.After;
import org.junit.Before;


public class BaseTest extends TapestryTestCase {

  protected static Registry registry;


  @Before
  public void createRegistry()
  {
    registry = buildRegistry(TapestryModule.class, getModuleClass());
    registry.getService(RequestGlobals.class).storeServletRequestResponse(mockHttpServletRequest(), mockHttpServletResponse());
  }

  @After
  public void shutdownRegistry()
  {
    if (registry != null) {
      registry.shutdown();
    }
  }

  /** Override to run test with module setting changes */
  protected Class<?> getModuleClass()
  {
    return BaseTestModule.class;
  }

  protected static String getClasspathFile(String file) throws IOException
  {
    return IOUtils.toString(BaseTest.class.getClassLoader().getResourceAsStream(file), "UTF-8");
  }

  protected Resource getResource(String rootClasspathFile) {
    return new ClasspathResource(rootClasspathFile);
  }
}
