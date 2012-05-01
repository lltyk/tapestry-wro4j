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

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.github.lltyk.wro4j.services.SassCssTransformer;


/**
 * Test class for {@link SassCssTransformer}
 * 
 * @author Alex Objelean
 */
public class TestSassCssTransformer extends BaseTest {
  private SassCssTransformer transformer;

  @Test(expected=NullPointerException.class)
  public void cannotTransformNullResource() throws Exception {
    transformer = registry.autobuild(SassCssTransformer.class);
    transformer.doTransform(null, null);
  }

  @Test(expected=NullPointerException.class)
  public void cannotTransformNullResourceContent() throws Exception {
    transformer = registry.autobuild(SassCssTransformer.class);
    transformer.doTransform("resourceName", null);
  }

  @Ignore // this is failing outside of eclipse due to some sort of threadlocal issue
  @Test
  public void shouldTransformResourceContent() throws Exception {
    transformer = registry.autobuild(SassCssTransformer.class);
    Assert.assertEquals("", transformer.doTransform("resourceName", ""));
  }
}
