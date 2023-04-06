scalaVersion := "2.13.8"
mainClass := Some("Main")
libraryDependencies ++= Seq("org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",
    "org.xerial" % "sqlite-jdbc" % "3.36.0.3",
    "com.typesafe.slick" %% "slick" % "3.5.0-M2",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.5.0-M2",
    "org.slf4j" % "slf4j-nop" % "2.0.7",
    "ch.qos.logback" % "logback-classic" % "1.2.6", 
    "org.slf4j" % "slf4j-nop" % "1.7.32",
    "org.xerial" % "sqlite-jdbc" % "3.36.0.3"
)
