// Grid.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;
var UIButton = org.game.UIButton;
var UI = org.game.UI;
var SceneNode = org.game.SceneNode;

function create(me) {
	me.properties._build = false;
	me.properties.build = new UIButton("build",
		function(me) {
			me.properties._build = true;
		}
	)
}

function init(me) {
}

function start(me) {
}

function update(me) {
	if(me.properties._build) {
		me.properties._build = false;
		build(me);
	}
}

function renderSprites(me, renderer) {
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}

function build(me) {
	var r1 = 1;
	var g1 = 0;
	var b1 = 0;
	var r2 = 1;
	var g2 = 1;
	var b2 = 0;
	var r3 = 0;
	var g3 = 0;
	var b3 = 1;
	var r4 = 0;
	var g4 = 1;
	var b4 = 0;
	me.node().detachAllChildren();
	for(var r = 0; r != 16; r++) {
		var a1 = r / 15;
		var rr1 = r1 + a1 * (r2 - r1);
		var rg1 = g1 + a1 * (g2 - g1);
		var rb1 = b1 + a1 * (b2 - b1);
		var rr2 = r3 + a1 * (r4 - r3);
		var rg2 = g3 + a1 * (g4 - g3);
		var rb2 = b3 + a1 * (b4 - b3);
		var z = -80 + r / 15 * 160;
		for(var c = 0; c != 16; c++) {
			var a2 = c / 15;
			var cr1 = rr1 + a2 * (rr2 - rr1);
			var cg1 = rg1 + a2 * (rg2 - rg1);
			var cb1 = rb1 + a2 * (rb2 - rb1);
			var x = -80 + c / 15 * 160;
			var n = new SceneNode();

			n.renderable = game.getAssets().load(IO.file("cube.obj"));
			n.ambientColor.set(cr1, cg1, cb1, 1).scale(0.5);
			n.diffuseColor.set(cr1, cg1, cb1, 1).scale(2);
			n.position.set(x + 5, 0, z + 5);
			me.node().addChild(n);
		}
	}
	UI.rebuildTree();
}

