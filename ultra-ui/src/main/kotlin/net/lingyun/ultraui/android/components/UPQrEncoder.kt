package net.lingyun.ultraui.android.components

/**
 * Pure QR Code matrix generator ported faithfully from uview-plus `u-qrcode/qrcode.js`
 * (QRCodeAlg). Given the UTF-8 bytes of [text] and an error-correction level [errorCorrectLevel]
 * (0=L, 1=M, 2=Q, 3=H), [upQrcodeMatrix] returns the boolean module matrix (`true` = dark).
 *
 * The algorithm, RS block table, mask patterns and Galois-field math mirror the upstream file
 * exactly so that generated matrices are byte-for-byte identical to uview-plus on H5/App.
 */

internal object UPQrEncoder {
    private const val PAD0 = 0xEC
    private const val PAD1 = 0x11

    // Maps errorCorrectLevel index (L,M,Q,H) to the BCH format bits.
    private val ERROR_CORRECT_LEVEL = intArrayOf(1, 0, 3, 2)

    private const val G15 = (1 shl 10) or (1 shl 8) or (1 shl 5) or (1 shl 4) or (1 shl 2) or (1 shl 1) or (1 shl 0)
    private const val G18 = (1 shl 12) or (1 shl 11) or (1 shl 10) or (1 shl 9) or (1 shl 8) or (1 shl 5) or (1 shl 2) or (1 shl 0)
    private const val G15_MASK = (1 shl 14) or (1 shl 12) or (1 shl 10) or (1 shl 4) or (1 shl 1)

    private val PATTERN_POSITION_TABLE: Array<IntArray> = arrayOf(
        intArrayOf(),
        intArrayOf(6, 18), intArrayOf(6, 22), intArrayOf(6, 26), intArrayOf(6, 30), intArrayOf(6, 34),
        intArrayOf(6, 22, 38), intArrayOf(6, 24, 42), intArrayOf(6, 26, 46), intArrayOf(6, 28, 50),
        intArrayOf(6, 30, 54), intArrayOf(6, 32, 58), intArrayOf(6, 34, 62),
        intArrayOf(6, 26, 46, 66), intArrayOf(6, 26, 48, 70), intArrayOf(6, 26, 50, 74),
        intArrayOf(6, 30, 54, 78), intArrayOf(6, 30, 56, 82), intArrayOf(6, 30, 58, 86),
        intArrayOf(6, 34, 62, 90),
        intArrayOf(6, 28, 50, 72, 94), intArrayOf(6, 26, 50, 74, 98), intArrayOf(6, 30, 54, 78, 102),
        intArrayOf(6, 28, 54, 80, 106), intArrayOf(6, 32, 58, 84, 110), intArrayOf(6, 30, 58, 86, 114),
        intArrayOf(6, 34, 62, 90, 118),
        intArrayOf(6, 26, 50, 74, 98, 122), intArrayOf(6, 30, 54, 78, 102, 126), intArrayOf(6, 26, 52, 78, 104, 130),
        intArrayOf(6, 30, 56, 82, 108, 134), intArrayOf(6, 34, 60, 86, 112, 138), intArrayOf(6, 30, 58, 86, 114, 142),
        intArrayOf(6, 34, 62, 90, 118, 146),
        intArrayOf(6, 30, 54, 78, 102, 126, 150), intArrayOf(6, 24, 50, 76, 102, 128, 154),
        intArrayOf(6, 28, 54, 80, 106, 132, 158), intArrayOf(6, 32, 58, 84, 110, 136, 162),
        intArrayOf(6, 26, 54, 82, 110, 138, 166), intArrayOf(6, 30, 58, 86, 114, 142, 170),
    )

