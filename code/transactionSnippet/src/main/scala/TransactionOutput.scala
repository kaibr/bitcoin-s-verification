import scodec.bits.ByteVector

/**
  * Created by chris on 12/26/15.
  */
sealed abstract class TransactionOutput extends NetworkElement {

  def value: CurrencyUnit
  def scriptPubKey: ScriptPubKey

  //https://bitcoin.org/en/developer-reference#txout
  override def size = scriptPubKey.size + 8

  override def bytes = RawTransactionOutputParser.write(this)
}

//case object EmptyTransactionOutput extends TransactionOutput {
//  override def value = CurrencyUnits.negativeSatoshi
//  override def scriptPubKey = ScriptPubKey.empty
//}

object TransactionOutput extends Factory[TransactionOutput] {
  private case class TransactionOutputImpl(
                                            value: CurrencyUnit,
                                            scriptPubKey: ScriptPubKey)
    extends TransactionOutput

  def fromBytes(bytes: ByteVector): TransactionOutput =
    RawTransactionOutputParser.read(bytes)

  def apply(
             currencyUnit: CurrencyUnit,
             scriptPubKey: ScriptPubKey): TransactionOutput = {
    TransactionOutputImpl(currencyUnit, scriptPubKey)
  }
}
