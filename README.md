# Tapestry WRO4J

[![Build Status](http://travis-ci.org/lltyk/tapestry-wro4j.png)](http://travis-ci.org/lltyk/tapestry-wro4j)

Adds support for various [Web Resource Optimizer](http://code.google.com/p/wro4j/) components to Tapestry 5.3

Included are various choices of JavaScript and CSS minifiers, and
Less/Sass/Coffeescript transformers.

## Usage

The transformers automatically associate with their file extensions, simply
create one (a .coffee file for example), and include it on a page just as you
would a js file:

    @Import(library="somefile.coffee",stylesheet="someotherfile.less")

The transformers can also be associated with .js and .css files. if you link
foo.css and a foo.less file exists, then the foo.less file is transformed and
sent as foo.css to the browser. Unfortunately an actual empty foo.css is needed
to satisfy Tapestry's asset-existance checking. You can enable this association
in the AppModule like this:

    public static void contributeStreamableResourceSource(MappedConfiguration<String, ResourceTransformer> configuration)
    {
        configuration.addInstace("css", LessCssTransformer.class);
    }

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

## 0.9.5
Tapestry 5.3.3  

## 0.9.4
WRO4J 1.4.5  
Experimental RequireJS support  

## 0.9.3
WRO4J 1.4.4  

## 0.9.2
Tapestry 5.3.2  

## 0.9.1
Automatically included WRO4JModule in jar manifest.  
Added Google Closure for JS and YUI for CSS as default minimizers.  
Fixed UglifyJS/Prototype interoperability.  
Fixed Less import support (issue #2)  
WRO4J 1.4.3  

## 0.9
Initial Release  
WRO4JModule needs to be included manually with @SubModule.  
Minifiers need to be added manually using contributeMinimizers.  
WRO4J 1.4.2  
