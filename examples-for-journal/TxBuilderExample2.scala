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

class TxBuilderExample2 extends FlatSpec with MustMatchers {

  behavior of "TxBuilderExample2"

  it must "build a signed tx" in {

    //This is an example creating and verifying a transaction which references two outputs of a previous tx
    //This file should be placed within Bitcoin-S project in bitcoin-s-core/doc/src/test/scala/TxBuilderExample2.scala)
    //You can run this test case with the following commands

    //$ sbt
    //sbt> doc/testOnly *TxBuilderExample2 -- -z signed

    //generate fresh private keys that we are going to use in the scriptpubkeys
    val privKey1 = ECPrivateKey.freshPrivateKey
    val privKey2 = ECPrivateKey.freshPrivateKey

    //this is the script that the TxBuilder is going to create a
    //script signature that validly spends this scriptPubKeys
    val creditingSpk1 = P2PKHScriptPubKey(pubKey = privKey1.publicKey)
    val creditingSpk2 = P2PKHScriptPubKey(pubKey = privKey2.publicKey)

    val amount1 = Satoshis(Int64(3000))
    val amount2 = Satoshis(Int64(4900))


    //this is the utxo we are going to be spending
    val utxo1 = TransactionOutput(currencyUnit = amount1, scriptPubKey = creditingSpk1)
    val utxo2 = TransactionOutput(currencyUnit = amount2, scriptPubKey = creditingSpk2)


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
    //utxos we are trying to spend. If this were a real blockchain
    //we would need to reference the utxo set
    val creditingTx = BaseTransaction(version = Int32.one,
                                      inputs = List.empty,
                                      outputs = List(utxo1, utxo2),
                                      lockTime = UInt32.zero)

    //this is the information we need from the crediting tx
    //to properly "link" it in the transaction we are creating
    val outPoint1 = TransactionOutPoint(creditingTx.txId, UInt32.zero)
    val outPoint2 = TransactionOutPoint(creditingTx.txId, UInt32.one)


    // this contains all the information we need to
    // validly sign the utxos above
    val utxoSpendingInfo1 = BitcoinUTXOSpendingInfo(outPoint = outPoint1,
                                                   output = utxo1,
                                                   signers = List(privKey1),
                                                   redeemScriptOpt = None,
                                                   scriptWitnessOpt = None,
                                                   hashType =
                                                     HashType.sigHashAll)
    val utxoSpendingInfo2 = BitcoinUTXOSpendingInfo(outPoint = outPoint2,
                                                    output = utxo2,
                                                    signers = List(privKey2),
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

    //yay! Now we have a TxBuilder object that we can use
    //to sign the tx.
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

    //let's finally produce a validly signed tx
    //The 'sign' method is going produce a validly signed transaction
    //This is going to iterate through each of the 'utxos' and use
    //the corresponding 'UTXOSpendingInfo' to produce a validly
    //signed input. This tx has a
    //
    //2 inputs
    //2 outputs (destination and change outputs)
    //a fee rate of 1 satoshi/byte
    val signedTxF: Future[Transaction] = txBuilder.flatMap(_.sign)
    println("-------------------------------------------")
    signedTxF onComplete {
      case Success(tx) => {
        println("\nInputs:")
        tx.inputs.foreach(println)

        println("\nOutputs:")
        tx.outputs.foreach(println)

        //check validity of a transaction
        println("\nResult of transaction check: " + ScriptInterpreter.checkTransaction(tx))
      }
      case Failure(t) => println("------------------------- Err: " +
        t.getMessage)
    }
  }
}