    private val RS_BLOCK_TABLE: Array<IntArray> = arrayOf(
        intArrayOf(1, 26, 19),
        intArrayOf(1, 26, 16),
        intArrayOf(1, 26, 13),
        intArrayOf(1, 26, 9),
        intArrayOf(1, 44, 34),
        intArrayOf(1, 44, 28),
        intArrayOf(1, 44, 22),
        intArrayOf(1, 44, 16),
        intArrayOf(1, 70, 55),
        intArrayOf(1, 70, 44),
        intArrayOf(2, 35, 17),
        intArrayOf(2, 35, 13),
        intArrayOf(1, 100, 80),
        intArrayOf(2, 50, 32),
        intArrayOf(2, 50, 24),
        intArrayOf(4, 25, 9),
        intArrayOf(1, 134, 108),
        intArrayOf(2, 67, 43),
        intArrayOf(2, 33, 15, 2, 34, 16),
        intArrayOf(2, 33, 11, 2, 34, 12),
        intArrayOf(2, 86, 68),
        intArrayOf(4, 43, 27),
        intArrayOf(4, 43, 19),
        intArrayOf(4, 43, 15),
        intArrayOf(2, 98, 78),
        intArrayOf(4, 49, 31),
        intArrayOf(2, 32, 14, 4, 33, 15),
        intArrayOf(4, 39, 13, 1, 40, 14),
        intArrayOf(2, 121, 97),
        intArrayOf(2, 60, 38, 2, 61, 39),
        intArrayOf(4, 40, 18, 2, 41, 19),
        intArrayOf(4, 40, 14, 2, 41, 15),
        intArrayOf(2, 146, 116),
        intArrayOf(3, 58, 36, 2, 59, 37),
        intArrayOf(4, 36, 16, 4, 37, 17),
        intArrayOf(4, 36, 12, 4, 37, 13),
        intArrayOf(2, 86, 68, 2, 87, 69),
        intArrayOf(4, 69, 43, 1, 70, 44),
        intArrayOf(6, 43, 19, 2, 44, 20),
        intArrayOf(6, 43, 15, 2, 44, 16),
        intArrayOf(4, 101, 81),
        intArrayOf(1, 80, 50, 4, 81, 51),
        intArrayOf(4, 50, 22, 4, 51, 23),
        intArrayOf(3, 36, 12, 8, 37, 13),
        intArrayOf(2, 116, 92, 2, 117, 93),
        intArrayOf(6, 58, 36, 2, 59, 37),
        intArrayOf(4, 46, 20, 6, 47, 21),
        intArrayOf(7, 42, 14, 4, 43, 15),
        intArrayOf(4, 133, 107),
        intArrayOf(8, 59, 37, 1, 60, 38),
        intArrayOf(8, 44, 20, 4, 45, 21),
        intArrayOf(12, 33, 11, 4, 34, 12),
        intArrayOf(3, 145, 115, 1, 146, 116),
        intArrayOf(4, 64, 40, 5, 65, 41),
        intArrayOf(11, 36, 16, 5, 37, 17),
        intArrayOf(11, 36, 12, 5, 37, 13),
        intArrayOf(5, 109, 87, 1, 110, 88),
        intArrayOf(5, 65, 41, 5, 66, 42),
        intArrayOf(5, 54, 24, 7, 55, 25),
        intArrayOf(11, 36, 12),
        intArrayOf(5, 122, 98, 1, 123, 99),
        intArrayOf(7, 73, 45, 3, 74, 46),
        intArrayOf(15, 43, 19, 2, 44, 20),
        intArrayOf(3, 45, 15, 13, 46, 16),
        intArrayOf(1, 135, 107, 5, 136, 108),
        intArrayOf(10, 74, 46, 1, 75, 47),
        intArrayOf(1, 50, 22, 15, 51, 23),
        intArrayOf(2, 42, 14, 17, 43, 15),
        intArrayOf(5, 150, 120, 1, 151, 121),
        intArrayOf(9, 69, 43, 4, 70, 44),
        intArrayOf(17, 50, 22, 1, 51, 23),
        intArrayOf(2, 42, 14, 19, 43, 15),
        intArrayOf(3, 141, 113, 4, 142, 114),
        intArrayOf(3, 70, 44, 11, 71, 45),
        intArrayOf(17, 47, 21, 4, 48, 22),
        intArrayOf(9, 39, 13, 16, 40, 14),
        intArrayOf(3, 135, 107, 5, 136, 108),
        intArrayOf(3, 67, 41, 13, 68, 42),
        intArrayOf(15, 54, 24, 5, 55, 25),
        intArrayOf(15, 43, 15, 10, 44, 16),
        intArrayOf(4, 144, 116, 4, 145, 117),
        intArrayOf(17, 68, 42),
        intArrayOf(17, 50, 22, 6, 51, 23),
        intArrayOf(19, 46, 16, 6, 47, 17),
        intArrayOf(2, 139, 111, 7, 140, 112),
        intArrayOf(17, 74, 46),
        intArrayOf(7, 54, 24, 16, 55, 25),
        intArrayOf(34, 37, 13),
        intArrayOf(4, 151, 121, 5, 152, 122),
        intArrayOf(4, 75, 47, 14, 76, 48),
        intArrayOf(11, 54, 24, 14, 55, 25),
        intArrayOf(16, 45, 15, 14, 46, 16),
        intArrayOf(6, 147, 117, 4, 148, 118),
        intArrayOf(6, 73, 45, 14, 74, 46),
        intArrayOf(11, 54, 24, 16, 55, 25),
        intArrayOf(30, 46, 16, 2, 47, 17),
        intArrayOf(8, 132, 106, 4, 133, 107),
        intArrayOf(8, 75, 47, 13, 76, 48),
        intArrayOf(7, 54, 24, 22, 55, 25),
        intArrayOf(22, 45, 15, 13, 46, 16),
        intArrayOf(10, 142, 114, 2, 143, 115),
        intArrayOf(19, 74, 46, 4, 75, 47),
        intArrayOf(28, 50, 22, 6, 51, 23),
        intArrayOf(33, 46, 16, 4, 47, 17),
        intArrayOf(8, 152, 122, 4, 153, 123),
        intArrayOf(22, 73, 45, 3, 74, 46),
        intArrayOf(8, 53, 23, 26, 54, 24),
        intArrayOf(12, 45, 15, 28, 46, 16),
        intArrayOf(3, 147, 117, 10, 148, 118),
        intArrayOf(3, 73, 45, 23, 74, 46),
        intArrayOf(4, 54, 24, 31, 55, 25),
        intArrayOf(11, 45, 15, 31, 46, 16),
        intArrayOf(7, 146, 116, 7, 147, 117),
        intArrayOf(21, 73, 45, 7, 74, 46),
        intArrayOf(1, 53, 23, 37, 54, 24),
        intArrayOf(19, 45, 15, 26, 46, 16),
        intArrayOf(5, 145, 115, 10, 146, 116),
        intArrayOf(19, 75, 47, 10, 76, 48),
        intArrayOf(15, 54, 24, 25, 55, 25),
        intArrayOf(23, 45, 15, 25, 46, 16),
        intArrayOf(13, 145, 115, 3, 146, 116),
        intArrayOf(2, 74, 46, 29, 75, 47),
        intArrayOf(42, 54, 24, 1, 55, 25),
        intArrayOf(23, 45, 15, 28, 46, 16),
        intArrayOf(17, 145, 115),
        intArrayOf(10, 74, 46, 23, 75, 47),
        intArrayOf(10, 54, 24, 35, 55, 25),
        intArrayOf(19, 45, 15, 35, 46, 16),
        intArrayOf(17, 145, 115, 1, 146, 116),
        intArrayOf(14, 74, 46, 21, 75, 47),
        intArrayOf(29, 54, 24, 19, 55, 25),
        intArrayOf(11, 45, 15, 46, 46, 16),
        intArrayOf(13, 145, 115, 6, 146, 116),
        intArrayOf(14, 74, 46, 23, 75, 47),
        intArrayOf(44, 54, 24, 7, 55, 25),
        intArrayOf(59, 46, 16, 1, 47, 17),
        intArrayOf(12, 151, 121, 7, 152, 122),
        intArrayOf(12, 75, 47, 26, 76, 48),
        intArrayOf(39, 54, 24, 14, 55, 25),
        intArrayOf(22, 45, 15, 41, 46, 16),
        intArrayOf(6, 151, 121, 14, 152, 122),
        intArrayOf(6, 75, 47, 34, 76, 48),
        intArrayOf(46, 54, 24, 10, 55, 25),
        intArrayOf(2, 45, 15, 64, 46, 16),
        intArrayOf(17, 152, 122, 4, 153, 123),
        intArrayOf(29, 74, 46, 14, 75, 47),
        intArrayOf(49, 54, 24, 10, 55, 25),
        intArrayOf(24, 45, 15, 46, 46, 16),
        intArrayOf(4, 152, 122, 18, 153, 123),
        intArrayOf(13, 74, 46, 32, 75, 47),
        intArrayOf(48, 54, 24, 14, 55, 25),
        intArrayOf(42, 45, 15, 32, 46, 16),
        intArrayOf(20, 147, 117, 4, 148, 118),
        intArrayOf(40, 75, 47, 7, 76, 48),
        intArrayOf(43, 54, 24, 22, 55, 25),
        intArrayOf(10, 45, 15, 67, 46, 16),
        intArrayOf(19, 148, 118, 6, 149, 119),
        intArrayOf(18, 75, 47, 31, 76, 48),
        intArrayOf(34, 54, 24, 34, 55, 25),
        intArrayOf(20, 45, 15, 61, 46, 16),
    )

