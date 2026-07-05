// Player.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;
var Controller = org.game.Controller;
var Resource = org.game.Resource;
var Keys = org.game.Keys;
var State = org.game.State;
var AssetManager = org.game.AssetManager;
var SceneNode = org.game.SceneNode;
var Particle = org.game.Particle;
var ParticleSystem = org.game.ParticleSystem;
var Random = java.util.Random;
var DepthState = org.game.DepthState;
var BlendState = org.game.BlendState;
var CullState = org.game.CullState;
var Vec3 = org.game.Vec3;
var FloatArray = Java.type("float[]");
var Triangle = org.game.Triangle;

function create(me) {
	me.properties.gravity = -2000;
	me.properties.radius = 16;
	me.properties.speed = 100;
	me.properties.controller = new Controller();
	me.properties._frame = 0
	me.properties._amount = 0
	me.properties._loadName = "";
	me.properties._down = false;
	me.properties._rand = new Random(100);
	me.properties._p = new Particle();
	me.properties._o = new Vec3();
	me.properties._d = new Vec3();
	me.properties._t = new FloatArray(1);
	me.properties._h = new Triangle();
	me.properties._c = 2.0;
	me.properties._c2 = -1.0;
	
	me.propertyNames.add("speed");
	me.propertyNames.add("gravity");
	me.propertyNames.add("radius");
}

function init(me) {
	if(State.properties.get("down") == null) {
		State.properties.put("down", false);
	}
}

function start(me) {
	if(me.scene().isInDesign()) {
		return;
	}
	
	me.properties._node = me.scene().root.find(function(n) {
		if(n.name == "smoke") {
			return true;
		}
		return false;
	})
	
	game.setMouseGrabbed(true);
	
	me.properties.controller.gravity = me.properties.gravity;
	me.properties.controller.speed = me.properties.speed;
	me.properties.controller.collider.radius = me.properties.radius;
	
	me.properties.controller.init(me.scene(), me.node());
	
	var node = me.properties._node;
	
	node.renderable = new ParticleSystem(500);
	node.depthState = DepthState.READONLY;
	node.blendState = BlendState.ALPHA;
	node.cullState = CullState.NONE;
	node.zOrder = 200;
	node.renderable.texture = game.getAssets().load(IO.file("smoke.png"));
	node.receivesLight = true;
	node.ambientColor.set(0.5, 0.5, 0.5, 1);
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
	if(game.keyDown(Keys.KEY_SPACE)) {
		if(!State.properties.get("down")) {
			State.properties.put("down", true);
			
			var name = me.scene().getSceneName();
			var i = 1;
			
			while(true) {
				var n = "scene" + i;
				
				if(n == name) {
					n = "scene" + (i + 1);
					
					var f = IO.file(AssetManager.getRoot(), n + ".scn");
					
					if(!f.exists()) {
						n = "scene1";
					}
					me.properties._loadName = n;
					
					break;
				}
				i += 1
			}
		}
	} else {
		State.properties.put("down", false);
	}
	if(game.keyDown(Keys.KEY_F)) {
		if(!me.properties._down) {
			me.properties._down = true;
			
			if(me.properties._c > 0.5) {
				var o = me.properties._o;
				var d = me.properties._d;
				var t = me.properties._t;
				
				t[0] = 999999;
				o.set(me.scene().eye);
				me.scene().target.sub(o, d).normalize();
				
				if(me.properties.controller.collider.intersect(
					me.scene(),
					me.scene().root,
					o,
					d,
					1.0,
					1,
					t,
					false,
					me.properties._h
				)) {
					var sound = game.getAssets().load(IO.file("fire.wav"));
					
					d.scale(t[0]).add(o, o);
					
					me.properties._c = 0.0;
					me.properties._c2 = 1;
				
					sound.play(false);
				}
			}
		}
	} else {
		me.properties._down = false;
	}
	
	me.properties._c += game.elapsedTime();
	
	if(me.properties._c2 > 0.0) {
		var node = me.properties._node;
		var rand = me.properties._rand;
		var p = me.properties._p;
		var o = me.properties._o;
		var n = me.properties._h.n;
		
		var sa = 0.1 + rand.nextFloat() * 0.2;
		var ss = 10 + rand.nextFloat() * 20;
		
		p.startR = 1;
		p.startG = 1;
		p.startB = 1;
		p.startA = sa;
		p.endR = 1;
		p.endG = 1;
		p.endB = 1;
		p.endA = 0;
		p.startX = ss;
		p.startY = ss;
		p.endX = 0.1;
		p.endY = 0.1;
		p.positionX = o.x;
		p.positionY = o.y;
		p.positionZ = o.z;
		p.velocityX = (20 + rand.nextFloat() * 40) * n.x;
		p.velocityY = (20 + rand.nextFloat() * 40) * n.y;
		p.velocityZ = (20 + rand.nextFloat() * 40) * n.z;
		p.lifeSpan = 0.25 + rand.nextFloat() * 0.75;
		
		node.renderable.emit(p);
		
		me.properties._c2 -= game.elapsedTime();
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
		"TST = " + me.properties.controller.collider.getTested() + "\n" +
		"SPC = Reload",
		8, 12, 100, 5, 10, 10, 1, 1, 1, 1
	);
	if(!me.scene().isInDesign()) {
		renderer.push(10, 3, 1, 1, game.w() / 2 - 8, game.h() / 2 - 1, 16, 2, 1, 1, 1, 1, false);
		renderer.push(10, 3, 1, 1, game.w() / 2 - 1, game.h() / 2 - 8, 2, 16, 1, 1, 1, 1, false);
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
	if(me.properties._loadName != "") {
    	return me.properties._loadName;
    }
    return null;
}