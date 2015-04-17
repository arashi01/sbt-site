import com.typesafe.sbt.site.ScalateSupport

val verify = taskKey[Unit]("")

name := "scalate site test"

site.settings

site.scalateSupport()

verify := {
  def f = (target in (ScalateSupport.ScalateSite)).value / "generated-site" / "index.html"
  assert(f.exists, s"Generated file `$f` does not exist!")
}
