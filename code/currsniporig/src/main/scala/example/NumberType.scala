package example

import scala.util.{Failure, Success, Try}

sealed abstract class Number[T <: Number[T]]
    extends BasicArithmetic[T] {
  type A = BigInt

  protected def underlying: A

  def toInt: Int = toBigInt.bigInteger.intValueExact()
  def toLong: Long = toBigInt.bigInteger.longValueExact()
  def toBigInt: BigInt = underlying

  def andMask: BigInt

  def apply: A => T

  override def +(num: T): T = apply(checkResult(underlying + num.underlying))

  def >(num: T): Boolean = underlying > num.underlying
  def >=(num: T): Boolean = underlying >= num.underlying
  def <(num: T): Boolean = underlying < num.underlying
  def <=(num: T): Boolean = underlying <= num.underlying

  def <<(num: Int): T = this.<<(apply(num))
  def >>(num: Int): T = this.>>(apply(num))

  def <<(num: T): T = {
    checkIfInt(num).map { _ =>
      apply((underlying << num.toInt) & andMask)
    }.get
  }

  def >>(num: T): T = {
    if (num.toLong > 63) apply(0)
    else {
      checkIfInt(num).map { _ =>
        apply(underlying >> num.toInt)
      }.get
    }
  }

  def |(num: T): T = apply(checkResult(underlying | num.underlying))
  def &(num: T): T = apply(checkResult(underlying & num.underlying))
  def unary_- : T = apply(-underlying)

  private def checkResult(result: BigInt): A = {
    require((result & andMask) == result,
            "Result was out of bounds, got: " + result)
    result
  }

  private def checkIfInt(num: T): Try[Unit] = {
    if (num.toBigInt >= Int.MaxValue || num.toBigInt <= Int.MinValue) {
      Failure(
        new IllegalArgumentException(
          "Num was not in range of int, got: " + num))
    } else {
      Success(())
    }
  }
}

sealed abstract class SignedNumber[T <: Number[T]] extends Number[T]

sealed abstract class Int64 extends SignedNumber[Int64] {
  override def apply: A => Int64 = Int64(_)
  override def andMask = 0xffffffffffffffffL
}

trait BaseNumbers[T] {
  def zero: T
  def one: T
  def min: T
  def max: T
}

object Int64 extends BaseNumbers[Int64] {
  private case class Int64Impl(underlying: BigInt) extends Int64 {
    require(underlying >= -9223372036854775808L,
            "Number was too small for a int64, got: " + underlying)
    require(underlying <= 9223372036854775807L,
            "Number was too big for a int64, got: " + underlying)
  }

  lazy val zero = Int64(0)
  lazy val one = Int64(1)

  lazy val min = Int64(-9223372036854775808L)
  lazy val max = Int64(9223372036854775807L)

  def apply(long: Long): Int64 = Int64(BigInt(long))

  def apply(bigInt: BigInt): Int64 = Int64Impl(bigInt)
}