    // --- Galois-field math (GF(256)) ---
    private val EXP_TABLE = IntArray(256)
    private val LOG_TABLE = IntArray(256)

    init {
        for (i in 0 until 8) EXP_TABLE[i] = 1 shl i
        for (i in 8 until 256) {
            EXP_TABLE[i] = EXP_TABLE[i - 4] xor EXP_TABLE[i - 5] xor EXP_TABLE[i - 6] xor EXP_TABLE[i - 8]
        }
        for (i in 0 until 255) LOG_TABLE[EXP_TABLE[i]] = i
    }

    private fun glog(n: Int): Int {
        require(n >= 1) { "glog($n)" }
        return LOG_TABLE[n]
    }

    private fun gexp(nIn: Int): Int {
        var n = nIn
        while (n < 0) n += 255
        while (n >= 256) n -= 255
        return EXP_TABLE[n]
    }

    private fun getBCHDigit(dataIn: Int): Int {
        var data = dataIn
        var digit = 0
        while (data != 0) {
            digit++
            data = data ushr 1
        }
        return digit
    }

    private fun getBCHTypeInfo(data: Int): Int {
        var d = data shl 10
        while (getBCHDigit(d) - getBCHDigit(G15) >= 0) {
            d = d xor (G15 shl (getBCHDigit(d) - getBCHDigit(G15)))
        }
        return ((data shl 10) or d) xor G15_MASK
    }

