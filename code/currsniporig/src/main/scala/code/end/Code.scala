package code.end

trait BasicArithmetic[N] {
  def +(n: N): N
}

sealed abstract class Number
    extends BasicArithmetic[Int64] {

  protected def underlying: BigInt

  def toBigInt: BigInt = underlying

  def apply: BigInt => Int64

  override def +(num: Int64): Int64 = {
    require(
          num.underlying <= BigInt(0)
      && this.underlying <= BigInt("9223372036854775807")
      && this.underlying >= BigInt("-9223372036854775808")
    )
    apply(checkResult(underlying + num.underlying))
  }

  private def checkResult(result: BigInt): BigInt = {
    require(
         result <= BigInt("9223372036854775807")
      && result >= BigInt("-9223372036854775808")
    )
    result
  }
}

sealed abstract class SignedNumber extends Number

sealed abstract class Int64 extends SignedNumber {
  override def apply: BigInt => Int64 = Int64(_)
}

trait BaseNumbers[T] {
  def zero: T
  def one: T
}

case object Int64 extends BaseNumbers[Int64] {
  lazy val zero = Int64(0)
  lazy val one = Int64(1)

  def apply(bigInt: BigInt): Int64 = Int64Impl(bigInt)
}

private case class Int64Impl(underlying: BigInt) extends Int64 {
  require(underlying >= BigInt("-9223372036854775808"))
  require(underlying <= BigInt("9223372036854775807"))
}

sealed abstract class CurrencyUnit
    extends BasicArithmetic[CurrencyUnit] {
  def satoshis: Satoshis

  def ==(c: CurrencyUnit): Boolean = satoshis == c.satoshis

  override def +(c: CurrencyUnit): CurrencyUnit = {
    require(
         c.satoshis == Satoshis.zero
      && this.underlying.toBigInt <= BigInt("9223372036854775807")
      && this.underlying.toBigInt >= BigInt("-9223372036854775808")
    )
    Satoshis(satoshis.underlying + c.satoshis.underlying)
  } ensuring(res => res.satoshis == this.satoshis)

  protected def underlying: Int64
}

sealed abstract class Satoshis extends CurrencyUnit {
  override def satoshis: Satoshis = this

  def toBigInt: BigInt = underlying.toBigInt

  def ==(satoshis: Satoshis): Boolean = underlying == satoshis.underlying
}

case object Satoshis extends BaseNumbers[Satoshis] {
  val zero = Satoshis(Int64.zero)
  val one = Satoshis(Int64.one)

  def apply(int64: Int64): Satoshis = SatoshisImpl(int64)
}

private case class SatoshisImpl(underlying: Int64) extends Satoshis