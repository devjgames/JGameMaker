// Torch.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;
var Particle = org.game.Particle;
var ParticleSystem = org.game.ParticleSystem;
var Random = java.util.Random;

function create(me) {
	me.properties._p = new Particle();
	me.properties._r = new Random(100);
}

function init(me) {
	var node = me.node().getChild(2);
	
	node.renderable = new ParticleSystem(500);
	node.renderable.texture = game.getAssets().load(IO.file("fire.png"));
}

function start(me) {
}

function update(me) {
	me.node().getChild(1).lightRadius = 100 + Math.sin(game.totalTime() * 10) * 10;
	
	var node = me.node().getChild(2);
	var p = me.properties._p;
	var r = me.properties._r;
	var sc = 0.2 + r.nextFloat() * 0.5;
	var ss = 5 + r.nextFloat() * 10;

	p.velocityX = 0;
	p.velocityY = 10 + r.nextFloat() * 20;
	p.velocityZ = 0;
	p.positionX = -1 + r.nextFloat() * 2;
	p.positionY = -1 + r.nextFloat() * 2;
	p.positionZ = -1 + r.nextFloat() * 2;
	p.startX = ss;
	p.startY = ss;
	p.endX = 0.1;
	p.endY = 0.1;
	p.startR = sc;
	p.startG = sc;
	p.startB = sc;
	p.startA = 1;
	p.endR = 0;
	p.endG = 0;
	p.endB = 0;
	p.endA =  1;
	p.lifeSpan = 0.25 + r.nextFloat() * 0.75;
	
	node.renderable.emit(p);
}

function renderSprites(me, renderer) {
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}

