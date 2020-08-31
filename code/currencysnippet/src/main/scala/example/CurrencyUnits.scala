package example

sealed abstract class CurrencyUnit
    extends BasicArithmetic[CurrencyUnit] {
  //type A

  def satoshis: Satoshis

  override def +(c: CurrencyUnit): CurrencyUnit = {
    Satoshis(satoshis.underlying + c.satoshis.underlying)
  }

  //def toBigDecimal: BigDecimal

  protected def underlying: Int64
}

sealed abstract class Satoshis extends CurrencyUnit {
  //override type A = Int64

 // override def toString: String = s"$toLong sat"

  override def satoshis: Satoshis = this

  //override def toBigDecimal = BigDecimal(toBigInt)

  //def toBigInt: BigInt = BigInt(toLong)

 // def toLong: Long = underlying.toLong

  def ==(satoshis: Satoshis): Boolean = underlying == satoshis.underlying
}

case object Satoshis extends BaseNumbers[Satoshis] {

  val min = Satoshis(Int64.min)
  val max = Satoshis(Int64.max)
  val zero = Satoshis(Int64.zero)
  val one = Satoshis(Int64.one)

  def apply(int64: Int64): Satoshis = SatoshisImpl(int64)

  //private case class SatoshisImpl(underlying: Int64) extends Satoshis
}

case class SatoshisImpl(underlying: Int64) extends Satoshis

object CurrencyUnits {
  val btcToSatoshiScalar: BigInt = 100000000
 // val satoshisToBTCScalar: BigDecimal = BigDecimal(1.0) / btcToSatoshiScalar
  val oneBTC: CurrencyUnit = Satoshis(Int64(btcToSatoshiScalar))
  val oneMBTC: CurrencyUnit = Satoshis(Int64(btcToSatoshiScalar / 1000))
  val zero: CurrencyUnit = Satoshis.zero
  val negativeSatoshi = Satoshis(Int64(-1))

  def toSatoshis(unit: CurrencyUnit): Satoshis = unit match {
    case x: Satoshis => x
  }
}
