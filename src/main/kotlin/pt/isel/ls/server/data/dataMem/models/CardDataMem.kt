package pt.isel.ls.server.data.dataMem.models

import pt.isel.ls.server.data.transactionManager.transaction.ITransactionContext
import pt.isel.ls.server.data.dataInterfaces.models.CardData
import pt.isel.ls.server.data.dataMem.cards
import pt.isel.ls.server.exceptions.INVAL_PARAM
import pt.isel.ls.server.exceptions.NOT_FOUND
import pt.isel.ls.server.exceptions.TrelloException
import pt.isel.ls.server.utils.Card
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CardDataMem : CardData {

    override fun createCard(
        idList: Int,
        idBoard: Int,
        name: String,
        description: String?,
        endDate: String?,
        ctx: ITransactionContext
    ): Int {
        if (name.length > 20) throw SQLException("$INVAL_PARAM name is too long.", "22001")
        val newCard =
            Card(
                getNextId(),
                idList,
                idBoard,
                name,
                description,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                endDate,
                false,
                getNextIdx(idList, ctx)
            )
        cards.add(newCard)
        return newCard.idCard
    }

    override fun getCardsFromList(idList: Int, idBoard: Int, ctx: ITransactionContext): List<Card> {
        return cards.filter { it.idList == idList && it.idBoard == idBoard }.sortedBy { it.idx }
    }

    override fun getCard(idCard: Int, idBoard: Int, ctx: ITransactionContext): Card {
        return cards.find { it.idCard == idCard && it.idBoard == idBoard }
            ?: throw TrelloException.NotFound("Card $NOT_FOUND")
    }

    override fun moveCard(
        idCard: Int,
        idBoard: Int,
        idList: Int,
        idListDst: Int,
        idx: Int,
        idxDst: Int,
        ctx: ITransactionContext
    ) {
        val card = getCard(idCard, idBoard, ctx)
        val cardIdx = card.idx
        cards.forEach {
            if (it.idList == card.idList && it.idx >= cardIdx) it.idx--
            if (it.idList == idListDst && it.idx >= idxDst) it.idx++
        }
        card.idList = idListDst
        card.idx = idxDst
    }

    override fun decreaseIdx(idList: Int, idx: Int, ctx: ITransactionContext) {
        cards.forEach { if (it.idList == idList && it.idx >= idx) it.idx-- }
    }

    override fun deleteCard(idCard: Int, idBoard: Int, ctx: ITransactionContext) {
        val card: Card
        try {
            card = getCard(idCard, idBoard, ctx)
        } catch (ex: TrelloException) {
            throw TrelloException.NoContent()
        }
        cards.remove(card)
        if (card.idList == null) return
        cards.forEach {
            if (it.idList == card.idList && it.idx >= card.idx) it.idx--
        }
    }

    override fun deleteCards(idList: Int, ctx: ITransactionContext) {
        val filtered = cards.filter { it.idList == idList }
        cards.removeAll(filtered)
    }

    override fun archiveCards(idBoard: Int, idList: Int, ctx: ITransactionContext) {
        val filtered = cards.filter { it.idList == idList && it.idBoard == idBoard }
        filtered.forEach {
            it.archived = true
            it.idList = null
        }
    }

    override fun getNextIdx(idList: Int, ctx: ITransactionContext): Int {
        val filtered = cards.filter { it.idList == idList }.sortedBy { it.idx }
        return if (filtered.isEmpty()) 1 else filtered.last().idx + 1
    }

    override fun getCardCount(idBoard: Int, idList: Int, ctx: ITransactionContext): Int {
        return cards.count { it.idBoard == idBoard && it.idList == idList }
    }

    override fun getArchivedCards(idBoard: Int, ctx: ITransactionContext): List<Card> {
        return cards.filter { it.idBoard == idBoard && it.idList == null }
    }

    override fun updateCard(
        card: Card,
        description: String?,
        endDate: String?,
        idList: Int?,
        archived: Boolean,
        ctx: ITransactionContext
    ) {
        val found = cards.find { it.idCard == card.idCard } ?: return
        found.archived = archived
        found.description = description
        found.endDate = endDate
        found.idList = idList
    }

    private fun getNextId(): Int {
        return if (cards.isEmpty()) 1 else cards.last().idCard + 1
    }
}
