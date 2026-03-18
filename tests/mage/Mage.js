// Mage.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;

function create(me) {
}

function init(me) {
	var kfm = me.node().renderable;
	
	kfm.setSequence(0, kfm.getFrameCount() - 1, 10, true);
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

