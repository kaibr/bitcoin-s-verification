package addition.reduced.number

import scala.util.Try

/**
  * @define mulSafe
  * Some classes have restrictions on upper bounds
  * for it's underlying value. This might cause the `*`
  * operator to throw. This method wraps it in a `Try`
  * block.
  */
trait BasicArithmetic[N] {
  def +(n: N): N
}
