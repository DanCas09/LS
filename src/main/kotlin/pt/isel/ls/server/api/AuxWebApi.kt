package pt.isel.ls.server.api

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import pt.isel.ls.server.annotations.Auth
import pt.isel.ls.server.exceptions.INVAL_PARAM
import pt.isel.ls.server.exceptions.TrelloException
import pt.isel.ls.server.utils.logger
import java.lang.reflect.InvocationTargetException
import java.sql.SQLException
import kotlin.reflect.KFunction
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.isAccessible

fun getToken(request: Request): String {
    val authHeader = request.header("Authorization") ?: throw TrelloException.NotAuthorized()
    return authHeader.removePrefix("Bearer ")
}

fun getPathParam(request: Request, name: String): Int {
    return request.path(name)?.toIntOrNull() ?: throw TrelloException.IllegalArgument(INVAL_PARAM + name)
}

fun getQueryParam(request: Request, name: String): String? {
    return request.query(name)
}

fun handleRequest(request: Request, handler: KFunction<Response>): Response {
    logRequest(request)
    handler.isAccessible = true
    return try {
        if (isAuthRequired(handler)) {
            handler.call(request, getToken(request))
        } else {
            handler.call(request)
        }
    } catch (e: Exception) {
        when (val cause = if (e is InvocationTargetException) e.targetException else e) {
            is SerializationException -> {
                println("Serialization")
                println(cause.message)
                val field = cause.message?.substringBefore(" for", "Invalid body.")
                createRsp(Status.BAD_REQUEST, "$INVAL_PARAM $field")
            }
            /*is IllegalArgumentException -> {
                println("IllegalArgument")
                println(cause.message)
                createRsp(Status.BAD_REQUEST, cause.message)
            }*/
            is TrelloException -> createRsp(cause.status, cause.message)
            is SQLException -> {
                println(cause.localizedMessage)
                println(cause.sqlState)
                println(cause.errorCode)
                createRsp(Status.BAD_REQUEST, cause.message)
            }
            else -> createRsp(Status.BAD_REQUEST, cause.message)
        }
    }
}

private fun isAuthRequired(function: KFunction<*>): Boolean {
    return function.hasAnnotation<Auth>()
}

inline fun <reified T> createRsp(status: Status, body: T): Response {
    return Response(status)
        .header("content-type", "application/json")
        .body(Json.encodeToString(body))
}

fun logRequest(request: Request) {
    logger.info(
        "incoming request: method={}, uri={}, content-type={} accept={}",
        request.method,
        request.uri,
        request.header("content-type"),
        request.header("accept")
    )
}
