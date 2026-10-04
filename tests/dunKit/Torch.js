// Torch.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;
var Particle = org.game.Particle;
var ParticleSystem = org.game.ParticleSystem;
var Random = java.util.Random;
var DepthState = org.game.DepthState;
var BlendState = org.game.BlendState;

function create(me) {
	me.properties._random = new Random(100);
	me.properties._particle = new Particle();
}

function init(me) {
	me.node().renderable = new ParticleSystem(500);
	me.node().depthState = DepthState.READONLY;
	me.node().blendState = BlendState.ADDITIVE;
	me.node().zOrder = 100;
	me.node().renderable.texture = game.getAssets().load(IO.file("fire.png"));
	me.node().receivesLight = false;
	me.node().scale.set(0.5, 0.5, 0.5);
}

function start(me) {
}

function update(me) {
	var particles = me.node().renderable;
	var p = me.properties._particle;
	var rand = me.properties._random;
	var sc = 0.5 + rand.nextFloat() * 0.5;
	var ss = 10 + rand.nextFloat() * 20;
	
	p.velocityX = 0;
	p.velocityY = 10 + rand.nextFloat() * 20;
	p.velocityZ = 0;
	p.positionX = -2 + rand.nextFloat() * 4;
	p.positionY = -2 + rand.nextFloat() * 4;
	p.positionZ = -2 + rand.nextFloat() * 4;
	p.startR = p.startG = p.startB = sc;
	p.startA = 1
	p.endR = p.endG = p.endB = 0;
	p.endA = 1;
	p.startX = p.startY = ss;
	p.endX = p.endY = 0.1;
	p.lifeSpan = 0.5 + rand.nextFloat() * 1.5;
	
	particles.emit(p);
}

function renderSprites(me, renderer) {
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}

