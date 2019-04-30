package example

import scala.util.Try

trait BasicArithmetic[N] {
  def +(n: N): N
}
