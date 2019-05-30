  package code.initial

  trait BasicArithmetic[N] {
    def +(n: N): N
  }

  sealed abstract class Number[T <: Number[T]]
    extends BasicArithmetic[T] {
    type A = BigInt

    protected def underlying: A

    def toLong: Long = toBigInt.bigInteger.longValueExact()
    def toBigInt: BigInt = underlying

    def andMask: BigInt

    def apply: A => T

    override def +(num: T): T = apply(checkResult(underlying + num.underlying))

    private def checkResult(result: BigInt): A = {
      require((result & andMask) == result,
        "Result was out of bounds, got: " + result)
      result
    }
  }

  sealed abstract class SignedNumber[T <: Number[T]] extends Number[T]

  sealed abstract class Int64 extends SignedNumber[Int64] {
    override def apply: A => Int64 = Int64(_)
    override def andMask = 0xffffffffffffffffL
  }

  trait BaseNumbers[T] {
    def zero: T
    def one: T
    def min: T //maybe remove
    def max: T //maybe remove
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

    lazy val min = Int64(-9223372036854775808L) //maybe remove
    lazy val max = Int64(9223372036854775807L) //maybe remove

    def apply(long: Long): Int64 = Int64(BigInt(long))

    def apply(bigInt: BigInt): Int64 = Int64Impl(bigInt)
  }


  sealed abstract class CurrencyUnit
    extends BasicArithmetic[CurrencyUnit] {
    type A

    def satoshis: Satoshis

    def ==(c: CurrencyUnit): Boolean = satoshis == c.satoshis

    override def +(c: CurrencyUnit): CurrencyUnit =
      Satoshis(satoshis.underlying + c.satoshis.underlying)

    def toBigDecimal: BigDecimal

    protected def underlying: A
  }

  sealed abstract class Satoshis extends CurrencyUnit {
    override type A = Int64

    override def satoshis: Satoshis = this

    override def toBigDecimal = BigDecimal(toBigInt)

    def toBigInt: BigInt = BigInt(toLong)

    def toLong: Long = underlying.toLong

    def ==(satoshis: Satoshis): Boolean = underlying == satoshis.underlying
  }

  object Satoshis extends BaseNumbers[Satoshis] {
    val min = Satoshis(Int64.min) //maybe remove
    val max = Satoshis(Int64.max) //maybe remove
    val zero = Satoshis(Int64.zero)
    val one = Satoshis(Int64.one)

    def apply(int64: Int64): Satoshis = SatoshisImpl(int64)

    private case class SatoshisImpl(underlying: Int64) extends Satoshis
  }