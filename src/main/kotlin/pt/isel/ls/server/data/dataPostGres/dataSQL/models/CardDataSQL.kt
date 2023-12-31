package pt.isel.ls.server.data.dataPostGres.dataSQL.models

import pt.isel.ls.server.Card
import pt.isel.ls.server.data.dataInterfaces.models.CardData
import pt.isel.ls.server.data.dataPostGres.statements.CardStatements
import pt.isel.ls.server.data.transactionManager.transactions.SQLTransaction
import pt.isel.ls.server.data.transactionManager.transactions.TransactionCtx
import pt.isel.ls.server.exceptions.NOT_FOUND
import pt.isel.ls.server.exceptions.TrelloException

class CardDataSQL : CardData {

    override fun createCard(
        idList: Int,
        idBoard: Int,
        name: String,
        description: String?,
        endDate: String?,
        ctx: TransactionCtx
    ): Int {
        val insertStmt =
            CardStatements.createCardCMD(idList, idBoard, name, description, endDate, getNextIdx(idList, ctx))

        val res = ctx.con.prepareStatement(insertStmt).executeQuery()
        res.next()

        return res.getInt("idCard")
    }

    override fun getCardsFromList(idList: Int, idBoard: Int, ctx: TransactionCtx): List<Card> {
        val selectStmt = CardStatements.getCardsFromListCMD(idList, idBoard)
        val cards = mutableListOf<Card>()

        val res = ctx.con.prepareStatement(selectStmt).executeQuery()

        while (res.next()) {
            if (res.row == 0) return emptyList() // test if this works both in here and in BoardSQL
            cards.add(
                Card(
                    res.getInt("idCard"),
                    res.getInt("idList"),
                    res.getInt("idBoard"),
                    res.getString("name"),
                    if (res.getString("description") == "null") null else res.getString("description"),
                    res.getString("startDate"),
                    res.getString("endDate"),
                    res.getBoolean("archived"),
                    res.getInt("idx")
                )
            )
        }
        return cards.sortedBy { it.idx }
    }

    override fun getCard(idCard: Int, idBoard: Int, ctx: TransactionCtx): Card {
        val selectStmt = CardStatements.getCardCMD(idCard, idBoard)

        val res = ctx.con.prepareStatement(selectStmt).executeQuery()
        res.next()

        if (res.row == 0) throw TrelloException.NotFound("Card $NOT_FOUND")

        return Card(
            idCard,
            if (res.getInt("idList") == 0) null else res.getInt("idList"),
            idBoard,
            res.getString("name"),
            res.getString("description"),
            res.getString("startDate"),
            res.getString("endDate"),
            res.getBoolean("archived"),
            res.getInt("idx")
        )
    }

    override fun moveCard(
        idCard: Int,
        idBoard: Int,
        idList: Int,
        idListDst: Int,
        idx: Int,
        idxDst: Int,
        ctx: TransactionCtx
    ) {
        val updateStmtCard = CardStatements.moveCardCMD(idCard, idList, idBoard, idListDst, idxDst)

        val con = ctx.con

        val decreaseStmt = CardStatements.decreaseIdx(idList, idx)
        con.prepareStatement(decreaseStmt).executeUpdate()

        val increaseStmt = CardStatements.increaseIdx(idListDst, idxDst)
        con.prepareStatement(increaseStmt).executeUpdate()

        con.prepareStatement(updateStmtCard).executeUpdate()
    }

    override fun decreaseIdx(idList: Int, idx: Int, ctx: TransactionCtx) {
        val updateStmt = CardStatements.decreaseIdx(idList, idx)
        ctx.con.prepareStatement(updateStmt).executeUpdate()
    }

    override fun deleteCard(idCard: Int, idBoard: Int, ctx: TransactionCtx) {
        val deleteStmt = CardStatements.deleteCard(idCard, idBoard)

        val res = ctx.con.prepareStatement(deleteStmt).executeQuery()
        res.next()

        if (res.row == 0) throw TrelloException.NoContent()

        val idList = res.getInt("idList")
        if (idList != 0) {
            val idx = res.getInt("idx")

            val updateIdxStmt = CardStatements.decreaseIdx(idList, idx)

            ctx.con.prepareStatement(updateIdxStmt).executeUpdate()
        }
    }

    override fun deleteCards(idList: Int, ctx: TransactionCtx) {
        val updateStmt = CardStatements.deleteCards(idList)
        ctx.con.prepareStatement(updateStmt).executeUpdate()
    }

    override fun archiveCards(idBoard: Int, idList: Int, ctx: TransactionCtx) {
        val updateStmt = CardStatements.archiveCards(idBoard, idList)
        ctx.con.prepareStatement(updateStmt).executeUpdate()
    }

    override fun getNextIdx(idList: Int, ctx: TransactionCtx): Int {
        val selectStmt = CardStatements.getNextIdx(idList)

        val res = (ctx as SQLTransaction).con.prepareStatement(selectStmt).executeQuery()
        res.next()

        return if (res.getInt("max") == 0) 1 else res.getInt("max") + 1
    }

    override fun getCardCount(idBoard: Int, idList: Int, ctx: TransactionCtx): Int {
        val selectStmt = CardStatements.getCardCount(idBoard, idList)

        val res = ctx.con.prepareStatement(selectStmt).executeQuery()
        res.next()

        return res.getInt("count")
    }

    override fun getArchivedCards(idBoard: Int, ctx: TransactionCtx): List<Card> {
        val selectStmt = CardStatements.getArchivedCards(idBoard)
        val cards = mutableListOf<Card>()

        val res = ctx.con.prepareStatement(selectStmt).executeQuery()

        while (res.next()) {
            if (res.row == 0) return emptyList() // test if this works both in here and in BoardSQL
            cards.add(
                Card(
                    res.getInt("idCard"),
                    if (res.getInt("idList") == 0) null else res.getInt("idList"),
                    res.getInt("idBoard"),
                    res.getString("name"),
                    res.getString("description"),
                    res.getString("startDate"),
                    res.getString("endDate"),
                    res.getBoolean("archived"),
                    res.getInt("idx")
                )
            )
        }
        return cards
    }

    override fun updateCard(
        card: Card,
        description: String?,
        endDate: String?,
        idList: Int?,
        archived: Boolean,
        idx: Int,
        ctx: TransactionCtx
    ) {
        val updateStmt = CardStatements.updateCard(
            card.idCard,
            card.idBoard,
            description,
            endDate,
            idList,
            archived,
            idx
        )

        val con = ctx.con
        con.prepareStatement(updateStmt).executeUpdate()
        if (idList != null) return
        val decreaseStmt = CardStatements.decreaseIdx(card.idList, card.idx)
        con.prepareStatement(decreaseStmt).executeUpdate()
    }
}