    private fun getBCHTypeNumber(data: Int): Int {
        var d = data shl 12
        while (getBCHDigit(d) - getBCHDigit(G18) >= 0) {
            d = d xor (G18 shl (getBCHDigit(d) - getBCHDigit(G18)))
        }
        return (data shl 12) or d
    }

    private fun getMask(maskPattern: Int, i: Int, j: Int): Boolean = when (maskPattern) {
        0 -> (i + j) % 2 == 0
        1 -> i % 2 == 0
        2 -> j % 3 == 0
        3 -> (i + j) % 3 == 0
        4 -> ((i / 2) + (j / 3)) % 2 == 0
        5 -> (i * j) % 2 + (i * j) % 3 == 0
        6 -> ((i * j) % 2 + (i * j) % 3) % 2 == 0
        7 -> ((i * j) % 3 + (i + j) % 2) % 2 == 0
        else -> throw IllegalArgumentException("bad maskPattern:$maskPattern")
    }

    // --- Polynomial over GF(256) ---
    private class QRPolynomial(numIn: IntArray, shift: Int) {
        val num: IntArray

        init {
            var offset = 0
            while (offset < numIn.size && numIn[offset] == 0) offset++
            num = IntArray(numIn.size - offset + shift)
            for (i in 0 until numIn.size - offset) num[i] = numIn[i + offset]
        }

