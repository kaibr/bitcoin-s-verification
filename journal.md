# Journal

## Stainless

### Presetup
First, let's install sbt, the **s**cala **b**uild **t**ool, so we can later add the `sbt-stainless` plugin to our build environment.
[Installation instruction](https://www.scala-sbt.org/download.html).

Stainless as well as Scala recommends the usage of JVM 8. [<sup>[1]</sup>](https://docs.scala-lang.org/overviews/jdk-compatibility/overview.html) [<sup>[2]</sup>](https://epfl-lara.github.io/stainless/installation.html)

There are different ways to force sbt to use JVM 8.
The method mentioned [here](https://stackoverflow.com/questions/25926111/how-to-force-sbt-to-use-java-8) by adding `-target:jvm-1.8` to scalac does not work in `Scala < 2.11.4`.
So the simplest way might be adding an alias like this:
```bash
alias sbt='JAVA_HOME=/usr/lib/jvm/java-8-openjdk; PATH=$JAVA_HOME/bin:$PATH; sbt'
```

### Setup
Now we can build the hello world project ([basic scala tutorial](https://docs.scala-lang.org/getting-started-sbt-track/getting-started-with-scala-and-sbt-on-the-command-line.html)).
```bash
sbt new scala/hello-world.g8
```
This creates a folder called `hello-world-template`.

Stainless only works with Scala 2.11.8. Here the PR to bring Stainless to Scala 2.12.8: [![epfl-lara/stainless/pull/437](https://img.shields.io/github/issues/detail/s/epfl-lara/stainless/437.svg)](https://github.com/epfl-lara/stainless/pull/437).

To change the Scala version we first change the `scalaVersion` property in `build.sbt` to `scalaVersion=2.11.8`.

Then we can follow the [guide](https://epfl-lara.github.io/stainless/installation.html#usage-within-an-sbt-project) provided by Stainless.

### Tutorial
Now we can start with the [Stainless tutorial](https://epfl-lara.github.io/stainless/tutorial.html).

I had to add `resolvers += "uuverifiers" at "http://logicrunch.it.uu.se:4096/~wv/maven"` to `build.sbt`, because it did not find the princess resolver
[![epfl-lara/stainless/issue/443](https://img.shields.io/github/issues/detail/s/epfl-lara/stainless/443.svg)](https://github.com/epfl-lara/stainless/pull/443)
[![epfl-lara/stainless/issue/457](https://img.shields.io/github/issues/detail/s/epfl-lara/stainless/457.svg)](https://github.com/epfl-lara/stainless/pull/457).

## Bitcoin-s

The Scala implementation of Bitcoin can be installed from the git repository Bitcoin-S-Core:
```bash
$ git clone https://github.com/bitcoin-s/bitcoin-s-core.git
```

Our main goal is to test whether a non-coinbase transaction is able to create new coins. It means that the total value of outputs in a transaction should not be greater than the total value of inputs.


### Creation of a valid transaction

First of all, creation of a transaction will be tested. The class `bitcoin-s-core/doc/src/test/scala/TxBuilderExample.scala` creates a signed bitcoin transaction having 10000 Satoshis as an Input, spending 5000 Satoshis and paying 1 Satoshi per byte as a Fee. To run the class in sbt console:

```bash
$ sbt
sbt> doc/testOnly *TxBuilderExample -- -z signed
```

The tranasaction is successfully created. The output of the class:

```
Inputs:
TransactionInputImpl(TransactionOutPointImpl(DoubleSha256DigestImpl(16773b048950aed7a2ff8ad5e6c005efd034749d5b18ea2026a5bdf5f4c02c70),UInt32Impl(0)),P2PKHScriptSignature(6b48304502210085f7e4dde6fd679b488668decebb3c5e2c46eee5b85d795679ecaab09aa6431202201c61e521f500c0d3f03cbe2eabae3ab7dd0a1b146a38d44b454089813a6b0cbf0121022473cfb0d50832a721a2995ca402ebc27a1d2ae2cd80a9adb0337b24a2a1d6b6),UInt32Impl(0))

Outputs:
TransactionOutputImpl(5000 sat,P2PKHScriptPubKeyImpl(1976a9147e3370045ebdffc889d9fdd4d2fa2b3474cdb5db88ac))
TransactionOutputImpl(4774 sat,P2PKHScriptPubKeyImpl(1976a91431912cb77715f1f709d730a2183bd1188fb7f8b988ac))
```

The following records are written in the Logger :
```
c.bitcoins.core.util.BitcoinSLogger$ - dummySignTx BaseTransactionImpl(Int32Impl(2),List(TransactionInputImpl(TransactionOutPointImpl(DoubleSha256DigestImpl(16773b048950aed7a2ff8ad5e6c005efd034749d5b18ea2026a5bdf5f4c02c70),UInt32Impl(0)),P2PKHScriptSignature(6b4800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000021022473cfb0d50832a721a2995ca402ebc27a1d2ae2cd80a9adb0337b24a2a1d6b6),UInt32Impl(0))),List(TransactionOutputImpl(5000 sat,P2PKHScriptPubKeyImpl(1976a9147e3370045ebdffc889d9fdd4d2fa2b3474cdb5db88ac)), TransactionOutputImpl(0 sat,P2PKHScriptPubKeyImpl(1976a91431912cb77715f1f709d730a2183bd1188fb7f8b988ac))),UInt32Impl(0))

c.bitcoins.core.util.BitcoinSLogger$ - fee 226 sat

c.bitcoins.core.util.BitcoinSLogger$ - newChangeOutput TransactionOutputImpl(4774 sat,P2PKHScriptPubKeyImpl(1976a91431912cb77715f1f709d730a2183bd1188fb7f8b988ac))
```

###  Creation of an invalid transaction

In following we test whether it is possible to create a transaction with an Output greater than an Input value. With `bitcoin-s-core/doc/src/test/scala/TxBuilderExample.scala` a transaction having an Input of 10000 Satoshis and spendig 15000 Satoshis will be created.

The following records are written in the Logger:

```
c.bitcoins.core.util.BitcoinSLogger$ - dummySignTx BaseTransactionImpl(Int32Impl(2),List(TransactionInputImpl(TransactionOutPointImpl(DoubleSha256DigestImpl(555df7a4f1eb1ad513d670771ead7223d5f0a2722ec58797a9b0532bdf460e8b),UInt32Impl(0)),P2PKHScriptSignature(6b480000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002102ca45002b8de75e8c3d88cf43358f3e932db012c914c13826b62efadd00f400a2),UInt32Impl(0))),List(TransactionOutputImpl(15000 sat,P2PKHScriptPubKeyImpl(1976a914e758670bfb3fe0463fe71dd1cc2bd41f0e16206988ac)), TransactionOutputImpl(0 sat,P2PKHScriptPubKeyImpl(1976a9148610a845886b1fe335aaa28153b90ce07e0b9ba188ac))),UInt32Impl(0))
[success] Total time: 10 s, completed Mar 26, 2019 1:23:32 PM

c.bitcoins.core.util.BitcoinSLogger$ - fee 226 sat

c.bitcoins.core.util.BitcoinSLogger$ - newChangeOutput TransactionOutputImpl(-5226 sat,P2PKHScriptPubKeyImpl(1976a9148610a845886b1fe335aaa28153b90ce07e0b9ba188ac))

c.bitcoins.core.util.BitcoinSLogger$ - removing change output as value is below the dustThreshold

```

Because of the missing error handling in the class `TxBuilderExample.scala` there is no output in the sbt console:
```bash
val signedTxF: Future[Transaction] = txBuilder.flatMap(_.sign)
signedTxF.map { tx =>
      println("\nInputs:")
      tx.inputs.foreach(println)

      println("\nOutputs:")
      tx.outputs.foreach(println)

      println(s"\nFully signed tx in hex:")

      println(s"${tx.hex}")
}
```

The previous code of the class `TxBuilderExample.scala` is adjusted to handle errors and print out a message of an error:

```bash
import scala.util.{Success, Failure}
...

val signedTxF: Future[Transaction] = txBuilder.flatMap(_.sign)
signedTxF onComplete {
      case Success(tx) => {
        println("\nInputs:")
        tx.inputs.foreach(println)

        println("\nOutputs:")
        tx.outputs.foreach(println)

      }
      case Failure(t) => println("------------------------- Err: " +
        t.getMessage)
    }
```

The error after running the class in sbt console:

```
This transaction creates spends more money than it was funded by the given utxos
```

Thus, Bitcoin-s prevents creation of a transaction that spends more than it may. 

The class `bitcoin-s-core/core/src/main/scala/org/bitcoins/core/wallet/builder/TxBuilder.scala` contains the check of amounts of Inputs and Outputs by signing a transaction:

```bash
def sanityAmountChecks(
      txBuilder: TxBuilder,
      signedTx: Transaction): Try[Unit] = {
    val spentAmount: CurrencyUnit =
      signedTx.outputs.map(_.value).fold(CurrencyUnits.zero)(_ + _)
    val creditingAmount = txBuilder.creditingAmount
    val actualFee = creditingAmount - spentAmount
    val estimatedFee = txBuilder.feeRate * signedTx
    if (spentAmount > creditingAmount) {
      TxBuilderError.MintsMoney
    } else if (actualFee > txBuilder.largestFee) {
      TxBuilderError.HighFee
    } else if (signedTx.outputs
                 .filterNot(_.scriptPubKey.asm.contains(OP_RETURN))
                 .map(_.value)
                 .exists(_ < Policy.dustThreshold)) {
      TxBuilderError.OutputBelowDustThreshold
    } else {
      val feeResult =
        isValidFeeRange(estimatedFee, actualFee, txBuilder.feeRate)
      feeResult
    }
  }
```


### Verification of a transaction

The next step is to test a validation of an existed transaction. Created transactions are verified in the class `bitcoin-s-core/core/src/main/scala/org/bitcoins/core/script/interpreter/ScriptInterpreter.scala`:

```bash
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
      case true =>
        transaction.inputs.head.scriptSignature.asmBytes.size >= 2 &&
          transaction.inputs.head.scriptSignature.asmBytes.size <= 100
      case false =>
        //since this is not a coinbase tx we cannot have any empty previous outs inside of inputs
        !transaction.inputs.exists(_.previousOutput == EmptyTransactionOutPoint)
    }
    inputOutputsNotZero && txNotLargerThanBlock && outputsSpendValidAmountsOfMoney && noDuplicateInputs &&
    allOutputsValidMoneyRange && noDuplicateInputs && isValidScriptSigForCoinbaseTx
  }

  /** Determines if the given currency unit is within the valid range for the system */
  def validMoneyRange(currencyUnit: CurrencyUnit): Boolean = {
    currencyUnit >= CurrencyUnits.zero && currencyUnit <= Consensus.maxMoney
  }
```
The idea of our verification is to created an invalid transaction with an Output value more than an Input and test it with the method `checkTransaction`.

The previous class `bitcoin-s-core/doc/src/test/scala/TxBuilderExample.scala` is used to create an invalid transaction. To avoid the error mentioned above the method `sanityAmountChecks` in the class `bitcoin-s-core/core/src/main/scala/org/bitcoins/core/wallet/builder/TxBuilder.scala` is modified so that the amounts of Inputs and Outputs will not be checked:

```bash
def sanityAmountChecks(
      txBuilder: TxBuilder,
      signedTx: Transaction): Try[Unit] = {
    return Success(())
}
```

Furthermore the verification of the created transaction is added to the console output in the class `TxBuilderExample.scala`. The final code of the console output is:

```bash
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
```


After run of the class `TxBuilderExample.scala` the invalid Transaction is created and marked as valid:
```
Inputs:
TransactionInputImpl(TransactionOutPointImpl(DoubleSha256DigestImpl(414f64c1cc719ae34bcebcb2009a79f3a4dc6478412d5eedafdb45bce76a7390),UInt32Impl(0)),P2PKHScriptSignature(6a473044022036b37e308f6445327aa6af7aff82ce9d638cd0a445e8fc3b1e23e5169813bf2e0220612cfebf92887bdf1289c841f9bb69ee59fe42c9357afe6667f7e57ffce54762012102a28f3a14d162e1437fbdc089d5555e6535d7b27a3b47a4555b3fc2c45d19c34b),UInt32Impl(0))

Outputs:
TransactionOutputImpl(15000 sat,P2PKHScriptPubKeyImpl(1976a91463879752a407bfa1441131574210057288bd979a88ac))

Result of transaction check: true
```

Thus, Bitcoin-S avoids creation of invalid transaction but not prevents acceptance of invalid transaction. 


