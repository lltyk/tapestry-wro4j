package com.github.lltyk.wro4j.services;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.slf4j.Logger;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.ClosureCodingConvention;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.DiagnosticGroups;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;

/**
 * JavaScript resource minimizer based on the Google Closure Compiler
 *
 */
public class GoogleClosureJSMinimizer extends AbstractMinimizer
{
  private final Logger log;

  public GoogleClosureJSMinimizer(final Logger log, OperationTracker tracker)
  {
    super(log, tracker, "YuiCompressor");
    this.log = log;
  }

  protected void doMinimize(StreamableResource resource, Writer output) throws IOException
  {
    Reader reader = toReader(resource);
    String content = IOUtils.toString(toReader(resource));
    reader.close();
    try {
      CharArrayWriter caw = new CharArrayWriter();
      Compiler.setLoggingLevel(Level.SEVERE);
      Compiler compiler = new Compiler();
      CompilerOptions compilerOptions = new CompilerOptions();
      compilerOptions.setCodingConvention(new ClosureCodingConvention());
      compilerOptions.setOutputCharset("UTF-8");
      compilerOptions.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.WARNING);
      CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(compilerOptions);
      compiler.initOptions(compilerOptions);
      JSSourceFile[] input = new JSSourceFile[] {
        JSSourceFile.fromCode(resource.getDescription(), content)
      };
      final Result result = compiler.compile(new JSSourceFile[] {}, input, compilerOptions);
      if (result.success) {
        caw.write(compiler.toSource());
      } else {
        caw.write(content);
      }
      output.write(caw.toCharArray());
      return;
    } catch (Exception e) {
      final String resourceUri = resource == null ? StringUtils.EMPTY : "[" + resource.getDescription() + "]";
      log.warn("Exception while applying " + getClass().getSimpleName() + " processor on the " + resourceUri
        + " resource, no processing applied...", e);
    }
    //the fallback to unminimised
    output.write(content);
  }
}