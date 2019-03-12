# Journal

## Stainless

### Presetup
First, let's install sbt, the **s**cala **b**uild **t**ool, so we can later add the `sbt-stainless` plugin to our build environment.
[Installation instruction](https://www.scala-sbt.org/download.html).

Stainless as well as Scala recommends the usage of JVM 8. [<sup>[1]</sup>](https://docs.scala-lang.org/overviews/jdk-compatibility/overview.html) [<sup>[2]</sup>](https://epfl-lara.github.io/stainless/installation.html)

There are different ways to force sbt to use JVM 8.
The method mentioned [here](https://stackoverflow.com/questions/25926111/how-to-force-sbt-to-use-java-8) by adding `-target:jvm-1.8` to scalac does not work in `Scala < 2.11.4`.
So the simplest way might be adding an alias like this:
```bash
alias sbt='JAVA_HOME=/usr/lib/jvm/java-8-openjdk; PATH=$JAVA_HOME/bin:$PATH; sbt'
```

### Setup
Now we can build the hello world project ([basic scala tutorial](https://docs.scala-lang.org/getting-started-sbt-track/getting-started-with-scala-and-sbt-on-the-command-line.html)).
```bash
sbt new scala/hello-world.g8
```
This creates a folder called `hello-world-template`.

Stainless only works with Scala 2.11.8. Here the PR to bring Stainless to Scala 2.12.8: [![epfl-lara/stainless/pull/437](https://img.shields.io/github/issues/detail/s/epfl-lara/stainless/437.svg)](https://github.com/epfl-lara/stainless/pull/437).

To change the Scala version we first change the `scalaVersion` property in `build.sbt` to `scalaVersion=2.11.8`.

Then we can follow the [guide](https://epfl-lara.github.io/stainless/installation.html#usage-within-an-sbt-project) provided by Stainless.

### Tutorial
Now we can start with the [Stainless tutorial](https://epfl-lara.github.io/stainless/tutorial.html).

I had to add `resolvers += "uuverifiers" at "http://logicrunch.it.uu.se:4096/~wv/maven"` to `build.sbt`, because it did not find the princess resolver
[![epfl-lara/stainless/issue/443](https://img.shields.io/github/issues/detail/s/epfl-lara/stainless/443.svg)](https://github.com/epfl-lara/stainless/pull/443)
[![epfl-lara/stainless/issue/457](https://img.shields.io/github/issues/detail/s/epfl-lara/stainless/457.svg)](https://github.com/epfl-lara/stainless/pull/457).
