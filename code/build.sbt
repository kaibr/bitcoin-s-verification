scalaVersion := "2.11.8"

resolvers += "uuverifiers" at "http://logicrunch.it.uu.se:4096/~wv/maven"

lazy val root = (project in file("."))
  .enablePlugins(StainlessPlugin)
