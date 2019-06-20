package addition.reduced.number

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
