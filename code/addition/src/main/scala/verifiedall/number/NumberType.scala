package verifiedall.number

/**
  * This abstract class is meant to represent a signed and unsigned number in C
  * This is useful for dealing with codebases/protocols that rely on C's
  * unsigned integer types
  */
sealed abstract class Number {
  /** The underlying scala number used to to hold the number */
  protected def underlying: BigInt

  def toBigInt: BigInt = underlying

  /** Factory function to create the underlying T, for instance a UInt32 */
  def apply: BigInt => Int64

  def +(num: Int64): Int64 = {
    require(Int64.isInRange(underlying + num.underlying))
    apply(checkResult(underlying + num.underlying))
  }

  /**
    * Checks if the given result is within the range
    * of this number type
    */
  private def checkResult(result: BigInt): BigInt = {
    require(Int64.isInRange(result))
    result
  }
}

/**
  * Represents a signed number in our number system
  * Instances of this is [[Int64]]
  */
sealed abstract class SignedNumber extends Number

/**
  * Represents a int64_t in C
  */
sealed abstract class Int64 extends SignedNumber {
  override def apply: BigInt => Int64 = num => {
    require(Int64.isInRange(num))
    Int64(num)
  }
}

/**
  * Represents various numbers that should be implemented
  * inside of any companion object for a number
  */
trait BaseNumbers[T] {
  def zero: T
  def one: T
  def min: T
  def max: T
}

case object Int64 extends BaseNumbers[Int64] {
  lazy val zero = Int64(0)
  lazy val one = Int64(1)

  lazy val min = Int64(BigInt("-9223372036854775808"))
  lazy val max = Int64(BigInt("9223372036854775807"))

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
