// TorchLight.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;

function create(me) {
}

function init(me) {
}

function start(me) {
}

function update(me) {
	me.node().lightRadius = 75 + Math.sin(game.totalTime() * 7) * 15;
}

function renderSprites(me, renderer) {
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}

