// From https://stackoverflow.com/a/53018129
fun getResourceAsText(path: String): String {
    return object {}.javaClass.getResource(path)?.readText() ?: throw Exception("Unable to read file")
}

fun getResourceAsInts(path: String): List<Int> {
    val contents = getResourceAsText(path);
    return contents.trim().lines().map { it.toInt() }
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
    return contents.trim().lineSequence().mapNotNull { re.find(it)?.destructured }
        .filter { (lo, hi, ch, pw) -> valid(lo.toInt(), hi.toInt(), ch, pw) }.count()
}

fun problem3a(right: Int, down: Int): Int {
    val map = getResourceAsText("3.txt").trim().lines()
    val width = map[0].length
    val tree = '#'
    var nTrees = 0
    var row = 0
    var col = 0
    while (row < map.size) {
        nTrees += if (map[row][col % width] == tree) 1 else 0
        row += down
        col += right
    }
    return nTrees
}

fun problem3b(): Long {
    return listOf(
        problem3a(1, 1),
        problem3a(3, 1),
        problem3a(5, 1),
        problem3a(7, 1),
        problem3a(1, 2)
    ).fold(1L) { acc, i -> acc * i }
}

fun problem4a(): Int {
    val contents = getResourceAsText("4.txt").trim().splitToSequence("\n\n")
    val listOfKeys = contents.map {
        it
            .split("\\s".toRegex())
            .map { kv -> kv.splitToSequence(':').first() }
            .toSet()
    }
    return listOfKeys.count { it.containsAll(listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")) }
}

fun <T> listToPair(l: List<T>): Pair<T, T> {
    return Pair(l[0], l[1])
}

fun problem4b(): Int {
    val requiredFields = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
    val okEyeColors = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")

    val contents = getResourceAsText("4.txt").trim().splitToSequence("\n\n")
    val passports = contents.map {
        it
            .split("\\s".toRegex())
            .associate { kv -> listToPair(kv.split(":")) }
    }
    val validPassports = passports.filter { requiredFields.all { k -> it.containsKey(k) } }

    fun isValid(m: Map<String, String>): Boolean {
        val hgt = m["hgt"]!!
        val hgtOk = (hgt.endsWith("cm") && hgt.dropLast(2).toInt() in 150..193) ||
                (hgt.endsWith("in") && hgt.dropLast(2).toInt() in 59..76)
        return hgtOk &&
                (m["byr"]?.toInt() in 1920..2002) &&
                (m["iyr"]?.toInt() in 2010..2020) &&
                (m["eyr"]?.toInt() in 2020..2030) &&
                (m["hcl"]!!.contains("^#[a-f0-9]{6}$".toRegex())) &&
                (okEyeColors.contains(m["ecl"]!!)) &&
                (m["pid"]!!.contains("^[0-9]{9}$".toRegex()))
    }
    return validPassports.count(::isValid)

}

fun main(args: Array<String>) {
    println("Problem 1a: ${problem1a()}")
    println("Problem 1b: ${problem1b()}")
    println("Problem 2a: ${problem2a(true)}")
    println("Problem 2b: ${problem2a(!true)}")
    println("Problem 3a: ${problem3a(3, 1)}")
    println("Problem 3b: ${problem3b()}")
    println("Problem 4a: ${problem4a()}")
    println("Problem 4b: ${problem4b()}")
}