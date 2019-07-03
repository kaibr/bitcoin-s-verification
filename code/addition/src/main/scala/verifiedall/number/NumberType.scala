package verifiedall.number

sealed abstract class Number {
  protected def underlying: BigInt
  def toBigInt: BigInt = underlying
  def apply: BigInt => Int64

  def +(num: Int64): Int64 = {
    require(Int64.isInRange(underlying + num.underlying))
    apply(underlying + num.underlying)
  }
}

sealed abstract class SignedNumber extends Number

sealed abstract class Int64 extends SignedNumber {
  override def apply: BigInt => Int64 = num => {
    require(Int64.isInRange(num))
    Int64(num)
  }
}

trait BaseNumbers[T] {
  def zero: T
}

case object Int64 extends BaseNumbers[Int64] {
  lazy val zero = Int64(0)

  def apply(bigInt: BigInt): Int64 = {
    require(Int64.isInRange(bigInt))
    Int64Impl(bigInt)
  }

  def isInRange(num: BigInt): Boolean = num >= BigInt("-9223372036854775808") && num <= BigInt("9223372036854775807")
  def isSumInRange(num1: Int64, num2: Int64): Boolean = Int64.isInRange(num1.underlying + num2.underlying)
}

private case class Int64Impl(underlying: BigInt) extends Int64 {
  require(Int64.isInRange(underlying))
}
