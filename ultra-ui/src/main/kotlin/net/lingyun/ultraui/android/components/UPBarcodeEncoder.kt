package net.lingyun.ultraui.android.components

/**
 * Pure 1-D barcode encoder ported faithfully from uview-plus `u-barcode`'s `encodeBarcode` family.
 *
 * [upBarcodeEncode] returns the barcode as a string of '1' (bar) / '0' (space) modules, or throws
 * [IllegalArgumentException] with the same messages upstream throws. Supported formats mirror the
 * upstream `switch`: CODE128 (also `auto`), CODE39, EAN13, EAN8, EAN5/EAN2, UPC/UPCA, UPCE.
 */
internal object UPBarcodeEncoder {
    private const val CODE128_START_CODE_B = 104
    private const val CODE128_STOP = 106
    private const val CODE128_CODE_B_CHARS =
        " !\"#\$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"

    private val CODE128_PATTERNS = arrayOf(
        "11011001100", "11001101100", "11001100110", "10010011000", "10010001100", "10001001100",
        "10011001000", "10011000100", "10001100100", "11001001000", "11001000100", "11000100100",
        "10110011100", "10011011100", "10011001110", "10111001100", "10011101100", "10011100110",
        "11001110010", "11001011100", "11001001110", "11011100100", "11001110100", "11101101110",
        "11101001100", "11100101100", "11100100110", "11101100100", "11100110100", "11100110010",
        "11011011000", "11011000110", "11000110110", "10100011000", "10001011000", "10001000110",
        "10110001000", "10001101000", "10001100010", "11010001000", "11000101000", "11000100010",
        "10110111000", "10110001110", "10001101110", "10111011000", "10111000110", "10001110110",
        "11101110110", "11010001110", "11000101110", "11011101000", "11011100010", "11011101110",
        "11101011000", "11101000110", "11100010110", "11101101000", "11101100010", "11100011010",
        "11101111010", "11001000010", "11110001010", "10100110000", "10100001100", "10010110000",
        "10010000110", "10000101100", "10000100110", "10110010000", "10110000100", "10011010000",
        "10011000010", "10000110100", "10000110010", "11000010010", "11001010000", "11110111010",
        "11000010100", "10001111010", "10100111100", "10010111100", "10010011110", "10111100100",
        "10011110100", "10011110010", "11110100100", "11110010100", "11110010010", "11011011110",
        "11011110110", "11110110110", "10101111000", "10100011110", "10001011110", "10111101000",
        "10111100010", "11110101000", "11110100010", "10111011110", "10111101110", "11101011110",
        "11110101110", "11010000100", "11010010000", "11010011100", "11000111010",
    )

    private val EAN_LEFT_ODD = arrayOf(
        "0001101", "0011001", "0010011", "0111101", "0100011",
        "0110001", "0101111", "0111011", "0110111", "0001011",
    )
    private val EAN_LEFT_EVEN = arrayOf(
        "0100111", "0110011", "0011011", "0100001", "0011101",
        "0111001", "0000101", "0010001", "0001001", "0010111",
    )
    private val EAN_RIGHT = arrayOf(
        "1110010", "1100110", "1101100", "1000010", "1011100",
        "1001110", "1010000", "1000100", "1001000", "1110100",
    )
    private val EAN13_PARITY = arrayOf(
        "LLLLLL", "LLGLGG", "LLGGLG", "LLGGGL", "LGLLGG",
        "LGGLLG", "LGGGLL", "LGLGLG", "LGLGGL", "LGGLGL",
    )

    private val CODE39_CODES: Map<Char, String> = mapOf(
        '0' to "101000111011101", '1' to "111010001010111", '2' to "101110001010111",
        '3' to "111011100010101", '4' to "101000111010111", '5' to "111010001110101",
        '6' to "101110001110101", '7' to "101000101110111", '8' to "111010001011101",
        '9' to "101110001011101", 'A' to "111010100010111", 'B' to "101110100010111",
        'C' to "111011101000101", 'D' to "101011100010111", 'E' to "111010111000101",
        'F' to "101110111000101", 'G' to "101010001110111", 'H' to "111010100011101",
        'I' to "101110100011101", 'J' to "101011100011101", 'K' to "111010101000111",
        'L' to "101110101000111", 'M' to "111011101010001", 'N' to "101011101000111",
        'O' to "111010111010001", 'P' to "101110111010001", 'Q' to "101010111000111",
        'R' to "111010101110001", 'S' to "101110101110001", 'T' to "101011101110001",
        'U' to "111000101010111", 'V' to "100011101010111", 'W' to "111000111010101",
        'X' to "100010111010111", 'Y' to "111000101110101", 'Z' to "100011101110101",
        '-' to "100010101110111", '.' to "111000101011101", ' ' to "100011101011101",
        '*' to "100010111011101", '\$' to "100010001000101", '/' to "100010001010001",
        '+' to "100010100010001", '%' to "101000100010001",
    )

    fun encode(value: String, format: String): String = when (format) {
        "CODE128", "auto" -> encodeCode128(value)
        "CODE39" -> encodeCode39(value)
        "EAN13" -> encodeEAN13(value)
        "EAN8" -> encodeEAN8(value)
        "EAN5", "EAN2" -> encodeEAN52(value, format)
        "UPC", "UPCA" -> encodeUPCA(value)
        "UPCE" -> encodeUPCE(value)
        else -> encodeCode128(value)
    }

