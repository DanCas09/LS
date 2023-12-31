package pt.isel.ls.server.services

import pt.isel.ls.server.BoardList
import pt.isel.ls.server.Card
import pt.isel.ls.server.data.dataInterfaces.models.CardData
import pt.isel.ls.server.data.dataInterfaces.models.ListData
import pt.isel.ls.server.data.dataInterfaces.models.UserBoardData
import pt.isel.ls.server.data.dataInterfaces.models.UserData
import pt.isel.ls.server.data.transactionManager.executor.DataExecutor
import pt.isel.ls.server.utils.validateString

class ListServices(
    private val userData: UserData,
    private val userBoardData: UserBoardData,
    private val listData: ListData,
    private val cardData: CardData,
    private val dataExecutor: DataExecutor
) {

    fun createList(token: String, idBoard: Int, name: String): Int {
        validateString(name, "name")

        return dataExecutor.execute {
            val idUser = userData.getUser(token, it).idUser
            userBoardData.checkUserInBoard(idUser, idBoard, it)
            listData.createList(idBoard, name, it)
        }
    }

    fun getList(token: String, idBoard: Int, idList: Int): BoardList {
        return dataExecutor.execute {
            val idUser = userData.getUser(token, it).idUser
            userBoardData.checkUserInBoard(idUser, idBoard, it)
            listData.getList(idList, idBoard, it)
        }
    }

    fun getCardsFromList(token: String, idBoard: Int, idList: Int, limit: Int?, skip: Int?): List<Card> {
        return dataExecutor.execute {
            val idUser = userData.getUser(token, it).idUser
            userBoardData.checkUserInBoard(idUser, idBoard, it)
            listData.getList(idList, idBoard, it)
            cardData.getCardsFromList(idList, idBoard, it)
        }
    }

    fun getListsOfBoard(token: String, idBoard: Int): List<BoardList> {
        return dataExecutor.execute {
            val idUser = userData.getUser(token, it).idUser
            userBoardData.checkUserInBoard(idUser, idBoard, it)
            listData.getListsOfBoard(idBoard, it)
        }
    }

    fun deleteList(token: String, idBoard: Int, idList: Int, action: String?) {
        return dataExecutor.execute {
            val idUser = userData.getUser(token, it).idUser
            userBoardData.checkUserInBoard(idUser, idBoard, it)
            when (action) {
                "delete" -> cardData.deleteCards(idList, it)
                "archive" -> cardData.archiveCards(idBoard, idList, it)
            }
            listData.deleteList(idList, idBoard, it)
        }
    }
}
