
package com.github.lltyk.wro4j.services;

import ro.isdc.wro.extensions.processor.js.BeautifyJsProcessor;
import ro.isdc.wro.extensions.processor.support.uglify.UglifyJs;



/**
 * Pass a Prototype compatibility option to UglifyJSProcessor
 */
public class PrototypeUglifyJsProcessor extends BeautifyJsProcessor {
  @Override
  protected UglifyJs newEngine() {
    UglifyJs uglify = UglifyJs.uglifyJs();
    uglify.setReservedNames("$super");
    return uglify;
  }
}
