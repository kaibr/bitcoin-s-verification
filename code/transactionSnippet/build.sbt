scalaVersion := "2.11.8"

resolvers += "uuverifiers" at "http://logicrunch.it.uu.se:4096/~wv/maven"

lazy val root = (project in file("."))
  .enablePlugins(StainlessPlugin)
  .settings(
    libraryDependencies ++= List(
      "org.scodec" %% "scodec-bits" % "1.1.6"
      , "org.bouncycastle" % "bcprov-jdk15on" % "1.55"
    )
  )
