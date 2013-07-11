name := "scaffold"

organization := "com.twitter"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.2"

resolvers ++= Seq(
  "spray"                  at "http://repo.spray.io",
  "mergeconflict"          at "https://github.com/mergeconflict/maven/raw/master/releases",
  "mergeconflict-snapshot" at "https://github.com/mergeconflict/maven/raw/master/snapshots"
)

libraryDependencies ++= Seq(
  // compile
  "com.atlassian.levee" %  "levee-instrumentor" % "1.0-SNAPSHOT" % "compile",
  "com.typesafe.akka"   %% "akka-actor"         % "2.2.0-RC1"    % "compile",
  "io.spray"            %  "spray-caching"      % "1.2-M8"       % "compile",
  "io.spray"            %  "spray-can"          % "1.2-M8"       % "compile",
  "io.spray"            %  "spray-httpx"        % "1.2-M8"       % "compile",
  "io.spray"            %% "spray-json"         % "1.2.5"        % "compile",
  "io.spray"            %  "spray-routing"      % "1.2-M8"       % "compile",
  "org.pegdown"         %  "pegdown"            % "1.4.0"        % "compile",
  "org.scala-lang"      %  "scala-compiler"     % "2.10.2"       % "compile",
  // runtime
  "com.atlassian.levee" %  "levee-runtime"      % "1.0-SNAPSHOT" % "runtime",
  "com.atlassian.levee" %  "sizeof-agent"       % "1.0-SNAPSHOT" % "runtime",
  "org.webjars"         %  "bootstrap"          % "2.3.2"        % "runtime",
  "org.webjars"         %  "codemirror"         % "3.14"         % "runtime",
  "org.webjars"         %  "html5shiv"          % "3.6.2"        % "runtime",
  "org.webjars"         %  "jquery"             % "2.0.2"        % "runtime",
  // test
  "com.typesafe.akka"   %% "akka-testkit"       % "2.2.0-RC1"    % "test",
  "io.spray"            %  "spray-testkit"      % "1.2-M8"       % "test",
  "org.scalatest"       %% "scalatest"          % "1.9.1"        % "test"
)

javaOptions in run ++= Seq(
  "-javaagent:lib_managed/jars/com.atlassian.levee/sizeof-agent/sizeof-agent-1.0-SNAPSHOT.jar"
)

retrieveManaged := true

fork := true

seq(Revolver.settings: _*)

seq(Twirl.settings: _*)

seq(com.typesafe.sbt.SbtStartScript.startScriptForClassesSettings: _*)

Twirl.twirlImports := Seq("com.twitter.scaffold.Document", "Document._")
