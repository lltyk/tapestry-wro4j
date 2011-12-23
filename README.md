# Tapestry WRO4J

Adds support for various [Web Resource Optimizer](http://code.google.com/p/wro4j/) components to Tapestry 5.3

Included are various choices of JavaScript and CSS minifiers, and
Less/Sass/Coffeescript transformers.

## Usage
At the moment the submodule is not in the jar manifest, so you will have to add
"@SubModule(WRO4JModule.class)" to your AppModule.

The transformers automatically associate with their file extensions, simply
create one (a .coffee file for example), and include it on a page just as you
would a js file:

    @Import(library="somefile.coffee",stylesheet="someotherfile.less")

By default, Google Closure Compiler is used for JS minimization, and YUI is
used for CSS. Other choices for JS include UglifyJSMinimizer and
YuiJSMinimizer. You can use one instead with this in your AppModule:

    @Contribute(ResourceMinimizer.class)
    @Primary
    public static void contributeMinimizers(MappedConfiguration<String, ResourceMinimizer> configuration)
    {
      configuration.overrideInstace("text/javascript", UglifyJSMinimizer.class);
    }

By default Tapestry only enables minification when the tapestry.production-mode
flag is set. You can enable it manually in your AppModule like so:

    public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration)
    {
      configuration.add(SymbolConstants.MINIFICATION_ENABLED, "true");
    }

UglifyJSMinimizer does not work with Prototype at the moment, anything that
uses Class.extend fails (which includes anything that uses Ajax.request).

## 0.9
WRO4JModule needs to be included manually with @SubModule.
Minifiers need to be added manually using contributeMinimizers.