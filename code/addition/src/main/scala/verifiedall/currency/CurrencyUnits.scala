package verifiedall.currency

import verifiedall.number.{BaseNumbers, Int64}
import stainless.lang._

sealed abstract class CurrencyUnit {
  def satoshis: Satoshis

  def ==(c: CurrencyUnit): Boolean = satoshis == c.satoshis

  def +(c: CurrencyUnit): CurrencyUnit = {
    require(Int64.isSumInRange(this.underlying, c.underlying))
    Satoshis(satoshis.underlying + c.satoshis.underlying)
  } ensuring (res =>
    (c == Satoshis.zero) ==> (res == this))

  protected def underlying: Int64
}

sealed abstract class Satoshis extends CurrencyUnit {
  override def satoshis: Satoshis = this

  def toBigInt: BigInt = underlying.toBigInt

  def ==(satoshis: Satoshis): Boolean = underlying == satoshis.underlying
}

case object Satoshis extends BaseNumbers[Satoshis] {
  val zero = Satoshis(Int64.zero)

  def apply(int64: Int64): Satoshis = SatoshisImpl(int64)
}

private case class SatoshisImpl(underlying: Int64) extends Satoshis