        fun get(index: Int): Int = num[index]
        fun length(): Int = num.size

        fun multiply(e: QRPolynomial): QRPolynomial {
            val result = IntArray(length() + e.length() - 1)
            for (i in 0 until length()) {
                for (j in 0 until e.length()) {
                    result[i + j] = result[i + j] xor gexp(glog(get(i)) + glog(e.get(j)))
                }
            }
            return QRPolynomial(result, 0)
        }

        fun mod(e: QRPolynomial): QRPolynomial {
            if (length() - e.length() < 0) return this
            val work = ArrayList<Int>(length())
            for (i in 0 until length()) work.add(get(i))
            while (work.size >= e.length()) {
                val ratio = glog(work[0]) - glog(e.get(0))
                for (i in 0 until e.length()) {
                    work[i] = work[i] xor gexp(glog(e.get(i)) + ratio)
                }
                while (work.isNotEmpty() && work[0] == 0) work.removeAt(0)
            }
            return QRPolynomial(work.toIntArray(), 0)
        }
    }

    private fun getErrorCorrectPolynomial(errorCorrectLength: Int): QRPolynomial {
        var a = QRPolynomial(intArrayOf(1), 0)
        for (i in 0 until errorCorrectLength) {
            a = a.multiply(QRPolynomial(intArrayOf(1, gexp(i)), 0))
        }
        return a
    }

    // --- Bit buffer ---
    private class QRBitBuffer {
        val buffer = ArrayList<Int>()
        var length = 0

        fun put(num: Int, len: Int) {
            for (i in 0 until len) putBit(((num ushr (len - i - 1)) and 1) == 1)
        }

        fun putBit(bit: Boolean) {
            val bufIndex = length / 8
            if (buffer.size <= bufIndex) buffer.add(0)
            if (bit) buffer[bufIndex] = buffer[bufIndex] or (0x80 ushr (length % 8))
            length++
        }
    }

    private fun getUTF8Bytes(text: String): IntArray {
        val out = ArrayList<Int>()
        for (ch in text) {
            val code = ch.code
            when {
                code < 128 -> out.add(code)
                code < 2048 -> {
                    out.add(192 + (code shr 6))
                    out.add(128 + (code and 63))
                }
                else -> {
                    out.add(224 + (code shr 12))
                    out.add(128 + ((code shr 6) and 63))
                    out.add(128 + (code and 63))
                }
            }
        }
        return out.toIntArray()
    }

    private class Alg(val text: String, val errorCorrectLevel: Int) {
        var typeNumber = -1
        var moduleCount = 0
        lateinit var modules: Array<Array<Boolean?>>
        var totalDataCount = -1
        lateinit var rsBlock: IntArray
        val utf8bytes = getUTF8Bytes(text)
        lateinit var dataCache: IntArray

        fun make() {
            getRightType()
            dataCache = createData()
            createQrcode()
        }

