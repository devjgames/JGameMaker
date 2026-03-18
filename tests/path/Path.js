// Path.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;
var Resource = org.game.Resource;
var ParticleSystem = org.game.ParticleSystem;
var Particle = org.game.Particle;
var Random = java.util.Random;
var Math = java.lang.Math;

function create(me) {
	me.properties._i = 0;
	me.properties._j = 1;
	me.properties._d = 1;
}

function init(me) {
	if(me.scene().isInDesign()) {
		return;
	}
	
	var particles = new ParticleSystem(500);
	
	me.node().getChild(2).renderable = particles;
	particles.texture = game.getAssets().load(IO.file("smoke.png"));
	
	me.properties._particle = new Particle();
	me.properties._random = new Random(100);
}

function start(me) {
	if(me.scene().isInDesign()) {
		return;
	}
	
	var p = me.node().getChild(1);
	var c = me.node().getChild(0);
	var s = p.getChild(0).position;
	
	c.position.set(s);
}

function update(me) {
	if(me.scene().isInDesign()) {
		return;
	}
	
	var p = me.node().getChild(1);
	var c = me.node().getChild(0);
	var s = p.getChild(me.properties._i).position;
	var e = p.getChild(me.properties._j).position; 
	
	if(c.move(s, e, 50)) {
		if(me.properties._d == 1) {
			if(me.properties._i == p.getChildCount() - 2) {
				me.properties._i = p.getChildCount() - 1;
				me.properties._j = p.getChildCount() - 2;
				me.properties._d = -1;
			} else {
				me.properties._i += 1;
				me.properties._j += 1;
			}
		} else {
			if(me.properties._i == 1) {
				me.properties._i = 0;
				me.properties._j = 1;
				me.properties._d = 1;
			} else {
				me.properties._i -= 1;
				me.properties._j -= 1;
			}
		}
	}
	c.getChild(0).rotate(2, -90 * game.elapsedTime());
	
	s = p.getChild(me.properties._i).position;
	e = p.getChild(me.properties._j).position; 
	
	var particles = me.node().getChild(2).renderable;
	var dx = s.x - e.x;
	var dz = s.z - e.z;
	var len = Math.sqrt(dx * dx + dz * dz)
	
	particles.emitPosition.set(c.position).add(dx / len * 18, 16, dz / len * 18);
	
	for(var i = 0; i != 3; i++) {
		var p = me.properties._particle;
		var r = me.properties._random;
		var sa = 0.5 + r.nextFloat() * 0.5;
		var ea = 0;
		var ss = 20 + r.nextFloat() * 40;
		var es = 0.1;
		
		p.startA = sa;
		p.startR = 1;
		p.startG = 1;
		p.startB =  1;
		p.endA = ea;
		p.endR = 1;
		p.endG = 1;
		p.endB = 1;
		p.startX = ss;
		p.startY = ss;
		p.endX = es;
		p.endY = es;
		p.velocityX = -5 + r.nextFloat() * 10;
		p.velocityY = -5 + r.nextFloat() * 10;
		p.velocityZ = -5 + r.nextFloat() * 10;
		p.positionX = 0;
		p.positionY = 0;
		p.positionZ = 0;
		p.lifeSpan = 0.5 + r.nextFloat() *  1.5;
	
		particles.emit(p);
	}
}

function renderSprites(me, renderer) {
	var font = game.getAssets().load(IO.file("sprites.png"));
	var sceneRenderer = game.getSceneRenderer();
	
	renderer.beginSprite(font);
	renderer.push(
		"FPS = " + game.frameRate() + "\n" +
		"RES = " + Resource.getInstances() + "\n" +
		"TRI = " + sceneRenderer.getTrianglesRendered(),
		8, 12, 100, 5, 10, 10, 1, 1, 1, 1
	);
	renderer.endSprite();
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}

