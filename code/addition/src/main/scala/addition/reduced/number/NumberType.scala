package addition.reduced.number

import org.bitcoins.core.protocol.NetworkElement
import org.bitcoins.core.util.{BitcoinSUtil, Factory, NumberUtil}
import scodec.bits.ByteVector

import scala.util.{Failure, Success, Try}

/**
  * Created by chris on 6/4/16.
  */
/**
  * This abstract class is meant to represent a signed and unsigned number in C
  * This is useful for dealing with codebases/protocols that rely on C's
  * unsigned integer types
  */
sealed abstract class Number[T <: Number[T]]
  extends NetworkElement
    with BasicArithmetic[T] {
  type A = BigInt

  /** The underlying scala number used to to hold the number */
  protected def underlying: A

  def toInt: Int = toBigInt.bigInteger.intValueExact()
  def toLong: Long = toBigInt.bigInteger.longValueExact()
  def toBigInt: BigInt = underlying

  /**
    * This is used to determine the valid amount of bytes in a number
    * for instance a UInt8 has an andMask of 0xff
    * a UInt32 has an andMask of 0xffffffff
    */
  def andMask: BigInt

  /** Factory function to create the underlying T, for instance a UInt32 */
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

  /**
    * Checks if the given result is within the range
    * of this number type
    */
  private def checkResult(result: BigInt): A = {
    require((result & andMask) == result,
      "Result was out of bounds, got: " + result)
    result
  }

  /** Checks if the given nubmer is within range of a Int */
  private def checkIfInt(num: T): Try[Unit] = {
    if (num.toBigInt >= Int.MaxValue || num.toBigInt <= Int.MinValue) {
      Failure(
        new IllegalArgumentException(
          "Num was not in range of int, got: " + num))
    } else {
      Success(())
    }
  }

  override def bytes: ByteVector = BitcoinSUtil.decodeHex(hex)
}

/**
  * Represents a signed number in our number system
  * Instances of this are [[Int32]] or [[Int64]]
  */
sealed abstract class SignedNumber[T <: Number[T]] extends Number[T]

/**
  * Represents a int64_t in C
  */
sealed abstract class Int64 extends SignedNumber[Int64] {
  override def apply: A => Int64 = Int64(_)
  override def andMask = 0xffffffffffffffffL
  override def hex: String = BitcoinSUtil.encodeHex(toLong)
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

object Int64 extends Factory[Int64] with BaseNumbers[Int64] {
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

  override def fromBytes(bytes: ByteVector): Int64 = {
    require(bytes.size <= 8, "We cannot have an Int64 be larger than 8 bytes")
    Int64(BigInt(bytes.toArray).toLong)
  }

  def apply(long: Long): Int64 = Int64(BigInt(long))

  def apply(bigInt: BigInt): Int64 = Int64Impl(bigInt)
}
