package pt.isel.ls.server.api

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import pt.isel.ls.server.annotations.Auth
import pt.isel.ls.server.services.ListServices
import pt.isel.ls.server.utils.BoardListIn

class ListAPI(private val services: ListServices) {

    fun createList(request: Request): Response {
        return handleRequest(request, ::createListInternal)
    }

    fun getList(request: Request): Response {
        return handleRequest(request, ::getListInternal)
    }

    fun getListsFromBoard(request: Request): Response {
        return handleRequest(request, ::getListsFromBoardInternal)
    }

    fun deleteList(request: Request) : Response {
        return handleRequest(request, ::deleteListInternal)
    }

    @Auth
    private fun createListInternal(request: Request, token: String): Response {
        val idBoard = getPathParam(request, "idBoard")
        val newList = Json.decodeFromString<BoardListIn>(request.bodyString())
        return createRsp(CREATED, services.createList(token, idBoard, newList.name))
    }

    @Auth
    private fun getListInternal(request: Request, token: String): Response {
        val idBoard = getPathParam(request, "idBoard")
        val idList = getPathParam(request, "idList")
        return createRsp(OK, services.getList(token, idBoard, idList))
    }

    @Auth
    private fun getListsFromBoardInternal(request: Request, token: String): Response {
        val idBoard = getPathParam(request, "idBoard")
        val limit = getQueryParam(request,"limit")?.toIntOrNull()
        val skip = getQueryParam(request,"skip")?.toIntOrNull()
        return createRsp(OK, services.getListsOfBoard(token, idBoard, limit, skip))
    }

    @Auth
    private fun deleteListInternal(request: Request, token: String): Response {
        val idBoard = getPathParam(request, "idBoard")
        val idList = getPathParam(request, "idList")
        return createRsp(NO_CONTENT, services.deleteList(token, idBoard, idList))
    }
}
