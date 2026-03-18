// Tree.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;
var SceneNode = org.game.SceneNode;
var UIButton = org.game.UIButton;
var UI = org.game.UI;
var Resource = org.game.Resource;

function create(me) {
	me.properties._build = false;
	me.properties.build = new UIButton("Build", function (me) {
		me.properties._build = true;
	})
}

function init(me) {	
}

function start(me) {
}

function update(me) {
	if(me.properties._build) {
		me.properties._build = false;
		me.node().detachAllChildren();
		me.node().scale.set(1, 1, 1);
		tree(me, me.node(), true, 25, 0);
		UI.rebuildTree();
	}
}

function renderSprites(me, renderer) {
	var sceneRenderer = game.getSceneRenderer();
	
	renderer.beginSprite(game.getAssets().load(IO.file("sprites.png")));
	renderer.push(
		"FPS = " + game.frameRate() + "\n" +
		"RES = " + Resource.getInstances() + "\n" +
		"TRI = " + sceneRenderer.getTrianglesRendered(), 
		8, 12, 100, 5, 10, 10, 1, 1, 1, 1);
	renderer.endSprite();
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}

function tree(me, parent, zrot, length, depth) {
	var sphereNode1 = new SceneNode();
	var sphereNode2 = new SceneNode();
	var cylinderNode = new SceneNode();
	
	sphereNode1.renderable = game.getAssets().load(IO.file("sphere.obj"));
	sphereNode2.renderable = game.getAssets().load(IO.file("sphere.obj"));
	cylinderNode.renderable = game.getAssets().load(IO.file("cylindar.obj"));
	parent.addChild(sphereNode1);
	parent.addChild(sphereNode2);
	parent.addChild(cylinderNode);
	sphereNode2.position.set(0, length, 0);
	cylinderNode.scale.set(1, length, 1);
	
	var amount = depth / 5;
	var r1 = 0.5;
	var g1 = 0.25;
	var b1 = 0;
	var r2 = 0;
	var g2 = 0.5;
	var b2 = 0;
	var r = r1 + amount * (r2 - r1);
	var g = g1 + amount * (g2 - g1);
	var b = b1 + amount * (b2 - b1);
		
	sphereNode1.ambientColor.set(r, g, b, 1);
	sphereNode1.diffuseColor.set(r, g, b, 1);
	sphereNode2.ambientColor.set(r, g, b, 1);
	sphereNode2.diffuseColor.set(r, g, b, 1);
	cylinderNode.ambientColor.set(r, g, b, 1);
	cylinderNode.diffuseColor.set(r, g, b, 1);
	
	cylinderNode.specularColor.set(2, 2, 2, 1);
	sphereNode1.specularColor.set(2, 2, 2, 1);
	sphereNode2.specularColor.set(2, 2, 2, 1);
	
	if(depth < 5) {
		var node1 = new SceneNode();
		var node2 = new SceneNode();
		
		if(zrot) {
			node1.rotate(2, -30);
			node2.rotate(2, 30);
		} else {
			node1.rotate(0, -30);
			node2.rotate(0, 30);
		}
		node1.position.set(0, length, 0);
		node2.position.set(0, length, 0);
		
		parent.addChild(node1);
		parent.addChild(node2);
		tree(me, node1, !zrot, length * 0.75, depth + 1);
		tree(me, node2, !zrot, length * 0.75, depth + 1);
	}
}

