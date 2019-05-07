package example

import scala.util.{Failure, Success, Try}

sealed abstract class Number
    extends BasicArithmetic[Int64] {
  //type A = BigInt

  protected def underlying: BigInt

  /*def toInt: Int = toBigInt.bigInteger.intValueExact()
  def toLong: Long = toBigInt.bigInteger.longValueExact()
  def toBigInt: BigInt = underlying
*/
  //def andMask: BigInt

  def apply: BigInt => Int64

  override def +(num: Int64): Int64 = apply(checkResult(underlying + num.underlying))

  private def checkResult(result: BigInt): BigInt = {
    require(result <= 9223372036854775807L && result >= -9223372036854775808L, "Result was out of bounds, got: " + result)
    result
  }
}

sealed abstract class SignedNumber extends Number

sealed abstract class Int64 extends SignedNumber {
  override def apply: BigInt => Int64 = Int64(_)
  //override def andMask = 0xffffffffffffffffL
}

trait BaseNumbers[T] {
  def zero: T
  def one: T
  def min: T
  def max: T
}

case object Int64 extends BaseNumbers[Int64] {
//  private case class Int64Impl(underlying: BigInt) extends Int64 {
//    require(underlying >= -9223372036854775808L,
//            "Number was too small for a int64, got: " + underlying)
//    require(underlying <= 9223372036854775807L,
//            "Number was too big for a int64, got: " + underlying)
//  }

  lazy val zero = Int64(0)
  lazy val one = Int64(1)

  lazy val min = Int64(-9223372036854775808L)
  lazy val max = Int64(9223372036854775807L)

  def apply(long: Long): Int64 = Int64(BigInt(long))

  def apply(bigInt: BigInt): Int64 = Int64Impl(bigInt)
}

case class Int64Impl(underlying: BigInt) extends Int64 {
  require(underlying >= -9223372036854775808L)//, "Number was too small for a int64, got: " + underlying)
  require(underlying <= 9223372036854775807L)//, "Number was too big for a int64, got: " + underlying)
}

