// From https://stackoverflow.com/a/53018129
fun getResourceAsText(path: String): String {
    return object {}.javaClass.getResource(path)?.readText() ?: throw Exception("Unable to read file")
}

fun getResourceAsBytes(path: String): ByteArray {
    return object {}.javaClass.getResource(path)?.readBytes() ?: throw Exception("Unable to read file")
}

fun getResourceAsInts(path: String): Sequence<Int> {
    return getResourceAsText(path).trim().lineSequence().map { it.toInt() }
}

fun getResourceAsLongs(path: String): Sequence<Long> {
    return getResourceAsText(path).trim().lineSequence().map { it.toLong() }
}

fun problem1a(expenses: Sequence<Long> = getResourceAsLongs("1.txt"), targetSum: Long = 2020): Pair<Long, List<Long>>? {
    val seen: MutableSet<Long> = mutableSetOf()
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

fun problem2a(a: Boolean): Int {
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

fun strToBinary(s: String, zeroChar: Char): Int {
    return s.toCharArray().fold(0) { acc, i -> (acc shl 1) + if (i == zeroChar) 0 else 1 }
    // Does toCharArray *create* a new array? Or is it a view?
    // Because, an alternative: `chars` returns IntSequence, so
    // return s.chars().map{if (it == zeroChar.toInt()) 0 else 1}.reduce{ acc, i -> (acc shl 1) + i }.asInt
}

fun problem5a(): Int {
    val lines = getResourceAsText("5.txt").trim().lineSequence()
    return lines.maxOfOrNull { strToBinary(it.take(7), 'F') * 8 + strToBinary(it.takeLast(3), 'L') }!!
}

fun problem5b(): Int {
    val lines = getResourceAsText("5.txt").trim().lineSequence()
    val sorted = lines.map { strToBinary(it.take(7), 'F') * 8 + strToBinary(it.takeLast(3), 'L') }.sorted()
    for ((curr, next) in sorted.zipWithNext()) {
        if (curr + 1 != next) return curr + 1
    }
    return -1
}

fun problem6a(): Int {
    val groups = getResourceAsText("6.txt").trim().splitToSequence("\n\n")
    return groups.sumOf { it.replace("\n", "").toSet().size }
}

fun problem6b(): Int {
    val groups = getResourceAsText("6.txt").trim().splitToSequence("\n\n")
    return groups.sumOf {
        it.splitToSequence("\n")
            .map { person -> person.toSet() }
            .reduce { acc, i -> acc intersect i }
            .size
    }
}

fun problem7a(): Int {
    fun outerToInner(rule: String): Sequence<Pair<String, String>> {
        val split = rule.split(" bags contain")
        val parent = split[0] // don't need null check?
        return "[0-9]+ ([a-z ]+?) bag".toRegex().findAll(split[1]).map { Pair(parent, it.destructured.component1()) }
    }

    val innerToOuters =
        getResourceAsText("7.txt").trim().lineSequence().flatMap(::outerToInner).groupBy({ it.second }, { it.first })

    val ancestors: MutableSet<String> = mutableSetOf()
    fun recur(inner: String) {
        val outers = innerToOuters[inner].orEmpty()
        ancestors += outers
        outers.forEach(::recur)
    }

    recur("shiny gold")
    return ancestors.size
}

data class BagContent(val num: Int, val color: String)

fun problem7b(): Int {
    fun outerToInners(rule: String): Pair<String, List<BagContent>> {
        val split = rule.split(" bags contain")
        assert(split.size >= 2) { "two sides to the rule expected" }
        val parent = split[0] // don't need null check? Guess not. Either this will throw or the assert above
        return Pair(
            parent,
            "([0-9]+) ([a-z ]+?) bag"
                .toRegex()
                .findAll(split[1])
                .map { BagContent(it.destructured.component1().toInt(), it.destructured.component2()) }
                .toList()
        )
    }

    val outerToInners =
        getResourceAsText("7.txt").trim().lineSequence().associate(::outerToInners)

    fun recur(outer: String): Int {
        val inners = outerToInners[outer].orEmpty()
        return inners.sumOf { it.num + it.num * recur(it.color) }
    }

    return recur("shiny gold")
}

enum class Op { Acc, Jmp, Nop }
data class OpCode(val op: Op, val arg: Int)

fun loadProgram8(): List<OpCode> {
    return getResourceAsText("8.txt").trim().lines().map {
        val (op, arg) = it.split(" ")
        OpCode(
            when (op) {
                "nop" -> Op.Nop
                "acc" -> Op.Acc
                "jmp" -> Op.Jmp
                else -> throw Error("unknown")
            },
            arg.toInt()
        )
    }
}

enum class FinalState { Terminated, InfiniteLoop }

fun problem8a(program: List<OpCode> = loadProgram8()): Pair<FinalState, Int> {
    val linesVisited = mutableSetOf<Int>()
    var programCounter = 0
    var acc = 0
    while (programCounter < program.size) {
        if (linesVisited.contains(programCounter)) return Pair(FinalState.InfiniteLoop, acc)
        else linesVisited.add(programCounter)

        val (op, arg) = program[programCounter]
        when (op) {
            Op.Acc -> {
                acc += arg
                programCounter++
            }
            Op.Jmp -> programCounter += arg
            Op.Nop -> programCounter++
        }
    }
    return Pair(FinalState.Terminated, acc)
}

fun problem8b(): Int {
    val program = loadProgram8()
    for ((idx, op) in program.withIndex()) {
        if (op.op == Op.Nop || op.op == Op.Jmp) {
            val newProgram = program.toMutableList()
            newProgram[idx] = OpCode(if (op.op == Op.Nop) Op.Jmp else Op.Nop, op.arg)
            val (newState, newAcc) = problem8a(newProgram)
            if (newState == FinalState.Terminated) return newAcc
        }
    }
    throw Error("no solution found")
}
// Kotlin Sad 1: no nested destructure
// 2: windowed returns a List of Lists? No Sequence? Is that inefficient?
// Question: is List.asSequence() expensive?
// Definitely annoying: `if(x != null) return x.max()` doesn't work: Kotlin doesn't know x is non-null there. Similarly `if(m.containsKey(idx)) return m[idx]!!`
// subList does bound-checking. I miss python lst[5:1000000] would just work

fun problem9a(numbers: Sequence<Long> = getResourceAsLongs("9.txt")): Long {
    val preamble = 25
    return numbers
        .windowed(1 + preamble)
        .first { problem1a(it.dropLast(1).asSequence(), it.last()) == null }
        .last()
}

fun problem9b(): Long {
    val numbers = getResourceAsLongs("9.txt").toList()
    val target = problem9a(numbers.asSequence()) // is this stupid, going from seq -> list -> seq?
    for (win in 2..numbers.size) {
        val solution = numbers.windowed(win).firstOrNull { it.sum() == target }
        if (solution != null) return solution.maxOrNull()!! + solution.minOrNull()!!
    }
    error("unable to find solution")
}

fun problem10a(): Int {
    return getResourceAsInts("10.txt")
        .sorted()
        .zipWithNext { a, b -> b - a }
        .groupingBy { it }
        .eachCount()
        .values
        .fold(1) { acc, it -> acc * (it + 1) }
}

fun problem10b(): Long {
    val list0 = listOf(0) + getResourceAsInts("10.txt").sorted()
    val list = list0 + (list0.last() + 3)

    val m = mutableMapOf<Int, Long>()
    fun recur(idx: Int = 0): Long {
        if (idx + 1 == list.size) return 1
        if (m.containsKey(idx)) return m[idx]!!
        val cur = list[idx]
        val next = list.drop(idx + 1).takeWhile { it <= cur + 3 }.size // drop & takeWhile since subList checks bounds
        val numDescendants = (idx + 1..idx + next).sumOf(::recur)
        m += idx to numDescendants
        return numDescendants
    }
    return recur()
}

data class DoubleBuffer(
    val get: (Int, Int) -> Byte,
    val set: (Int, Int, Byte) -> Unit,
    val flip: () -> Unit,
    val getBuffer: () -> ByteArray,
    val getLineOfSight: (Int, Int, (Byte) -> Boolean) -> List<Byte>,
    val height: Int,
    val width: Int,
)

fun prepareBytes(aBuffer: ByteArray): DoubleBuffer {
    // FIXME: BOM 0xEF,0xBB,0xBF https://en.wikipedia.org/wiki/Byte_order_mark
    val newline = aBuffer.indexOf('\n'.toByte())
    assert(newline > 0) { "file must contain newlines" }
    val (width, padding) = when (aBuffer[newline - 1]) {
        '\r'.toByte() -> Pair(newline - 1, 2)
        else -> Pair(newline, 1)
    }
    val lastIdx = aBuffer.indexOfLast { !(it == '\n'.toByte() || it == '\r'.toByte()) } + 1
    val height = (lastIdx + padding) / (width + padding)

    val bBuffer = aBuffer.copyOf()

    var readBufferA = true // which buffer, a or b, is the read-ready copy? The other will be written to until flip()ed

    val rowColToIndex = { row: Int, col: Int -> row * (width + padding) + col }
    val get = { row: Int, col: Int -> (if (readBufferA) aBuffer else bBuffer)[rowColToIndex(row, col)] }
    val set = { row: Int, col: Int, new: Byte ->
        (if (readBufferA) bBuffer else aBuffer)[rowColToIndex(row, col)] = new
    }
    val flip = { readBufferA = !readBufferA }
    val getBuffer = { if (readBufferA) aBuffer else bBuffer }
    val getLineOfSight = { row: Int, col: Int, f: (Byte) -> Boolean ->
        val inBounds = { r: Int, c: Int -> r >= 0 && c >= 0 && r < height && c < width }
        val buf = if (readBufferA) aBuffer else bBuffer
        val ret = mutableListOf<Byte>()
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dc == 0 && dr == 0) continue
                var r = row + dr
                var c = col + dc
                while (inBounds(r, c)) {
                    val char = buf[rowColToIndex(r, c)]
                    if (f(char)) {
                        ret += char
                        break
                    }
                    c += dc
                    r += dr
                }
            }
        }
        ret
    }
    return DoubleBuffer(get, set, flip, getBuffer, getLineOfSight, height, width)
}

