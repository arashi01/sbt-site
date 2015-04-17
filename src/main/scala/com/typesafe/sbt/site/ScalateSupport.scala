package com.typesafe.sbt.site

import java.io.File

import sbt._
import Keys._

import collection.mutable.ArrayBuffer


object ScalateSupport {

  val ScalateSite = config("scalate-site")

  val scalateSite = taskKey[Seq[(File, String)]]("Generates a static website from Scalate templates.")
  val scalateBootClass = settingKey[Option[String]]("Optional Scalate template engine boot class name.")

  val settings: Seq[Setting[_]] = (configSettings ++ dependencySettings :+ (ivyConfigurations += ScalateSite)) ++
    inConfig(ScalateSite)(Seq(
      sourceDirectory in scalateSite := sourceDirectory.value / "app",
      target := crossTarget.value / ScalateSite.name,
      target in scalateSite := target.value / "generated-site",
      includeFilter in scalateSite := AllPassFilter,
      excludeFilter in scalateSite := HiddenFileFilter,
      scalateBootClass := None,
      scalateSite <<= scalateSiteGeneratorTask,
      mappings in scalateSite := scalateSite.value
    ))

  private def configSettings: Seq[Setting[_]] = inConfig(ScalateSite)(Defaults.configSettings)

  private def dependencySettings: Seq[Setting[_]] = Seq(
    libraryDependencies ++= {
      def tool = (libraryDependencies in Compile).value find (l ⇒ l.name == "scalate-core") map
        (_.copy(name = "scalate-tool", configurations = Some(ScalateSite.name))) getOrElse
        "org.scalatra.scalate" %% "scalate-tool" % "1.7.2-SNAPSHOT" % ScalateSite
      Seq(
        tool,
        tool.copy(name = "scalate-page"),
        "org.slf4j" % "slf4j-nop" % "1.7.12" % ScalateSite)
    },
    resolvers += "FuseSource Maven" at "http://repo.fusesource.com/nexus/content/groups/public/" // FIXME: For now Scalate uses a custom Karaf version.
  )

  def scalateSiteGeneratorTask: Def.Initialize[Task[Seq[(File, String)]]] = Def.task {
    val target = (Keys.target in scalateSite).value
    val streams = Keys.streams.value
    target.mkdirs()
    streams.cacheDirectory.mkdirs()
    val cp = fullClasspath.value map (_.data) mkString File.pathSeparator
    def opts = ForkOptions(javaHome.value, runJVMOptions = Seq(
      "-cp", cp))
    def args = {
      val buffer = ArrayBuffer(
        "org.fusesource.scalate.tool.ScalateMain",
        "generate-site",
        s"--working-directory=${streams.cacheDirectory.getAbsolutePath}")
      scalateBootClass.value foreach (b ⇒ buffer += s"--boot-class=$b")
      buffer ++ Array((sourceDirectory in scalateSite).value.getAbsolutePath, target.getAbsolutePath)
    }
    Fork.java(opts, args) // FIXME: Build successful even if generation failed.
    target ** ((includeFilter in scalateSite).value -- (excludeFilter in scalateSite).value) --- target x relativeTo(target)
  }

}
