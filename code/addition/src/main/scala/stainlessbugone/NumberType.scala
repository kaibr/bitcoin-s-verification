package stainlessbugone

/**
  * This abstract class is meant to represent a signed and unsigned number in C
  * This is useful for dealing with codebases/protocols that rely on C's
  * unsigned integer types
  */
sealed abstract class Number {
  /** The underlying scala number used to to hold the number */
  protected def underlying: BigInt

  /** Factory function to create the underlying T, for instance a UInt32 */
  def apply: BigInt => Int64

  def +(num: Int64): Int64 = Int64(underlying + num.underlying)

  private def checkResult(result: BigInt): BigInt = result
}

/**
  * Represents a int64_t in C
  */
case class Int64(underlying: BigInt) extends Number {
  require(true)
}