fun problem11(partA: Boolean): Int {
    val buffer = prepareBytes(getResourceAsBytes("11.txt"))
    val occupiedThreshold = if (partA) 4 else 5
    val predicate = if (partA) ({ true }) else ({ b: Byte -> b != '.'.toByte() })
    while (true) {
        var changed = false
        for (row in 0 until buffer.height) {
            for (col in 0 until buffer.width) {
                val seat = buffer.get(row, col)
                if (seat != '.'.toByte()) {
                    val ring = buffer.getLineOfSight(row, col, predicate)
                    val occupied = ring.count { it == '#'.toByte() }
                    if (seat == 'L'.toByte() && occupied == 0) {
                        buffer.set(row, col, '#'.toByte())
                        changed = true
                    } else if (seat == '#'.toByte() && occupied >= occupiedThreshold) {
                        buffer.set(row, col, 'L'.toByte())
                        changed = true
                    } else {
                        buffer.set(row, col, seat)
                    }
                }
            }
        }
        buffer.flip() // all done reading from one buffer and writing to the other: flip which one is readable
        if (!changed) break
    }
    return buffer.getBuffer().count { it == '#'.toByte() }
}

fun bytesToLinesSequence(bytes: ByteArray): Sequence<ByteArray> {
    return sequence {
        val lineFeed = '\n'.toByte()
        val carriageReturn = '\r'.toByte()

        var newlineSize = 1 // unix
        var slice = bytes.sliceArray(bytes.indices)

        val searchIdx = slice.indexOf(lineFeed)
        if (searchIdx < 0) {
            yield(slice)
        } else {
            if (searchIdx > 0 && slice[searchIdx - 1] == carriageReturn) newlineSize = 2 // Windows
            yield(slice.sliceArray(0..searchIdx - newlineSize))
            slice = slice.sliceArray(searchIdx + 1 until slice.size)

            while (slice.isNotEmpty()) {
                val searchIdx = slice.indexOf(lineFeed)
                if (searchIdx < 0) {
                    yield(slice)
                    break
                }
                yield(slice.sliceArray(0..searchIdx - newlineSize))
                slice = slice.sliceArray(searchIdx + 1 until slice.size)
            }
        }
    }
}

