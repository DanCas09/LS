import {button, div, h1} from "../../html/common/components/elements.js";
import {mainContent, MAX_BOARDS_DISPLAY} from "../../config.js";
import {coloredContainer, createRows} from "../../html/common/components/containers.js";
import {addUserToBoard, boardFunc, updateBoardsPath} from "../../html/dsl/listeners/boardFuncs.js";
import Pagination from "../../html/dsl/components/boards/pagination.js";
import createSearchBar from "../../html/dsl/components/boards/searchBar.js";
import {archivedDropdown, usersDropdown} from "../../html/dsl/dropdowns/modelDropdowns.js";
import listContainer from "../../html/dsl/components/lists/listContainer.js";
import {createList} from "../../html/dsl/listeners/listFuncs.js";
import {cardModalHTML, listModalHTML} from "../../html/dsl/modals/modals.js";


function boardsPageView(boards, totalBoards, skip, limit, name, numLists) {
    skip = parseInt(skip)
    limit = parseInt(limit)

    h1("My Boards")

    const object = updateBoardsPath(skip, limit, name, numLists, totalBoards) // if skip > 0 ...
    //document.location = object.newPath this is not necessary...
    skip = object.skip
    limit = object.limit

    const pagContainer = div(null, ["paginationContainer"])

    const boardsContainer = div(null, ["boardBox-container"])

    console.log("skip " + skip)

    const pagination = new Pagination(boardsContainer, skip, limit, totalBoards)

    pagContainer.replaceChildren(
        pagination.createLimitSelector(),
        pagination.createPaginationButtons(),
        createSearchBar(name, numLists)
    )
}

export function renderBoards(boards, name, numLists, renderedContainer) {

    const boardsContainers = boards.map(board =>
        coloredContainer(board.name, board.description, board.numLists, () =>
            boardFunc(board), board.primaryColor, board.secondaryColor
        )
    )
    renderedContainer.replaceChildren(createRows(boardsContainers, MAX_BOARDS_DISPLAY))
}

function boardPageView(board, boardUsers) {
    mainContent.style.background = `linear-gradient(135deg, ${board.primaryColor}, ${board.secondaryColor})`

    const addUserToBoardButton = button("+", ["add-user-to-board-button"])

    const boardBtnContainer = div(null, ["board-buttons-users"], null,
        addUserToBoardButton,
        usersDropdown(boardUsers),
    );

    addUserToBoardButton.addEventListener("click", () => addUserToBoard(boardBtnContainer, board))

    div(null, ["board-header"], null,
        h1(board.description, ["board-desc"]),
        boardBtnContainer,
        div(null, ["board-buttons-archived"], null,
            archivedDropdown(board.archivedCards)
        )
    )

    const listCards = board.lists.map(list => listContainer(list))
    const createListButton = button("Add new List", ["create-list-button"])
    createListButton.addEventListener("click", () => createList(boardContainer, board))

    const boardContainer = div(null, ["board"], "boardContainer",
        ...listCards,
        createListButton
    )

    cardModalHTML()
    listModalHTML()
}

export default {
    boardsPageView,
    renderBoards,
    boardPageView
}