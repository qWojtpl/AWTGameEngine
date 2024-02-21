
function setPosition(identifier, x, y) {
    let object = getObject(identifier);
    object.setAttribute("x", x);
    object.setAttribute("y", y);
    object.style.left = x + "px";
    object.style.top = y + "px";
}

function getObject(identifier) {
    return document.getElementById(identifier);
}