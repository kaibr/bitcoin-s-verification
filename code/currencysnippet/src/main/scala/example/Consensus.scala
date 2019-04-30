package example

sealed abstract class Consensus {

  def maxBlockSize: Long = 1000000

  def weightScalar: Long = 4

  def maxBlockWeight: Long = maxBlockSize * weightScalar

  def maxSigOps = 80000

  def maxMoney: CurrencyUnit = Satoshis(Int64(2100000000000000L))

  def maxPublicKeysPerMultiSig = 20
}

object Consensus extends Consensus
