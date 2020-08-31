package verified.number

sealed abstract class Number {
  protected def underlying: BigInt
  def toBigInt: BigInt = underlying
  def apply: BigInt => Int64
  def +(num: Int64): Int64 = apply(underlying + num.underlying)
}

sealed abstract class SignedNumber extends Number

sealed abstract class Int64 extends SignedNumber {
  override def apply: BigInt => Int64 = Int64(_)
}

trait BaseNumbers[T] {
  def zero: T
}

case object Int64 extends BaseNumbers[Int64] {
  lazy val zero = Int64(0)
  def apply(bigInt: BigInt): Int64 = Int64Impl(bigInt)
}

private case class Int64Impl(underlying: BigInt) extends Int64 {
  require(underlying >= BigInt("-9223372036854775808"))
  require(underlying <= BigInt("9223372036854775807"))
}
