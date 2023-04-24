scalaVersion := "2.13.10"
mainClass := Some("Main")
libraryDependencies ++= Seq("org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",
    "org.xerial" % "sqlite-jdbc" % "3.36.0.3",
    "com.typesafe.slick" %% "slick" % "3.5.0-M2",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.5.0-M2",
    "org.slf4j" % "slf4j-nop" % "2.0.7",
    "ch.qos.logback" % "logback-classic" % "1.2.6", 
    "org.slf4j" % "slf4j-nop" % "1.7.32",
    "org.xerial" % "sqlite-jdbc" % "3.36.0.3",
    "com.typesafe.akka" %% "akka-http" % "10.5.1",
    "com.typesafe.akka" %% "akka-actor-typed" % "2.8.1-M1",
    "com.typesafe.akka" %% "akka-stream" % "2.8.1-M1",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.1",
    "de.heikoseeberger" %% "akka-http-circe" % "1.40.0-RC3",
    "io.circe" %% "circe-generic" % "0.15.0-M1"
)
