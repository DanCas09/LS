package pt.isel.ls.server.data.dataMem

import pt.isel.ls.server.data.dataInterfaces.BoardData
import pt.isel.ls.server.exceptions.TrelloException
import pt.isel.ls.server.utils.Board
import pt.isel.ls.server.utils.User

class BoardDataMem : BoardData {

    val boards =
        mutableListOf<Board>(Board(0, "Board1", "this is description1"), Board(1, "Board2", "this is description2"))

    override fun createBoard(idUser: Int, name: String, description: String): Int {
        val newBoard = Board(getNextId(), name, description)
        boards.add(newBoard)
        return newBoard.idBoard
    }

    override fun getBoard(idBoard: Int): Board {
        return boards.find { it.idBoard == idBoard } ?: throw TrelloException.NotFound("Board")
    }

    override fun checkBoardName(name: String) {
        if (boards.any { it.name == name }) throw TrelloException.AlreadyExists("Board $name")
    }

    override fun getBoardsFromUser(idBoards: List<Int>): List<Board> {
        return boards.filter { idBoards.contains(it.idBoard) }
    }

    private fun getNextId(): Int {
        return if (boards.isEmpty()) 0 else boards.last().idBoard + 1
    }
}
