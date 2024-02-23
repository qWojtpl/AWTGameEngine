
function setPosition(identifier, x, y) {
    let object = getObject(identifier);
    object.setAttribute("x", x);
    object.setAttribute("y", y);
    object.style.left = x + "px";
    object.style.top = y + "px";
    object.style.position = "absolute";
}

function setSize(identifier, width, height) {
    let object = getObject(identifier);
    object.setAttribute("sizeX", width);
    object.setAttribute("sizeY", height);
    object.style.width = width + "px";
    object.style.height = height + "px";
}

function drawImage(identifier, image) {
    let object = getObject(identifier);
    object.style.setProperty("-fx-background-image", "url('" + image + "')";
}

function getObject(identifier) {
    return document.getElementById(identifier);
}