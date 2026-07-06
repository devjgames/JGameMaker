// Export.js
//

var game = org.game.Game.getInstance();
var IO = org.game.IO;
var Log = org.game.Log;
var AssetManager = org.game.AssetManager;
var UIButton = org.game.UIButton;

function create(me) {
	me.properties.export = new UIButton("Export", 
	function(me) {
		var bnode = me.scene().root.find(function(node) {
			if(node.name == "brushes") {
				return true;
			}
			return false;
		});
		if(bnode != null) {
			for(var i = 0; i != bnode.getChildCount(); i++) {
				var b = bnode.getChild(i);
				var text = "";
				
				for(var j = 0; j != b.getChildCount(); j++) {
					var n = b.getChild(j);
				
					text += n.name + " " + n.position.x + " " + n.position.y + " " + n.position.z + " ";
					text += n.r.x + " " + n.r.y + " " + n.r.z + " ";
					text += n.u.x + " " + n.u.y + " " + n.u.z + " ";
					text += n.f.x + " " + n.f.y + " " + n.f.z + "\n";
				}
				Log.put(1, "exporting '" + b.name + "' ...")
				IO.writeAllBytes(text.getBytes(), IO.file(AssetManager.getRoot(), b.name + ".txt"));
			}
		} else {
			Log.put(0, "brushes node not found");
		}
	});
}

function init(me) {
}

function start(me) {
}

function update(me) {
}

function renderSprites(me, renderer) {
}

function receiveMessage(me, component, type) {
}

function loadSceneName(me) {
    return null;
}