        private fun getRightType() {
            for (type in 1 until 41) {
                val rs = RS_BLOCK_TABLE[(type - 1) * 4 + errorCorrectLevel]
                val length = rs.size / 3
                var total = 0
                for (i in 0 until length) {
                    val count = rs[i * 3 + 0]
                    val dataCount = rs[i * 3 + 2]
                    total += dataCount * count
                }
                val lengthBytes = if (type > 9) 2 else 1
                if (utf8bytes.size + lengthBytes < total || type == 40) {
                    typeNumber = type
                    rsBlock = rs
                    totalDataCount = total
                    break
                }
            }
        }

        private fun createData(): IntArray {
            val buffer = QRBitBuffer()
            val lengthBits = if (typeNumber > 9) 16 else 8
            buffer.put(4, 4)
            buffer.put(utf8bytes.size, lengthBits)
            for (b in utf8bytes) buffer.put(b, 8)
            if (buffer.length + 4 <= totalDataCount * 8) buffer.put(0, 4)
            while (buffer.length % 8 != 0) buffer.putBit(false)
            while (true) {
                if (buffer.length >= totalDataCount * 8) break
                buffer.put(PAD0, 8)
                if (buffer.length >= totalDataCount * 8) break
                buffer.put(PAD1, 8)
            }
            return createBytes(buffer)
        }

        private fun createBytes(buffer: QRBitBuffer): IntArray {
            var offset = 0
            var maxDcCount = 0
            var maxEcCount = 0
            val length = rsBlock.size / 3
            val rsBlocks = ArrayList<IntArray>()
            for (i in 0 until length) {
                val count = rsBlock[i * 3 + 0]
                val totalCount = rsBlock[i * 3 + 1]
                val dataCount = rsBlock[i * 3 + 2]
                for (j in 0 until count) rsBlocks.add(intArrayOf(dataCount, totalCount))
            }
            val dcdata = arrayOfNulls<IntArray>(rsBlocks.size)
            val ecdata = arrayOfNulls<IntArray>(rsBlocks.size)
            for (r in rsBlocks.indices) {
                val dcCount = rsBlocks[r][0]
                val ecCount = rsBlocks[r][1] - dcCount
                maxDcCount = maxOf(maxDcCount, dcCount)
                maxEcCount = maxOf(maxEcCount, ecCount)
                val dc = IntArray(dcCount)
                for (i in 0 until dcCount) dc[i] = 0xff and buffer.buffer[i + offset]
                dcdata[r] = dc
                offset += dcCount
                val rsPoly = getErrorCorrectPolynomial(ecCount)
                val rawPoly = QRPolynomial(dc, rsPoly.length() - 1)
                val modPoly = rawPoly.mod(rsPoly)
                val ec = IntArray(rsPoly.length() - 1)
                for (i in ec.indices) {
                    val modIndex = i + modPoly.length() - ec.size
                    ec[i] = if (modIndex >= 0) modPoly.get(modIndex) else 0
                }
                ecdata[r] = ec
            }
            val data = ArrayList<Int>()
            for (i in 0 until maxDcCount) {
                for (r in rsBlocks.indices) {
                    if (i < dcdata[r]!!.size) data.add(dcdata[r]!![i])
                }
            }
            for (i in 0 until maxEcCount) {
                for (r in rsBlocks.indices) {
                    if (i < ecdata[r]!!.size) data.add(ecdata[r]!![i])
                }
            }
            return data.toIntArray()
        }

        private fun createQrcode() {
            var minLostPoint = 0.0
            var pattern = 0
            var bestModules: Array<Array<Boolean?>>? = null
            for (i in 0 until 8) {
                makeImpl(i)
                val lostPoint = getLostPoint()
                if (i == 0 || minLostPoint > lostPoint) {
                    minLostPoint = lostPoint
                    pattern = i
                    bestModules = modules
                }
            }
            modules = bestModules!!
            setupTypeInfo(false, pattern)
            if (typeNumber >= 7) setupTypeNumber(false)
        }

