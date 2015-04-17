# sbt-site #

This is an sbt plugin that can generate project websites.

It is designed to work hand-in-hand with publishing plugins like [sbt-ghpages].


## Usage ##

Add this to your `project/plugins.sbt`:

    addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1")

*Note: this requires sbt 0.13 - for sbt 0.12 see the [0.7.2 version][old] of this plugin.*

And then in your `build.sbt` file:

    site.settings

Then, run `make-site` to generate your project's webpage in the `target/site` directory.

See the [sbt-ghpages] plugin for information about publishing to gh-pages. We expect other
publishing mechanisms to be supported in the future.


## Adding content to your site ##

The sbt-site plugin has specific "support" settings for the various supported ways
of generating a site. By default, all files under `src/site` are included in the site.
You can add new generated content in one of the following ways:


### Direct ###

Simply add to the site mappings. In your `build.sbt` file:

    site.siteMappings <++= Seq(file1 -> "location.html", file2 -> "image.png")


## Scaladoc APIS ###

Add the following line to your `build.sbt`:

    site.includeScaladoc()

This will default to putting the scaladoc under the `latest/api` directory on the
website. You can configure this by passing a parameter to the includeScaladoc method:

    site.includeScaladoc("alternative/directory")


### Scalate Site Generation
The site plugin has direct support for generating [Scalate](scalate.github.io/scalate) projects.
This assumes a Scalate project located under the `src/scalate-site` directory, with template files
located in `src/scalate-site/app`, and supporting Scala, Java and resource files located in `src/scalate-site/scala`,
`src/scalate-site/java` and `src/scalate-site/resources` respectively.

To add Scalate support, add the following to your project build configuration:
```scala
site.includeScalate() // Note: 'includeScalate' also accepts an optional string parameter specifying Scalate a boot class name.
```

The following settings can be configured to customise Scalate site generation (assumes the import of `com.typesafe.sbt.site.ScalateSupport`):
```scala
libraryDependencies in ScalateSupport.ScalateSite  // Allows adding additional libraries to site generation classpath.
sourceDirectory in ScalateSupport.ScalateSite // Allows changing the base source directory.
scalateBootClass in ScalateSupport.ScalateSite // Allows specifying a Scalate template engine boot class name.
```


### Jekyll site generation ###

The site plugin has direct support for running jekyll locally.  This is suprisingly
useful for suporting custom jekyll plugins that are not allowed when publishing to
github, or hosting a jekyll site on your own server. To add jekyll support, add the
following to your `build.sbt`:

    site.jekyllSupport()

This assumes you have a jekyll project in the `src/jekyll` directory.

*TODO - Link to documentation site on usage and configuration.*

One common issue with Jekyll is ensuring that everyone uses the same version for
generating a website.  There is special support for ensuring the version of gems.
To do so, add the following to your `build.sbt` file:

    com.typesafe.sbt.site.JekyllSupport.requiredGems := Map(
      "jekyll" -> "0.11.2",
      "liquid" -> "2.3.0"
    )


### Sphinx site generation ###

The site plugin has direct support for building Sphinx projects locally. This assumes
you have a sphinx project under the `src/sphinx` directory. To enable sphinx site
generation, simply add the following to your `build.sbt` file:

    site.sphinxSupport()

### Pamflet generation ###

The site plugin has direct support for building [Pamflet](pamflet.databinder.net) projects.   This assumes you have a pamflet project under the `src/pamflet` directory.   To enable pamflet site generation, simply add the following to your `build.sbt` file:


    site.pamfletSupport()

To place pamflet HTML under a directory, run:
    
    site.pamfletSupport("manual")

The above will place pamflet generated HTML under the `manual/` directory in the generated site.

### nanoc generation ###

The site plugin has direct support for building [nanoc](http://nanoc.ws/) projects.
This assumes you have a nanoc project under the `src/nanoc` directory. To enable nanoc site generation,
simply simply add the following to your `build.sbt` file:

    site.nanocSupport()

### Asciidoctor site generation ###

The site plugin has direct support for building Asciidoctor projects locally. This assumes
you have a asciidoctor project under the `src/asciidoctor` directory. To enable asciidoctor site
generation, simply add the following to your `build.sbt` file:

    site.asciidoctorSupport()

### Changing the source directory ###

To change the source directory for static site files, use the `siteSourceDirectory` alias:

    siteSourceDirectory <<= target / "generated-stuff"


### Previewing the site ###

To preview your generated site, run `previewSite` to open a web server. Direct your
web browser to `http://localhost:4000/` to view your site.

*TODO - Link to documentation site on usage and configuration.*


## Packaging and Publishing ##

To create a zip package of the site run `package-site`.

To also publish this zip file when running `publish`, add the following to your `build.sbt`:

    site.publishSite


[old]: https://github.com/sbt/sbt-site/tree/0.7.2
[sbt-ghpages]: http://github.com/sbt/sbt-ghpages
[Pamflet]: http://pamflet.databinder.net
