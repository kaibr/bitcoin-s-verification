import org.bitcoins.core.config.RegTest
import org.bitcoins.core.crypto.ECPrivateKey
import org.bitcoins.core.currency.Satoshis
import org.bitcoins.core.number.{Int32, Int64, UInt32}
import org.bitcoins.core.protocol.script.P2PKHScriptPubKey
import org.bitcoins.core.protocol.transaction.{BaseTransaction, Transaction, TransactionOutPoint, TransactionOutput}
import org.bitcoins.core.script.crypto.HashType
import org.bitcoins.core.script.interpreter.ScriptInterpreter
import org.bitcoins.core.wallet.builder.BitcoinTxBuilder
import org.bitcoins.core.wallet.fee.SatoshisPerByte
import org.bitcoins.core.wallet.utxo.BitcoinUTXOSpendingInfo
import org.scalatest.{FlatSpec, MustMatchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class TxBuilderExample1 extends FlatSpec with MustMatchers {

  behavior of "TxBuilderExample1"

  it must "build a signed tx" in {

    //This is an example to try to create a transaction which double-spends the output of a previous tx
    //This file should be placed within Bitcoin-S project in bitcoin-s-core/doc/src/test/scala/TxBuilderExample1.scala)
    //You can run this test case with the following commands

    //$ sbt
    //sbt> doc/testOnly *TxBuilderExample1 -- -z signed

    //generate a fresh private key that we are going to use in the scriptpubkey
    val privKey = ECPrivateKey.freshPrivateKey

    //this is the script that the TxBuilder is going to create a
    //script signature that spends this scriptPubKey
    val creditingSpk = P2PKHScriptPubKey(pubKey = privKey.publicKey)

    //value of a previous output
    val amount = Satoshis(Int64(4000))

    //utxo we are going to be spending
    val utxo = TransactionOutput(currencyUnit = amount, scriptPubKey = creditingSpk)

    //the private key that locks the funds for the script we are spending too
    val destinationPrivKey = ECPrivateKey.freshPrivateKey

    //the amount we are sending -- 5000 satoshis -- to the destinationSPK
    val destinationAmount = Satoshis(Int64(5000))

    //the script that corresponds to destination private key, this is what is protecting the money
    val destinationSPK =
      P2PKHScriptPubKey(pubKey = destinationPrivKey.publicKey)

    //this is where we are sending money too
    //we could add more destinations here if we
    //wanted to batch transactions
    val destinations = {
      val destination1 = TransactionOutput(currencyUnit = destinationAmount,
                                           scriptPubKey = destinationSPK)

      List(destination1)
    }

    //we have to fabricate a transaction that contains the
    //utxo we are trying to spend.
    val creditingTx = BaseTransaction(version = Int32.one,
                                      inputs = List.empty,
                                      outputs = List(utxo),
                                      lockTime = UInt32.zero)

    //this is the information we need from the crediting tx
    //to "link" it in the transaction we are creating
    //we link it twice to try to double spend
    //notes: outPoints with the same entries are equal
    val outPoint1 = TransactionOutPoint(creditingTx.txId, UInt32.zero)
    val outPoint2 = TransactionOutPoint(creditingTx.txId, UInt32.zero)

    // this contains all the information we need to
    // validly sign the utxo above
    val utxoSpendingInfo1 = BitcoinUTXOSpendingInfo(outPoint = outPoint1,
                                                   output = utxo,
                                                   signers = List(privKey),
                                                   redeemScriptOpt = None,
                                                   scriptWitnessOpt = None,
                                                   hashType =
                                                     HashType.sigHashAll)
    val utxoSpendingInfo2 = BitcoinUTXOSpendingInfo(outPoint = outPoint2,
                                                    output = utxo,
                                                    signers = List(privKey),
                                                    redeemScriptOpt = None,
                                                    scriptWitnessOpt = None,
                                                    hashType =
                                                      HashType.sigHashAll)

    //all of the utxo spending information
    val utxos: List[BitcoinUTXOSpendingInfo] = List(utxoSpendingInfo1, utxoSpendingInfo2)

    //this is how much we are going to pay as a fee to the network
    //for this example, we are going to pay 1 satoshi per byte
    val feeRate = SatoshisPerByte(Satoshis.one)

    val changePrivKey = ECPrivateKey.freshPrivateKey
    val changeSPK = P2PKHScriptPubKey(pubKey = changePrivKey.publicKey)

    // the network we are on, for this example we are using
    // the regression test network. This is a network you control
    // on your own machine
    val networkParams = RegTest

    //TxBuilder object that we can use to sign the tx.
    val txBuilder: Future[BitcoinTxBuilder] = {
      BitcoinTxBuilder(
        destinations = destinations,
        utxos = utxos,
        feeRate = feeRate,
        changeSPK = changeSPK,
        network = networkParams
      )
    }

    txBuilder.failed.foreach { case err => println(err.getMessage) }

    //let's finally produce a signed tx
    //The 'sign' method is going produce a validly signed transaction
    //This is going to iterate through each of the 'utxos' and use
    //the corresponding 'UTXOSpendingInfo' to produce a validly
    //signed input.
    // This tx ends up with an error, because both outPoints are mapped to
    // a single entry in a map of the TxBuilder. So there are
    // 1 input with 4000 satoshis
    // 1 output with 5000 satoshis
    // in sign() of TxBuilder the check of amounts is not passed.
    val signedTxF: Future[Transaction] = txBuilder.flatMap(_.sign)
    println("-------------------------------------------")
    signedTxF onComplete {
      case Success(tx) => {
        println("\nInputs:")
        tx.inputs.foreach(println)

        println("\nOutputs:")
        tx.outputs.foreach(println)

        println("\nResult of transaction check: " + ScriptInterpreter.checkTransaction(tx))
      }
      case Failure(t) => println("------------------------- Err: " +
        t.getMessage)
    }
  }

}
