package pt.isel.ls.tests.services

import pt.isel.ls.server.exceptions.TrelloException
import pt.isel.ls.server.utils.Card
import pt.isel.ls.tests.utils.boardId
import pt.isel.ls.tests.utils.cardId
import pt.isel.ls.tests.utils.dataSetup
import pt.isel.ls.tests.utils.dummyBoardListName
import pt.isel.ls.tests.utils.dummyCardDescription
import pt.isel.ls.tests.utils.dummyCardName
import pt.isel.ls.tests.utils.invalidId
import pt.isel.ls.tests.utils.invalidToken
import pt.isel.ls.tests.utils.listId
import pt.isel.ls.tests.utils.services
import pt.isel.ls.tests.utils.user
import pt.isel.ls.tests.utils.validEndDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServicesCardTests {

    @BeforeTest
    fun setup() {
        dataSetup(Card::class.java)
    }

    @Test
    fun `Create a valid card without endDate`() {
        val newCardId =
            services.cardServices.createCard(user.token, boardId, listId, dummyCardName, dummyCardDescription, null)
        assertEquals(0, newCardId)
    }

    @Test
    fun `Create a valid card with endDate`() {
        val newCardId = services.cardServices.createCard(
            user.token,
            boardId,
            listId,
            dummyCardName,
            dummyCardDescription,
            validEndDate
        )
        assertEquals(0, newCardId)
    }

    @Test
    fun `Create a card with invalid endDate`() {
        val err = assertFailsWith<TrelloException.IllegalArgument> {
            services.cardServices.createCard(
                user.token,
                boardId,
                listId,
                dummyCardName,
                dummyCardDescription,
                "2023-03-14"
            )
        }
        assertEquals(400, err.status.code)
        assertEquals("Invalid parameters: 2023-03-14", err.message)
    }

    @Test
    fun `Create a card with invalid token`() {
        val err = assertFailsWith<TrelloException.NotAuthorized> {
            services.cardServices.createCard(invalidToken, boardId, listId, dummyCardName, dummyCardDescription, null)
        }
        assertEquals(401, err.status.code)
        assertEquals("Unauthorized Operation.", err.message)
    }

    @Test
    fun `Get a card from a list`() {
        val newCardId =
            services.cardServices.createCard(user.token, boardId, listId, dummyCardName, dummyCardDescription, null)
        val card = services.cardServices.getCard(user.token, boardId, listId, newCardId)
        assertEquals(newCardId, card.idCard)
        assertEquals(listId, card.idList)
    }

    @Test
    fun `Get a card from an invalid board`() {
        val err = assertFailsWith<TrelloException.NotFound> {
            services.cardServices.getCard(user.token, invalidId, listId, cardId)
        }
        assertEquals(404, err.status.code)
        assertEquals("Board not found.", err.message)
    }

    @Test
    fun `Get a card from an invalid list`() {
        val err = assertFailsWith<TrelloException.NotFound> {
            services.cardServices.getCard(user.token, boardId, invalidId, cardId)
        }
        assertEquals(404, err.status.code)
        assertEquals("List not found.", err.message)
    }

    @Test
    fun `Get a card from an invalid user`() {
        val err = assertFailsWith<TrelloException.NotAuthorized> {
            services.cardServices.getCard(invalidToken, boardId, listId, cardId)
        }
        assertEquals(401, err.status.code)
        assertEquals("Unauthorized Operation.", err.message)
    }

    @Test
    fun `Get cards from a list`() {
        val newCardId =
            services.cardServices.createCard(user.token, boardId, listId, dummyCardName, dummyCardDescription, null)
        val cards = services.cardServices.getCardsFromList(user.token, boardId, listId)
        assertEquals(1, cards.size)
        assertEquals(newCardId, cards[0].idCard)
        assertEquals(listId, cards[0].idList)
    }

    @Test
    fun `Get cards from invalid board`() {
        val err = assertFailsWith<TrelloException.NotFound> {
            services.cardServices.getCardsFromList(user.token, invalidId, listId)
        }
        assertEquals(404, err.status.code)
        assertEquals("Board not found.", err.message)
    }

    @Test
    fun `Get cards from invalid list`() {
        val err = assertFailsWith<TrelloException.NotFound> {
            services.cardServices.getCardsFromList(user.token, boardId, invalidId)
        }
        assertEquals(404, err.status.code)
        assertEquals("List not found.", err.message)
    }

    @Test
    fun `Get cards from invalid user`() {
        val err = assertFailsWith<TrelloException.NotAuthorized> {
            services.cardServices.getCardsFromList(invalidToken, boardId, listId)
        }
        assertEquals(401, err.status.code)
        assertEquals("Unauthorized Operation.", err.message)
    }

    @Test
    fun `Move card from list to another list`() {
        val newCardId =
            services.cardServices.createCard(user.token, boardId, listId, dummyCardName, dummyCardDescription, null)
        val newCard = services.cardServices.getCard(user.token, boardId, listId, newCardId)
        assertEquals(newCardId, newCard.idCard)
        assertEquals(listId, newCard.idList)

        val listId2 = services.listServices.createList(user.token, boardId, dummyBoardListName)
        services.cardServices.moveCard(user.token, boardId, listId, listId2, newCardId)
        val movedCard = services.cardServices.getCard(user.token, boardId, listId2, newCardId)
        assertEquals(newCardId, movedCard.idCard)
        assertEquals(listId2, movedCard.idList)
    }

    @Test
    fun `Move card from list to another invalid list`() {
        val newCardId =
            services.cardServices.createCard(user.token, boardId, listId, dummyCardName, dummyCardDescription, null)
        val newCard = services.cardServices.getCard(user.token, boardId, listId, newCardId)
        assertEquals(newCardId, newCard.idCard)
        assertEquals(listId, newCard.idList)

        val err = assertFailsWith<TrelloException.NotFound> {
            services.cardServices.moveCard(user.token, boardId, listId, invalidId, newCardId)
        }
        assertEquals(404, err.status.code)
        assertEquals("List not found.", err.message)
    }

    @Test
    fun `Move card from list to another list with invalid user`() {
        val newCardId =
            services.cardServices.createCard(user.token, boardId, listId, dummyCardName, dummyCardDescription, null)
        val newCard = services.cardServices.getCard(user.token, boardId, listId, newCardId)
        assertEquals(newCardId, newCard.idCard)
        assertEquals(listId, newCard.idList)

        val listId2 = services.listServices.createList(user.token, boardId, dummyBoardListName)
        val err = assertFailsWith<TrelloException.NotAuthorized> {
            services.cardServices.moveCard(invalidToken, boardId, listId, listId2, newCardId)
        }
        assertEquals(401, err.status.code)
        assertEquals("Unauthorized Operation.", err.message)
    }

    @Test
    fun `Move card from list to another list with invalid card`() {
        val listId2 = services.listServices.createList(user.token, boardId, dummyBoardListName)
        val err = assertFailsWith<TrelloException.NotFound> {
            services.cardServices.moveCard(user.token, boardId, listId, listId2, invalidId)
        }
        assertEquals(404, err.status.code)
        assertEquals("Card not found.", err.message)
    }
}
