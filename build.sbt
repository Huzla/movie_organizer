name := "Movie Organizer"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" % "scalatestplus-plus" % "3.0.0" % "test",
  "org.mockito" % "mockito-all" % "1.8.4"
)