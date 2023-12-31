import {mainContent} from "../../../config.js"

export function createElement(tagName, innerText, classArr, id, ...children) {
    const element = document.createElement(tagName)

    if (classArr != null) classArr.forEach(c => element.classList.add(c))
    if (id != null) element.id = id
    if (innerText != null) element.innerText = innerText

    children.forEach(child => {
        if(!child) return
        if (typeof child === "string") {
            const textNode = document.createTextNode(child)
            element.appendChild(textNode)
        } else {
            element.appendChild(child)
        }
    })

    mainContent.appendChild(element)
    return element
}

export function div(innerText, classArr, id, ...children) {
    return createElement("div", innerText, classArr, id, ...children)
}

export function button(innerText, classArr, id, ...children) {
    return createElement("button", innerText, classArr, id, ...children)
}

export function h1(innerText, classArr, id, ...children) {
    return createElement("h1", innerText, classArr, id, ...children)
}

export function h5(innerText, classArr, id, ...children) {
    return createElement("h5", innerText, classArr, id, ...children)
}

export function p1(innerText, classArr, id, ...children) {
    return createElement("p1", innerText, classArr, id, ...children)
}

export function p2(innerText, classArr, id, ...children) {
    return createElement("p2", innerText, classArr, id, ...children)
}

export function span(innerText, classArr, id, ...children) {
    return createElement("span", innerText, classArr, id, ...children)
}

export function input(innerText, classArr, id, placeholder, ...children) {
    const inp = createElement("input", innerText, classArr, id, ...children)
    if(placeholder) inp.placeholder = placeholder
    return inp
}

export function option(innerText, classArr, id, ...children) {
    return createElement("option", innerText, classArr, id, ...children)
}

export function select(innerText, classArr, id, ...children) {
    return createElement("select", innerText, classArr, id, ...children)
}

export function img(innerText, classArr, id, ...children) {
    return createElement("img", innerText, classArr, id, ...children)
}

export function p(innerText, classArr, id, ...children) {
    return createElement("p", innerText, classArr, id, ...children)
}

export function form(innerText, classArr, id, ...children) {
    return createElement("form", innerText, classArr, id, ...children)
}

export function label(innerText, classArr, id, ...children) {
    return createElement("label", innerText, classArr, id, ...children)
}

export function ul(innerText, classArr, id, ...children) {
    return createElement("ul", innerText, classArr, id, ...children)
}

export function li(innerText, classArr, id, ...children) {
    return createElement("li", innerText, classArr, id, ...children)
}

export function textarea(innerText, classArr, id, ...children) {
    return createElement("textarea", innerText, classArr, id, ...children)
}

export function a(innerText, classArr, id, ...children) {
    return createElement("a", innerText, classArr, id, ...children)
}

export function nav(innerText, classArr, id, ...children) {
    return createElement("nav", innerText, classArr, id, ...children)
}

export function small(innerText, classArr, id, ...children) {
    return createElement("small", innerText, classArr, id, ...children)
}

// Receives a handler for when a file is accepted from the user. The handler receives a reader object to read the file.
export function fileInput(handler) {
    const inputHtml = document.createElement("input")
    inputHtml.type = 'file'
    inputHtml.accept = 'image/*'

    inputHtml.addEventListener('change', () => {
        const file = inputHtml.files[0]
        if (!file) return

        const reader = new FileReader()
        reader.onload = () => handler(reader)
        reader.readAsDataURL(file)
    })

    inputHtml.click()
}