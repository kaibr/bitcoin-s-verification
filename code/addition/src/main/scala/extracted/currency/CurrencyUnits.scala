package extracted.currency

import extracted.number.{BaseNumbers, Int64}

sealed abstract class CurrencyUnit {
  type A
  def satoshis: Satoshis
  def ==(c: CurrencyUnit): Boolean = satoshis == c.satoshis
  def +(c: CurrencyUnit): CurrencyUnit = {
    Satoshis(satoshis.underlying + c.satoshis.underlying)
  }
  protected def underlying: A
}

sealed abstract class Satoshis extends CurrencyUnit {
  override type A = Int64
  override def satoshis: Satoshis = this
  def toBigInt: BigInt = BigInt(toLong)
  def toLong: Long = underlying.toLong
  def ==(satoshis: Satoshis): Boolean = underlying == satoshis.underlying
}

object Satoshis extends BaseNumbers[Satoshis] {
  val zero = Satoshis(Int64.zero)
  def apply(int64: Int64): Satoshis = SatoshisImpl(int64)
  private case class SatoshisImpl(underlying: Int64) extends Satoshis
}