    private fun encodeCode128(data: String): String {
        val codes = ArrayList<Int>()
        var checksum = CODE128_START_CODE_B
        codes.add(CODE128_START_CODE_B)
        for (i in data.indices) {
            val code = CODE128_CODE_B_CHARS.indexOf(data[i])
            require(code != -1) { "Invalid character in CODE128: ${data[i]}" }
            codes.add(code)
            checksum += code * (i + 1)
        }
        codes.add(checksum % 103)
        codes.add(CODE128_STOP)
        val sb = StringBuilder()
        for (code in codes) sb.append(CODE128_PATTERNS.getOrElse(code) { "" })
        sb.append("00000")
        return sb.toString()
    }

    private fun encodeCode39(dataIn: String): String {
        val data = dataIn.uppercase()
        val sb = StringBuilder(CODE39_CODES.getValue('*'))
        for (ch in data) {
            val code = CODE39_CODES[ch] ?: throw IllegalArgumentException("Invalid character in CODE39: $ch")
            sb.append('0')
            sb.append(code)
        }
        sb.append('0')
        sb.append(CODE39_CODES.getValue('*'))
        return sb.toString()
    }

    private fun encodeEAN13(data: String): String {
        require(Regex("^\\d{13}\$").matches(data)) { "EAN13 must be 13 digits" }
        var sum = 0
        for (i in 0 until 12) {
            val digit = data[i].digitToInt()
            sum += if (i % 2 == 0) digit else digit * 3
        }
        val checkDigit = (10 - (sum % 10)) % 10
        require(data[12].digitToInt() == checkDigit) { "Invalid EAN13 check digit" }
        val leftData = data.substring(1, 7)
        val rightData = data.substring(7, 13)
        val sb = StringBuilder("101")
        val firstDigit = data[0].digitToInt()
        val pattern = EAN13_PARITY[firstDigit]
        for (i in leftData.indices) {
            val digit = leftData[i].digitToInt()
            sb.append(if (pattern[i] == 'L') EAN_LEFT_ODD[digit] else EAN_LEFT_EVEN[digit])
        }
        sb.append("01010")
        for (i in rightData.indices) sb.append(EAN_RIGHT[rightData[i].digitToInt()])
        sb.append("101")
        return sb.toString()
    }

    private fun encodeEAN8(data: String): String {
        require(Regex("^\\d{8}\$").matches(data)) { "EAN8 must be 8 digits" }
        var sum = 0
        for (i in 0 until 7) {
            val digit = data[i].digitToInt()
            sum += digit * (if (i % 2 == 0) 3 else 1)
        }
        val checkDigit = (10 - (sum % 10)) % 10
        require(data[7].digitToInt() == checkDigit) { "Invalid EAN8 check digit" }
        val leftData = data.substring(0, 4)
        val rightData = data.substring(4, 8)
        val sb = StringBuilder("101")
        for (i in leftData.indices) sb.append(EAN_LEFT_ODD[leftData[i].digitToInt()])
        sb.append("01010")
        for (i in rightData.indices) sb.append(EAN_RIGHT[rightData[i].digitToInt()])
        sb.append("101")
        return sb.toString()
    }

    private fun encodeEAN52(data: String, format: String): String {
        val length = if (format == "EAN5") 5 else 2
        require(Regex("^\\d{$length}$").matches(data)) { "$format must be $length digits" }
        // The upstream parity 'pattern' is computed but never applied; the emitted modules are just
        // each digit's left-odd code separated by '01', prefixed with the start guard, so we mirror
        // exactly that output.
        val sb = StringBuilder("1011")
        for (i in data.indices) {
            if (i > 0) sb.append("01")
            sb.append(EAN_LEFT_ODD[data[i].digitToInt()])
        }
        return sb.toString()
    }

    private fun encodeUPCA(dataIn: String): String {
        var data = dataIn
        if (Regex("^\\d{11}\$").matches(data)) {
            var sum = 0
            for (i in 0 until 11) {
                val digit = data[i].digitToInt()
                sum += if (i % 2 == 0) digit * 3 else digit
            }
            val checkDigit = (10 - (sum % 10)) % 10
            data += checkDigit.toString()
        }
        require(Regex("^\\d{12}\$").matches(data)) { "UPC-A must be 11 or 12 digits" }
        return encodeEAN13("0$data")
    }

    private fun encodeUPCE(dataIn: String): String {
        var data = dataIn
        if (Regex("^\\d{7}\$").matches(data)) {
            var sum = 0
            for (i in 0 until 7) {
                val digit = data[i].digitToInt()
                sum += if (i % 2 == 0) digit * 3 else digit
            }
            val checkDigit = (10 - (sum % 10)) % 10
            data += checkDigit.toString()
        }
        require(Regex("^\\d{8}\$").matches(data)) { "UPC-E must be 7 or 8 digits" }
        require(data[0] == '0' || data[0] == '1') { "UPC-E must start with 0 or 1" }
        val checkDigit = data[7]
        val middleData = data.substring(1, 7)
        val pattern = when (checkDigit) {
            '0', '1', '2', '3' -> "EEEEOO"
            '4' -> "EEEOOO"
            else -> "EEOOOO"
        }
        val sb = StringBuilder("101")
        for (i in middleData.indices) {
            val digit = middleData[i].digitToInt()
            sb.append(if (pattern[i] == 'E') EAN_LEFT_EVEN[digit] else EAN_LEFT_ODD[digit])
        }
        sb.append("010101")
        sb.append("101")
        return sb.toString()
    }
}

/**
 * Encodes [value] as a '1'/'0' module string for barcode [format] (default `auto` = CODE128),
 * throwing [IllegalArgumentException] on invalid input, exactly as uview-plus `u-barcode` does.
 */
internal fun upBarcodeEncode(value: String, format: String = "auto"): String =
    UPBarcodeEncoder.encode(value, format)
