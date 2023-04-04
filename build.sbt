name := "slick-codegen-example"

scalacOptions += "-deprecation"

val slickVersion = "3.4.1"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-codegen" % slickVersion,
  "org.slf4j" % "slf4j-nop" % "2.0.7",
  "org.xerial" % "sqlite-jdbc" % "3.41.2.1"

)

