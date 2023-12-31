package pt.isel.ls.server.data.dataMem

import pt.isel.ls.server.Board
import pt.isel.ls.server.BoardList
import pt.isel.ls.server.Card
import pt.isel.ls.server.User
import pt.isel.ls.server.UserBoard
import pt.isel.ls.server.data.dataInterfaces.Data
import pt.isel.ls.server.data.dataMem.models.BoardDataMem
import pt.isel.ls.server.data.dataMem.models.CardDataMem
import pt.isel.ls.server.data.dataMem.models.ListDataMem
import pt.isel.ls.server.data.dataMem.models.UserBoardDataMem
import pt.isel.ls.server.data.dataMem.models.UserDataMem

class DataMem : Data {
    override val userData = UserDataMem()
    override val boardData = BoardDataMem()
    override val userBoardData = UserBoardDataMem()
    override val listData = ListDataMem()
    override val cardData = CardDataMem()
}

val users = mutableListOf<User>(
    User(
        1,
        "alberto.tremocos@gmail.com",
        "Jose",
        "token123",
        "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8",
        "https://live.staticflickr.com/65535/52841364369_13521f6ef1_m.jpg"
    )
)
val boards = mutableListOf<Board>()
val usersBoards = mutableListOf<UserBoard>()
val lists = mutableListOf<BoardList>()
val cards = mutableListOf<Card>()