        private fun makeImpl(maskPattern: Int) {
            moduleCount = typeNumber * 4 + 17
            modules = Array(moduleCount) { arrayOfNulls<Boolean>(moduleCount) }
            setupPositionProbePattern(0, 0)
            setupPositionProbePattern(moduleCount - 7, 0)
            setupPositionProbePattern(0, moduleCount - 7)
            setupPositionAdjustPattern()
            setupTimingPattern()
            setupTypeInfo(true, maskPattern)
            if (typeNumber >= 7) setupTypeNumber(true)
            mapData(dataCache, maskPattern)
        }

        private fun setupPositionProbePattern(row: Int, col: Int) {
            for (r in -1..7) {
                if (row + r <= -1 || moduleCount <= row + r) continue
                for (c in -1..7) {
                    if (col + c <= -1 || moduleCount <= col + c) continue
                    modules[row + r][col + c] =
                        (r in 0..6 && (c == 0 || c == 6)) ||
                            (c in 0..6 && (r == 0 || r == 6)) ||
                            (r in 2..4 && c in 2..4)
                }
            }
        }

        private fun setupTimingPattern() {
            for (r in 8 until moduleCount - 8) {
                if (modules[r][6] == null) modules[r][6] = (r % 2 == 0)
                if (modules[6][r] == null) modules[6][r] = (r % 2 == 0)
            }
        }

        private fun setupPositionAdjustPattern() {
            val pos = PATTERN_POSITION_TABLE[typeNumber - 1]
            for (i in pos.indices) {
                for (j in pos.indices) {
                    val row = pos[i]
                    val col = pos[j]
                    if (modules[row][col] != null) continue
                    for (r in -2..2) {
                        for (c in -2..2) {
                            modules[row + r][col + c] =
                                r == -2 || r == 2 || c == -2 || c == 2 || (r == 0 && c == 0)
                        }
                    }
                }
            }
        }

        private fun setupTypeNumber(test: Boolean) {
            val bits = getBCHTypeNumber(typeNumber)
            for (i in 0 until 18) {
                val mod = !test && ((bits shr i) and 1) == 1
                modules[i / 3][i % 3 + moduleCount - 8 - 3] = mod
                modules[i % 3 + moduleCount - 8 - 3][i / 3] = mod
            }
        }

        private fun setupTypeInfo(test: Boolean, maskPattern: Int) {
            val data = (ERROR_CORRECT_LEVEL[errorCorrectLevel] shl 3) or maskPattern
            val bits = getBCHTypeInfo(data)
            for (i in 0 until 15) {
                val mod = !test && ((bits shr i) and 1) == 1
                when {
                    i < 6 -> modules[i][8] = mod
                    i < 8 -> modules[i + 1][8] = mod
                    else -> modules[moduleCount - 15 + i][8] = mod
                }
                val mod2 = !test && ((bits shr i) and 1) == 1
                when {
                    i < 8 -> modules[8][moduleCount - i - 1] = mod2
                    i < 9 -> modules[8][15 - i - 1 + 1] = mod2
                    else -> modules[8][15 - i - 1] = mod2
                }
            }
            modules[moduleCount - 8][8] = !test
        }

        private fun mapData(data: IntArray, maskPattern: Int) {
            var inc = -1
            var row = moduleCount - 1
            var bitIndex = 7
            var byteIndex = 0
            var col = moduleCount - 1
            while (col > 0) {
                if (col == 6) col--
                while (true) {
                    for (c in 0 until 2) {
                        if (modules[row][col - c] == null) {
                            var dark = false
                            if (byteIndex < data.size) {
                                dark = ((data[byteIndex] ushr bitIndex) and 1) == 1
                            }
                            if (getMask(maskPattern, row, col - c)) dark = !dark
                            modules[row][col - c] = dark
                            bitIndex--
                            if (bitIndex == -1) {
                                byteIndex++
                                bitIndex = 7
                            }
                        }
                    }
                    row += inc
                    if (row < 0 || moduleCount <= row) {
                        row -= inc
                        inc = -inc
                        break
                    }
                }
                col -= 2
            }
        }

