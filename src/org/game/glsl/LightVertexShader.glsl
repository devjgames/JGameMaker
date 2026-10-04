attribute vec3 aPosition;
attribute vec2 aTextureCoordinate;
attribute vec3 aNormal;
attribute vec4 aColor;

varying vec3 vPosition;
varying vec2 vTextureCoordinate;
varying vec3 vNormal;
varying vec4 vColor;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uModel;
uniform mat4 uModelIT;

uniform int uWarpEnabled;
uniform float uWarpAmplitude;
uniform float uWarpFrequency;
uniform float uWarpTime;
uniform int uWarpY;

void main() {
    vec4 position = uModel * vec4(aPosition, 1.0);
    vec3 normal = normalize((uModelIT * vec4(aNormal, 0.0)).xyz);

    if(uWarpEnabled != 0) {
        vec3 base = position.xyz;

        position.x = position.x + uWarpAmplitude * cos(uWarpFrequency * base.z + uWarpTime) * sin(uWarpFrequency * base.y + uWarpTime);
        position.y = position.y + uWarpAmplitude * sin(uWarpFrequency * base.x + uWarpTime) * cos(uWarpFrequency * base.z + uWarpTime) * float(uWarpY);
        position.z = position.z + uWarpAmplitude * sin(uWarpFrequency * base.x + uWarpTime) * sin(uWarpFrequency * base.y + uWarpTime);
    }

    vPosition = position.xyz;
    vTextureCoordinate = aTextureCoordinate;
    vNormal = normal;
    vColor = aColor;

    gl_Position = uProjection * uView * position;
}