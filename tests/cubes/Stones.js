// Stones.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;
var UIEnum = org.game.UIEnum;
var Resource = org.game.Resource;
var SceneRenderer = org.game.SceneRenderer;

function create(me) {
	me.properties.stones = new UIEnum(
		"StoneLava",
		"Stone",
		"Grass",
		"Dirt",
		"Ledge"
	)
}

function init(me) {
}

function start(me) {
}

function update(me) {
	var file;
	var value = me.properties.stones.getValue()
	
	if(value == 0) {
		file = IO.file("stone-lava.obj")
	} else if(value == 1) {
		file = IO.file("stone.obj")
	} else if(value == 2) {
		file = IO.file("grass.obj")
	} else if(value == 3){
		file = IO.file("dirt.obj")
	} else {
		file = IO.file("ledge.obj")
	}
	me.node().renderable = game.getAssets().load(file)
}

function renderSprites(me, renderer) {
	var sceneRenderer = game.getSceneRenderer();

	renderer.beginSprite(game.getAssets().load(IO.file("sprites.png")));
	renderer.push(
		"FPS = " + game.frameRate() + "\n" +
		"RES = " + Resource.getInstances() + "\n" + 
		"TRI = " + sceneRenderer.getTrianglesRendered(), 8, 12, 100, 5, 10, 10, 1, 1, 1, 1
	);
	renderer.endSprite();
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}


