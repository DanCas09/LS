package pt.isel.ls.tests.utils

import org.http4k.routing.routes
import pt.isel.ls.server.api.WebAPI
import pt.isel.ls.server.data.dataMem.DataMem
import pt.isel.ls.server.routes.BoardRoutes
import pt.isel.ls.server.routes.CardRoutes
import pt.isel.ls.server.routes.ListRoutes
import pt.isel.ls.server.routes.UserRoutes
import pt.isel.ls.server.services.Services
import pt.isel.ls.server.utils.User

const val invalidToken = "INVALID_TOKEN"
const val invalidId = 1904

/** User Dummies **/
const val dummyName = "Alberto"
const val dummyEmail = "alberto.tremocos@gmail.com"
val dummyBadEmail = dummyEmail.replace("@", "")

/** Board Dummies **/
const val dummyBoardName = "Board1"
const val dummyBoardDescription = "This is Board1"

/** BoardList Dummies **/
const val dummyBoardListName = "List1"

/** Card Dummies **/
const val dummyCardName = "Card1"
const val dummyCardDescription = "This is Card1"
const val validEndDate = "2023-12-12"
const val invalidEndDate = "INVALID_END_DATE"

/** Base Url **/
const val baseUrl = "http://localhost:8080"

/** modules **/
val dataMem = DataMem()

val services = Services(dataMem)

val webAPI = WebAPI(services)

/** tests initial components **/
var user = User(0, dummyEmail, dummyName, "token")
var boardId = 0
var listId = 0
var cardId = 0

/** routes **/
val app = routes(
    UserRoutes(webAPI.userAPI)(),
    BoardRoutes(webAPI.boardAPI)(),
    ListRoutes(webAPI.listAPI)(),
    CardRoutes(webAPI.cardAPI)()
)
