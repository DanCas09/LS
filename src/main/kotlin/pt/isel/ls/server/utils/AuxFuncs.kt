package pt.isel.ls.server.utils

import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.LoggerFactory
import pt.isel.ls.server.exceptions.INVAL_PARAM
import pt.isel.ls.server.exceptions.TrelloException
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDate
import kotlin.math.min

val logger = LoggerFactory.getLogger("pt.isel.ls.http.HTTPServer")

fun checkEndDate(endDate: String) {
    val endDateParsed = LocalDate.parse(endDate) // 2023-03-14
    if (endDateParsed < LocalDate.now()) throw TrelloException.IllegalArgument("$INVAL_PARAM $endDate")
}

fun isValidString(value: String, property: String): Boolean {
    val trim = value.trim()
    if (trim != "" && trim != "null") return true
    throw TrelloException.IllegalArgument("$property is not a valid.")
}

fun setup(): PGSimpleDataSource {
    val dataSource = PGSimpleDataSource()
    val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
    dataSource.setURL(jdbcDatabaseURL)
    return dataSource
}

fun checkPaging(max: Int, limit: Int?, skip: Int?): Pair<Int, Int> {
    val skipped = if (skip == null || skip < 0) 0 else min(skip, max)
    var limited = if (limit == null || limit < 0 || skipped + limit > max) max else skipped + limit
    if (skipped > limited) limited = skipped
    return Pair(skipped, limited)
}

fun hashPassword(password: String): String {
    val salt = generateSalt()

    val saltedPassword = password + "123"

    val messageDigest = MessageDigest.getInstance("SHA-256") // Choose the hash algorithm you want to use, such as SHA-256

    val hashedBytes = messageDigest.digest(saltedPassword.toByteArray())
    val stringBuilder = StringBuilder()

    for (hashedByte in hashedBytes) {
        val hex = String.format("%02x", hashedByte)
        stringBuilder.append(hex)
    }

    return stringBuilder.toString()
}

fun generateSalt(): String {
    val random = SecureRandom()
    val saltBytes = ByteArray(16)
    random.nextBytes(saltBytes)
    return saltBytes.joinToString("") { "%02x".format(it) }
}


fun isValidAvatar(urlAvatar: String?) : String {
    return urlAvatar ?: "https://i.imgur.com/1qZ0QZB.png"
}
