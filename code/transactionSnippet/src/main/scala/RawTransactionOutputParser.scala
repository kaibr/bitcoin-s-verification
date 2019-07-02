import scodec.bits.ByteVector

/**
  * Created by chris on 1/11/16.
  * [[https://bitcoin.org/en/developer-reference#txout]]
  */
sealed abstract class RawTransactionOutputParser
  extends RawBitcoinSerializer[TransactionOutput] {

  /** Writes a single transaction output */
  override def write(output: TransactionOutput): ByteVector = {
    val satoshis: Satoshis = CurrencyUnits.toSatoshis(output.value)
    satoshis.bytes ++ output.scriptPubKey.bytes
  }

  override def read(bytes: ByteVector): TransactionOutput = {
    null
//    val satoshisBytes = bytes.take(8)
//    val satoshis = RawSatoshisSerializer.read(satoshisBytes)
//    //it doesn't include itself towards the size, thats why it is incremented by one
//    val scriptPubKeyBytes = bytes.slice(8, bytes.size)
//    val scriptPubKey = RawScriptPubKeyParser.read(scriptPubKeyBytes)
//    val parsedOutput = TransactionOutput(satoshis, scriptPubKey)
//    parsedOutput
  }

}

object RawTransactionOutputParser extends RawTransactionOutputParser
