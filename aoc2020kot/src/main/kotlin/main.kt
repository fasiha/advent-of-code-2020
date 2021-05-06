// From https://stackoverflow.com/a/53018129
fun getResourceAsText(path: String): String? {
    return object {}.javaClass.getResource(path)?.readText()
}

fun getResourceAsInts(path: String): List<Int> {
    val contents = getResourceAsText(path);
    if (contents != null) {
        return contents.trim().lines().map { it.toInt() }
    }
    throw Exception("File not found")
}

fun problem1a(): Pair<Int, List<Int>>? {
    val targetSum = 2020
    val expenses = getResourceAsInts("1.txt")
    val seen: MutableSet<Int> = mutableSetOf()
    for (x in expenses) {
        if (seen.contains(targetSum - x)) {
            return Pair((targetSum - x) * x, listOf(targetSum - x, x))
        }
        seen += x
    }
    return null
}

fun problem1b(): Pair<Int, List<Int>>? {
    val targetSum = 2020
    val expenses = getResourceAsInts("1.txt")
    val seen: MutableSet<Int> = mutableSetOf()
    for (x in expenses) {
        for (y in seen) {
            if (seen.contains(targetSum - (x + y))) {
                return Pair(x * y * (targetSum - (x + y)), listOf(x, y, targetSum - (x + y)))
            }
        }
        seen += x
    }
    return null
}

fun problem2a(a: Boolean): Int? {
    val re = "([0-9]+)-([0-9]+) (.): (.+)".toRegex()
    fun isValid(lo: Int, hi: Int, ch: String, pw: String): Boolean {
        val count = ch.toRegex().findAll(pw).count()
        return count in lo..hi
    }

    fun isValidB(i: Int, j: Int, ch: String, pw: String): Boolean {
        return (pw[i - 1] == ch[0]).xor(pw[j - 1] == ch[0])
    }

    val valid = if (a) ::isValid else ::isValidB

    val contents = getResourceAsText("2.txt")
    if (contents != null) {
        return contents.trim().lineSequence().mapNotNull { re.find(it)?.destructured }
            .filter { (lo, hi, ch, pw) -> valid(lo.toInt(), hi.toInt(), ch, pw) }.count()
    }
    return null
}

fun main(args: Array<String>) {
    println("Problem 1a: ${problem1a()}")
    println("Problem 1b: ${problem1b()}")
    println("Problem 2a: ${problem2a(true)}")
    println("Problem 2b: ${problem2a(!true)}")
    println("Hello World!")
}