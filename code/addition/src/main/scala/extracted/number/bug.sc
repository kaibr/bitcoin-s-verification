
// checkresult does not catch overflows

val andMask = 0xffffffffffffffffL

def checkResult(result: BigInt): BigInt = {
  require((result & andMask) == result,
    "Result was out of bounds, got: " + result)
  result
}

val max = BigInt(9223372036854775807L)
val outofrange = max + 1

checkResult(outofrange)

