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
	me.properties._rand = new Random(100);
	me.properties._p = new Particle();
}

function init(me) {
	me.node().getChild(2).renderable = new ParticleSystem(500);
	me.node().getChild(2).zOrder = 100;
	me.node().getChild(2).depthState = DepthState.READONLY;
	me.node().getChild(2).blendState = BlendState.ADDITIVE;
	me.node().getChild(2).cullState = org.game.CullState.NONE;
	me.node().getChild(2).receivesLight = false;
	me.node().getChild(2).renderable.texture = game.getAssets().load(IO.file("fire.png"));
}

function start(me) {
}

function update(me) {
	me.node().getChild(1).lightRadius = 85 + Math.sin(game.totalTime() * 8) * 5;
	
	var rand = me.properties._rand;
	var sc = 0.1 + rand.nextFloat() * 0.2;
	var ss = 5 + rand.nextFloat() * 5;
	var p = me.properties._p;
	
	p.startR = sc;
	p.startG = sc;
	p.startB = sc;
	p.startA = 1;
	p.endR = 0;
	p.endG = 0;
	p.endB = 0;
	p.endA = 1;
	p.startX = ss;
	p.startY = ss;
	p.endX = 0.1;
	p.endY = 0.1;
	p.positionX = -1 + rand.nextFloat() * 2;
	p.positionY = -4 + rand.nextFloat() * 2;
	p.positionZ = -1 + rand.nextFloat() * 2;
	p.velocityX = 0;
	p.velocityY = 5 + rand.nextFloat() * 5;
	p.velocityZ = 0;
	p.lifeSpan = 0.5 + rand.nextFloat() * 1.5;
	
	me.node().getChild(2).renderable.emit(p);
}

function renderSprites(me, renderer) {
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}

