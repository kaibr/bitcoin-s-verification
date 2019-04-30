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
  override def -(num: T): T = apply(checkResult(underlying - num.underlying))
  override def *(factor: BigInt): T = apply(checkResult(underlying * factor))
  override def *(num: T): T = apply(checkResult(underlying * num.underlying))

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
    //this check is for weird behavior with the jvm and shift rights
    //https://stackoverflow.com/questions/47519140/bitwise-shift-right-with-long-not-equaling-zero/47519728#47519728
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

sealed abstract class UnsignedNumber[T <: Number[T]] extends Number[T]

sealed abstract class UInt5 extends UnsignedNumber[UInt5] {
  override def apply: A => UInt5 = UInt5(_)

  override def andMask: BigInt = 0x1f

  def byte: Byte = toInt.toByte

  def toUInt8: UInt8 = UInt8(toInt)
}

sealed abstract class UInt8 extends UnsignedNumber[UInt8] {
  override def apply: A => UInt8 = UInt8(_)

  override def andMask = 0xff

  def toUInt5: UInt5 = {
    //this will throw if not in range of a UInt5, come back and look later
    UInt5(toInt)
  }
}

sealed abstract class UInt32 extends UnsignedNumber[UInt32] {
  override def apply: A => UInt32 = UInt32(_)

  override def andMask = 0xffffffffL
}

sealed abstract class UInt64 extends UnsignedNumber[UInt64] {
  override def apply: A => UInt64 = UInt64(_)
  override def andMask = 0xffffffffffffffffL
}

sealed abstract class Int32 extends SignedNumber[Int32] {
  override def apply: A => Int32 = Int32(_)
  override def andMask = 0xffffffff
}

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

object UInt5 extends BaseNumbers[UInt5] {
  private case class UInt5Impl(underlying: BigInt) extends UInt5 {
    require(underlying.toInt >= 0, s"Cannot create UInt5 from $underlying")
    require(underlying <= 31, s"Cannot create UInt5 from $underlying")
  }

  lazy val zero = UInt5(0.toByte)
  lazy val one = UInt5(1.toByte)

  lazy val min: UInt5 = zero
  lazy val max = UInt5(31.toByte)

  def apply(byte: Byte): UInt5 = fromByte(byte)

  def apply(bigInt: BigInt): UInt5 = {

    require(
      bigInt.toByteArray.length == 1,
      s"To create a uint5 from a BigInt it must be less than 32. Got: ${bigInt.toString}")

    UInt5.fromByte(bigInt.toByteArray.head)
  }

  def fromByte(byte: Byte): UInt5 = {
    UInt5Impl(BigInt(byte))
  }

  def toUInt5(b: Byte): UInt5 = {
    fromByte(b)
  }

}

object UInt8 extends BaseNumbers[UInt8] {
  private case class UInt8Impl(underlying: BigInt) extends UInt8 {
    require(isValid(underlying),
            "Invalid range for a UInt8, got: " + underlying)
  }
  lazy val zero = UInt8(0.toShort)
  lazy val one = UInt8(1.toShort)

  lazy val min: UInt8 = zero
  lazy val max = UInt8(255.toShort)

  def apply(short: Short): UInt8 = UInt8(BigInt(short))

  def apply(bigint: BigInt): UInt8 = UInt8Impl(bigint)

  def isValid(bigInt: BigInt): Boolean = bigInt >= 0 && bigInt < 256

  def checkBounds(res: BigInt): UInt8 = {
    if (res > max.underlying || res < min.underlying) {
      throw new IllegalArgumentException(
        "Out of boudns for a UInt8, got: " + res)
    } else UInt8(res.toShort)
  }
}

object UInt32 extends BaseNumbers[UInt32] {
  private case class UInt32Impl(underlying: BigInt) extends UInt32 {
    require(
      underlying >= 0,
      "We cannot have a negative number in an unsigned number, got: " + underlying)
    require(
      underlying <= 4294967295L,
      "We cannot have a number larger than 2^32 -1 in UInt32, got: " + underlying)
  }

  lazy val zero = UInt32(0)
  lazy val one = UInt32(1)

  lazy val min: UInt32 = zero
  lazy val max = UInt32(4294967295L)

  def apply(long: Long): UInt32 = UInt32(BigInt(long))

  def apply(bigInt: BigInt): UInt32 = UInt32Impl(bigInt)

  def checkBounds(res: BigInt): UInt32 = {
    if (res > max.underlying || res < min.underlying) {
      throw new IllegalArgumentException(
        "Out of boudns for a UInt8, got: " + res)
    } else UInt32(res)
  }

}

object UInt64 extends BaseNumbers[UInt64] {
  private case class UInt64Impl(underlying: BigInt) extends UInt64 {
    require(
      underlying >= 0,
      "We cannot have a negative number in an unsigned number: " + underlying)
    require(
      underlying <= BigInt("18446744073709551615"),
      "We cannot have a number larger than 2^64 -1 in UInt64, got: " + underlying)
  }

  lazy val zero = UInt64(BigInt(0))
  lazy val one = UInt64(BigInt(1))

  lazy val min: UInt64 = zero
  lazy val max = UInt64(BigInt("18446744073709551615"))

  def apply(num: BigInt): UInt64 = UInt64Impl(num)

}

object Int32 extends BaseNumbers[Int32] {
  private case class Int32Impl(underlying: BigInt) extends Int32 {
    require(underlying >= -2147483648,
            "Number was too small for a int32, got: " + underlying)
    require(underlying <= 2147483647,
            "Number was too large for a int32, got: " + underlying)
  }

  lazy val zero = Int32(0)
  lazy val one = Int32(1)

  lazy val min = Int32(-2147483648)
  lazy val max = Int32(2147483647)

  def apply(int: Int): Int32 = Int32(BigInt(int))

  def apply(bigInt: BigInt): Int32 = Int32Impl(bigInt)
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
