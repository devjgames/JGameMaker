package org.game;

public class Controller {

    public final Collider collider = new Collider();
    public float speed = 100;
    public float gravity = -20000;

    private final Vec3 forward = new Vec3();

    public void init(Scene scene, SceneNode node) {
        scene.eye.set(node.position);
        scene.eye.add(node.r, scene.target);
        scene.up.set(node.u).normalize();
    }

    public void update(Scene scene) throws Exception {
        Game game = Game.getInstance();

        scene.rotateAroundEye(-game.dX(), -game.dY());

        Vec3 direction = scene.calcOffset();

        direction.normalize().negate();
        forward.set(direction);
        forward.y = 0;

        collider.velocity.scale(0, 1, 0);
        if(forward.length() > 0.0000001) {
            forward.normalize();
            if(game.buttonDown(0) || game.buttonDown(1)) {
                float s = (game.buttonDown(0)) ? speed : -speed;

                collider.velocity.add(forward.scale(s));
            }
        }
        collider.velocity.y += gravity * game.elapsedTime();
        collider.resolve(scene, scene.root, scene.eye);
        scene.eye.add(direction, scene.target);
    }
}