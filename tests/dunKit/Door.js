// Door.js
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
	if(me.scene().isInDesign()) {
		return;
	}
	var e = me.scene().eye;
	var y = e.y;
	
	e.y = 0;
	
	var p = me.node().absolutePosition;
	var d = e.distance(p.x, 0, p.z);
	var a = 1 - Math.min(d / 100, 1);
	
	e.y = y;
	
	me.node().position.y = -a * 200;
}

function renderSprites(me, renderer) {
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}