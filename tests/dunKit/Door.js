// Door.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;

function create(me) {
	me.properties._y = 0;
}

function init(me) {
	me.node().setMeshPartDecal(0, game.getAssets().load(IO.file("door-glow.png")));
}

function start(me) {
	me.properties._y = me.node().position.y;
}

function update(me) {
	if(me.scene().isInDesign()) {
		return;
	}
	var ex = me.scene().eye.x;
	var ez = me.scene().eye.z;
	var px = me.node().absolutePosition.x;
	var pz = me.node().absolutePosition.z;
	var dx = ex - px;
	var dz = ez - pz;
	var d = Math.sqrt(dx * dx + dz * dz);
	var a = 1 - Math.min(1, d / 75);
	
	me.node().position.y = me.properties._y - a * 150;
}

function renderSprites(me, renderer) {
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}

