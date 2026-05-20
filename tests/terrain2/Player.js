// Player.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;
var Controller = org.game.Controller;
var Resource = org.game.Resource;

function create(me) {
	me.properties.gravity = -2000;
	me.properties.radius = 16;
	me.properties.speed = 100;
	me.properties.controller = new Controller();
	me.properties._frame = 0
	me.properties._amount = 0
	
	me.propertyNames.add("speed");
	me.propertyNames.add("gravity");
	me.propertyNames.add("radius");
}

function init(me) {
}

function start(me) {
	if(me.scene().isInDesign()) {
		return;
	}
	
	game.setMouseGrabbed(true);
	
	me.properties.controller.gravity = me.properties.gravity;
	me.properties.controller.speed = me.properties.speed;
	me.properties.controller.collider.radius = me.properties.radius;
	
	me.properties.controller.init(me.scene(), me.node());
}

function update(me) {
	if(me.scene().isInDesign()) {
		return;
	}
	me.properties.controller.update(me.scene());
	
	me.properties._amount += 10 * game.elapsedTime();
	if(me.properties._amount >= 1) {
		me.properties._frame = (me.properties._frame + 1) % 12;
		me.properties._amount = 0
	}
}

function renderSprites(me, renderer) {
	var sprites = game.getAssets().load(IO.file("sprites.png"));
	var sceneRenderer = game.getSceneRenderer();
	
	renderer.beginSprite(sprites);
	renderer.push(
		"FPS = " + game.frameRate() + "\n" +
		"RES = " + Resource.getInstances() + "\n" +
		"TRI = " + sceneRenderer.getTrianglesRendered() + "\n" +
		"TST = " + me.properties.controller.collider.getTested(),
		8, 12, 100, 5, 10, 10, 1, 1, 1, 1
	);
	if(!me.scene().isInDesign()) {
		renderer.push(
			me.properties._frame * 12, 12, 4, 1,
			10, game.h() - 26, 16 * 4, 16,
			1, 1, 1, 1, false
		);
	}
	renderer.endSprite();
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}