        private fun m(row: Int, col: Int): Boolean = modules[row][col] == true

        private fun getLostPoint(): Double {
            var lostPoint = 0.0
            var darkCount = 0
            for (row in 0 until moduleCount) {
                var sameCount = 0
                var head = modules[row][0]
                for (col in 0 until moduleCount) {
                    val current = modules[row][col]
                    if (col < moduleCount - 6) {
                        if (m(row, col) && !m(row, col + 1) && m(row, col + 2) && m(row, col + 3) &&
                            m(row, col + 4) && !m(row, col + 5) && m(row, col + 6)
                        ) {
                            if (col < moduleCount - 10) {
                                if (m(row, col + 7) && m(row, col + 8) && m(row, col + 9) && m(row, col + 10)) {
                                    lostPoint += 40
                                }
                            } else if (col > 3) {
                                if (m(row, col - 1) && m(row, col - 2) && m(row, col - 3) && m(row, col - 4)) {
                                    lostPoint += 40
                                }
                            }
                        }
                    }
                    if (row < moduleCount - 1 && col < moduleCount - 1) {
                        var count = 0
                        if (m(row, col)) count++
                        if (m(row + 1, col)) count++
                        if (m(row, col + 1)) count++
                        if (m(row + 1, col + 1)) count++
                        if (count == 0 || count == 4) lostPoint += 3
                    }
                    if ((head == true) != (current == true)) {
                        sameCount++
                    } else {
                        head = current
                        if (sameCount >= 5) lostPoint += (3 + sameCount - 5)
                        sameCount = 1
                    }
                    if (m(row, col)) darkCount++
                }
            }
            for (col in 0 until moduleCount) {
                var sameCount = 0
                var head = modules[0][col]
                for (row in 0 until moduleCount) {
                    val current = modules[row][col]
                    if (row < moduleCount - 6) {
                        if (m(row, col) && !m(row + 1, col) && m(row + 2, col) && m(row + 3, col) &&
                            m(row + 4, col) && !m(row + 5, col) && m(row + 6, col)
                        ) {
                            if (row < moduleCount - 10) {
                                if (m(row + 7, col) && m(row + 8, col) && m(row + 9, col) && m(row + 10, col)) {
                                    lostPoint += 40
                                }
                            } else if (row > 3) {
                                if (m(row - 1, col) && m(row - 2, col) && m(row - 3, col) && m(row - 4, col)) {
                                    lostPoint += 40
                                }
                            }
                        }
                    }
                    if ((head == true) != (current == true)) {
                        sameCount++
                    } else {
                        head = current
                        if (sameCount >= 5) lostPoint += (3 + sameCount - 5)
                        sameCount = 1
                    }
                }
            }
            val ratio = Math.abs(100.0 * darkCount / moduleCount / moduleCount - 50.0) / 5.0
            lostPoint += ratio * 10.0
            return lostPoint
        }
    }

    /**
     * Generates the QR module matrix for [text] at error-correction level [errorCorrectLevel]
     * (0=L, 1=M, 2=Q, 3=H). Returns a \`moduleCount x moduleCount\` grid where \`true\` is a dark module.
     */
    fun encode(text: String, errorCorrectLevel: Int): Array<BooleanArray> {
        val alg = Alg(text, errorCorrectLevel.coerceIn(0, 3))
        alg.make()
        val n = alg.moduleCount
        return Array(n) { r -> BooleanArray(n) { c -> alg.modules[r][c] == true } }
    }
}

/**
 * Convenience wrapper: returns the QR matrix (\`true\` = dark) for [text] at [errorCorrectLevel].
 */
internal fun upQrcodeMatrix(text: String, errorCorrectLevel: Int = 3): Array<BooleanArray> =
    UPQrEncoder.encode(text, errorCorrectLevel)
