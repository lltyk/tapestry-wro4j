# Tapestry WRO4J

Adds support for various [Web Resource Optimizer](http://code.google.com/p/wro4j/) components to Tapestry 5.3

Included are various choices of JavaScript and CSS minifiers, and
Less/Sass/Coffeescript transformers.

## Usage
At the moment the submodule is not in the jar manifest, so you will have to add
"@SubModule(WRO4JModule.class)" to your AppModule to enable the transformers.
The transformers automatically associate with their file extensions, simply
create one (a .coffee file for example), and include it on a page just as you
would a js file:

    @Import(library="somefile.coffee",stylesheet="someotherfile.less")

The minifiers are not enabled automatically, since there are a few
alternatives. Choices include GoogleClosureJSMinimizer, UglifyJSMinimizer, and
YuiJSMinimizer. There is also the YuiCssMinimizer for css. They are enabled by
adding something similar to the following to your AppModule:

    @Contribute(ResourceMinimizer.class)
    @Primary
    public static void contributeMinimizers(MappedConfiguration<String, ResourceMinimizer> configuration)
    {
      configuration.addInstance("text/javascript", UglifyJSMinimizer.class);
      configuration.addInstance("text/css", YuiCssMinimizer.class);
    }

By default Tapestry only enables minification when the tapestry.production-mode
flag is set. You can enable it manually in your AppModule like so:

    public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration)
    {
      configuration.add(SymbolConstants.MINIFICATION_ENABLED, "true");
    }