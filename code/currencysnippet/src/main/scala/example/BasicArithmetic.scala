package example

import scala.util.Try

trait BasicArithmetic[N] {

  def +(n: N): N

  def addSafe(n: N): Try[N] = Try { this + n }

  def -(n: N): N

  def subtractSafe(n: N): Try[N] = Try { this - n }

  def *(factor: BigInt): N

  def multiplySafe(factor: BigInt): Try[N] = Try { this * factor }

  def *(factor: N): N

  def multiplySafe(factor: N): Try[N] = Try { this * factor }
}
