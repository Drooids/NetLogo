// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.headless
package lang

import java.io.File
import org.scalatest.{ FunSuite, Tag }
import org.nlogo.util.SlowTest

/// top level entry points

class TestCommands extends Finder {
  override def files =
    TxtsInDir("test/commands")
}
class TestReporters extends Finder {
  override def files =
    TxtsInDir("test/reporters")
}
class TestModels extends Finder {
  override def files =
    TxtsInDir("models/test")
      .filterNot(_.getName.startsWith("checksums"))
}
class TestExtensions extends Finder {
  override def files = new Iterable[File] {
    override def iterator = {
    def filesInDir(parent: File): Iterable[File] =
      parent.listFiles.flatMap{f =>
        if(f.isDirectory) filesInDir(f)
        else List(f)}
    filesInDir(new File("extensions"))
      .filter(_.getName == "tests.txt")
      .iterator
    }
  }
}

/// common infrastructure

trait Finder extends FunSuite with SlowTest {
  def files: Iterable[File]
  def tests = Parser.parseFiles(files)
  // parse tests first, then run them
  for(t <- tests if shouldRun(t))
    test(t.fullName, new Tag(t.suiteName){}, new Tag(t.fullName){}) {
      new Runner(t).run()
    }
  // on the core branch the _3D tests are gone, but extensions tests still have them since we
  // didn't branch the extensions, so we still need to filter those out - ST 1/13/12
  def shouldRun(t: LanguageTest) =
    !t.testName.endsWith("_3D") && {
      import org.nlogo.api.Version.useGenerator
      if (t.testName.startsWith("Generator"))
        useGenerator
      else if (t.testName.startsWith("NoGenerator"))
        !useGenerator
      else true
    }
}

case class TxtsInDir(dir: String) extends Iterable[File] {
  override def iterator =
    new File(dir).listFiles
      .filter(_.getName.endsWith(".txt"))
      .filterNot(_.getName.containsSlice("SDM"))
      .filterNot(_.getName.containsSlice("HubNet"))
      .iterator
}
