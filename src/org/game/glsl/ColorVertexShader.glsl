attribute vec3 aPosition;
attribute vec4 aColor;

varying vec4 vColor;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uModel;

void main() {
    vColor = aColor;

    gl_Position = uProjection * uView * uModel * vec4(aPosition, 1.0);
}