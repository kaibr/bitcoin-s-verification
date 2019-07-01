
/*

 https://docs.oracle.com/javase/8/docs/api/java/math/BigInteger.html

 Semantics of bitwise logical operations exactly mimic those
 of Java's bitwise integer operators. The binary operators (and, or, xor)
 implicitly perform sign extension on the shorter of the two operands
 prior to performing the operation.

https://en.wikipedia.org/wiki/Two%27s_complement#Sign_extension

*/

val andmask = 0xffffffffffffffffL

// 64 1-bits, that a -1

val max = BigInt(9223372036854775807L)

// One 0-bit followed by 63 1-bits

max.bitLength

val outofrange = max + 1

// sign extension (prepend 0-bit) followed by addition as usual, yielding
// One 0-bit, followed by one 1-bit, followed by 63 0-bits

outofrange.bitLength


outofrange & andmask

// second operand is shorter, so perform sign extension (prepend 1-bit)
// so now second operand is 65 1-bits
// result of the biwise conjuntion is simply the first operand