fun prob12(): Int {
    val faceToDelta = mapOf('N' to Pair(0, 1), 'S' to Pair(0, -1), 'E' to Pair(1, 0), 'W' to Pair(-1, 0))
    val faces = listOf('N', 'E', 'S', 'W')

    var x = 0
    var y = 0
    var faceIdx = faces.indexOf('E')
    for (line in bytesToLinesSequence(getResourceAsBytes("12.txt"))) {
        val instruction = line[0].toChar()
        val arg = String(line.sliceArray(1 until line.size), Charsets.US_ASCII).toInt()
        when (instruction) {
            'N' -> y += arg
            'S' -> y -= arg
            'W' -> x -= arg
            'E' -> x += arg
            'L' -> faceIdx = (4 + faceIdx - arg / 90) % 4
            'R' -> faceIdx = (4 + faceIdx + arg / 90) % 4
            'F' -> {
                val (dx, dy) = faceToDelta[faces[faceIdx]]!!
                x += dx * arg
                y += dy * arg
            }
        }
    }
    return kotlin.math.abs(x) + kotlin.math.abs(y)
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
    println("Problem 5a: ${problem5a()}")
    println("Problem 5b: ${problem5b()}")
    println("Problem 6a: ${problem6a()}")
    println("Problem 6b: ${problem6b()}")
    println("Problem 7a: ${problem7a()}")
    println("Problem 7b: ${problem7b()}")
    println("Problem 8a: ${problem8a()}")
    println("Problem 8b: ${problem8b()}")
    println("Problem 9a: ${problem9a()}")
    println("Problem 9b: ${problem9b()}")
    println("Problem 10a: ${problem10a()}")
    println("Problem 10b: ${problem10b()}")
//    println("Problem 11a: ${problem11(true)}")
//    println("Problem 11b: ${problem11(false)}")
    println("Problem 12a: ${prob12()}")
}