sealed abstract class ScriptInterpreter {
  def checkTransaction(transaction: Transaction): Boolean = {
    val inputOutputsNotZero =
      !(transaction.inputs.isEmpty || transaction.outputs.isEmpty)
    val txNotLargerThanBlock = transaction.bytes.size < Consensus.maxBlockSize
    val outputsSpendValidAmountsOfMoney = !transaction.outputs.exists(o =>
      o.value < CurrencyUnits.zero || o.value > Consensus.maxMoney)

    val outputValues = transaction.outputs.map(_.value)
    val totalSpentByOutputs: CurrencyUnit =
      outputValues.fold(CurrencyUnits.zero)(_ + _)
    val allOutputsValidMoneyRange = validMoneyRange(totalSpentByOutputs)
    val prevOutputTxIds = transaction.inputs.map(_.previousOutput.txId)
    val noDuplicateInputs = prevOutputTxIds.distinct.size == prevOutputTxIds.size

    val isValidScriptSigForCoinbaseTx = transaction.isCoinbase match {
//      case true =>
//        transaction.inputs.head.scriptSignature.asmBytes.size >= 2 &&
//          transaction.inputs.head.scriptSignature.asmBytes.size <= 100
      case false =>
        //since this is not a coinbase tx we cannot have any empty previous outs inside of inputs
        !transaction.inputs.exists(_.previousOutput == EmptyTransactionOutPoint)
    }
    (
      inputOutputsNotZero
      && txNotLargerThanBlock
      && outputsSpendValidAmountsOfMoney
      && noDuplicateInputs
      && allOutputsValidMoneyRange
      && noDuplicateInputs
      && isValidScriptSigForCoinbaseTx
    )
  }

  def validMoneyRange(currencyUnit: CurrencyUnit): Boolean = {
    currencyUnit >= CurrencyUnits.zero && currencyUnit <= Consensus.maxMoney
  }
}
