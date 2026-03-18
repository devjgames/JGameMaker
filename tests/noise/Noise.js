// Noise.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;
var Random = java.util.Random;
var Vec3 = org.game.Vec3;
var Vec2 = org.game.Vec2;
var UIButton = org.game.UIButton;
var UI = org.game.UI;
var SceneNode = org.game.SceneNode;
var DepthState = org.game.DepthState;
var CullState = org.game.CullState;
var BlendState = org.game.BlendState;
var Resource = org.game.Resource;

function create(me) {
	me.properties.color1 = new Vec3(1, 0, 0);
	me.properties.color2 = new Vec3(1, 1, 0);
	me.properties.alpha1 = 0.001;
	me.properties.alpha2 = 0.005;
	me.properties.size1 = new Vec2(10, 10);
	me.properties.size2 = new Vec2(100, 100);
	me.properties.pos1 = new Vec2(-100, -100);
	me.properties.pos2 = new Vec2(100, 100);
	me.properties.angle1 = -90;
	me.properties.angle2 = 90;
	me.properties.seed = 100;
	me.properties.count = 1000;
	me.properties.vel1 = -45;
	me.properties.vel2 = 45;
	me.properties.build = new UIButton("Build", function (me) {
		me.properties._build = true;
	});
	me.properties._build = false;
	
	me.propertyNames.add("color1");
	me.propertyNames.add("color2");
	me.propertyNames.add("alpha1");
	me.propertyNames.add("alpha2");
	me.propertyNames.add("size1");
	me.propertyNames.add("size2");
	me.propertyNames.add("pos1");
	me.propertyNames.add("pos2");
	me.propertyNames.add("angle1");
	me.propertyNames.add("angle2");
	me.propertyNames.add("seed");
	me.propertyNames.add("count");
	me.propertyNames.add("vel1");
	me.propertyNames.add("vel2");
	me.propertyNames.add("build");
}

function init(me) {
}

function start(me) {
}

function update(me) {
	var random = new Random(me.properties.seed);
		
	if(me.properties._build) {
		me.properties._build = false;
		
		me.node().detachAllChildren();
		
		var plane = game.getAssets().load(IO.file("plane.obj"));
		var color = new Vec3();
		
		for(var i = 0; i < me.properties.count; i++) {
			var alpha = me.properties.alpha1 + random.nextFloat() * (me.properties.alpha2 - me.properties.alpha1);
			var sx = me.properties.size1.x + random.nextFloat() * (me.properties.size2.x - me.properties.size1.x);
			var sz = me.properties.size1.y + random.nextFloat() * (me.properties.size2.y - me.properties.size1.y);
			var px = me.properties.pos1.x + random.nextFloat() * (me.properties.pos2.x - me.properties.pos1.x);
			var pz = me.properties.pos1.y + random.nextFloat() * (me.properties.pos2.y - me.properties.pos1.y);
			var angle = me.properties.angle1 + random.nextFloat() * (me.properties.angle2 - me.properties.angle1);
			var node = new SceneNode();
			
			me.properties.color1.lerp(me.properties.color2, random.nextFloat(), color);
			
			node.scale.set(sx, 1, sz);
			node.position.set(px, 0, pz);
			node.renderable = plane;
			node.ambientColor.set(color.x, color.y, color.z, alpha);
			node.rotate(1, angle);
			node.depthState = DepthState.NONE;
			node.blendState = BlendState.ALPHA;
			node.cullState = CullState.NONE;
			
			me.node().addChild(node);
		}
		
		UI.rebuildTree();
	}
	for(var i = 0; i < me.node().getChildCount(); i++) {
		var node = me.node().getChild(i);
		
		if(!node.properties.containsKey("velocity")) {
			node.properties.velocity = me.properties.vel1 + random.nextFloat() * (me.properties.vel2 - me.properties.vel1);
		}
		
		var vel = node.properties.velocity;
		
		node.rotate(1, vel * game.elapsedTime());
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

