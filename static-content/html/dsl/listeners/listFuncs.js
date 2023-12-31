import {input} from "../../common/components/elements.js";
import listData from "../../../data/listData.js";
import listContainer from "../components/lists/listContainer.js";
import cardFuncs from "./cardFuncs.js";


export async function createList(boardContainer, board) {
    const inputHtml = input()
    const createListButton = boardContainer.querySelector('.create-list-button')
    boardContainer.insertBefore(inputHtml, createListButton)
    boardContainer.scrollLeft = boardContainer.scrollWidth
    inputHtml.focus()
    const handleAddList = async () => {
        if(inputHtml.value.trim() === ""){
            boardContainer.removeChild(inputHtml)
            return
        }
        await addList(boardContainer, inputHtml, board, createListButton)
    }
    inputHtml.addEventListener("focusout", handleAddList)
    inputHtml.addEventListener("keydown", (event) => {
        if(event.key !== "Enter" || event.repeat) return
        inputHtml.removeEventListener("focusout", handleAddList)
        handleAddList()
    })
}

async function addList(boardContainer, input, board, createListButton) {
    const list = {
        name: input.value
    }
    input.remove()
    const idList = await listData.createList(board.idBoard, input.value)
    list.idBoard = board.idBoard
    list.idList = idList // comment
    boardContainer.insertBefore(listContainer(list), createListButton)
}

export async function deleteList(list) {
    const listToDelete = document.querySelector(`#List${list.idList}`)

    const card = listToDelete.querySelector(".card-container")

    const deleteHandler = () => {
        const board = document.querySelector("#boardContainer")
        board.removeChild(listToDelete)
        $('#listModal').modal('hide')
    }

    if(!card) {
        await listData.deleteList(list.idBoard, list.idList)
        deleteHandler()
    } else {
        $('#listModal').modal('show')

        document.querySelector('#listDeleteButton').onclick = async () => {
            await listData.deleteList(list.idBoard, list.idList, "delete")
            deleteHandler()
        }

        document.querySelector('#listArchiveButton').onclick = async () => {
            await listData.deleteList(list.idBoard, list.idList, "archive")
            // move them visually
            const archivedContainer = document.querySelector(`#dropdownMenu-archived`)
            const listContainer = document.querySelector(`#list${list.idList}`)
            listContainer.childNodes.forEach((c) => {
                const archCard = {
                    idBoard: list.idBoard,
                    idCard: c.dataset.idCard,
                    name: c.innerText,
                }
                cardFuncs.moveToArchivedContainer(archCard, archivedContainer)
            })
            deleteHandler()
        }
    }
}