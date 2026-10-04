// Monster.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;

function create(me) {
}

function init(me) {
	me.node().renderable.setSequence(0, me.node().renderable.getFrameCount() - 1, 9, true);
}

function start(me) {
}

function update(me) {
}

function renderSprites(me, renderer) {
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}

