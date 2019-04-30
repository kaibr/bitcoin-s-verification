package example

import scala.util.Try
import org.scalacheck.{Gen, Prop, Properties}

/**
  * Created by chris on 6/23/16.
  */
class CurrencyUnitSpec extends Properties("CurrencyUnitSpec") {
  // Modified
  property("additive identity") = Prop.forAll(Satoshis.zero) {
    satoshis =>
      satoshis + CurrencyUnits.zero == satoshis
  }
}
