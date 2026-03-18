#define MAX_LIGHTS 6

varying vec3 vPosition;
varying vec2 vTextureCoordinate;
varying vec3 vNormal;
varying vec4 vColor;

uniform sampler2D uTexture;
uniform int uTextureEnabled;

uniform sampler2D uDecalTexture;
uniform int uDecalTextureEnabled;

uniform vec3 uLightPosition[MAX_LIGHTS];
uniform vec4 uLightColor[MAX_LIGHTS];
uniform float uLightRadius[MAX_LIGHTS];

uniform int uLightCount;

uniform int uLightingEnabled;

uniform vec4 uAmbientColor;
uniform vec4 uDiffuseColor;
uniform vec4 uSpecularColor;
uniform float uSpecularPower;

uniform vec3 uEye;

void main() {
    vec4 color = vColor;

    if(uLightingEnabled != 0) {
        vec3 normal = normalize(vNormal);
        vec3 position = vPosition;
        vec3 viewNormal = normalize(uEye - position);
        
        color = vColor * uAmbientColor;

        for(int i = 0; i != uLightCount; i++) {
            vec3 offset = uLightPosition[i] - position;
            vec3 lightNormal = normalize(offset);
            vec3 relfectedNormal = reflect(-lightNormal, normal);
            float lDotN = clamp(dot(lightNormal, normal), 0.0, 1.0);
            float atten = 1.0 - clamp(length(offset) / uLightRadius[i], 0.0, 1.0);
            float spec = clamp(dot(relfectedNormal, viewNormal), 0.0, 1.0);

            color += atten * (lDotN * vColor * uDiffuseColor + pow(spec, uSpecularPower) * uSpecularColor) * uLightColor[i];
        }
    }

    if(uTextureEnabled != 0) {
        color *= texture2D(uTexture, vTextureCoordinate);
    }
    if(uDecalTextureEnabled != 0) {
        vec4 d = texture2D(uDecalTexture, vTextureCoordinate);

        color.rgb = (1.0 - d.a) * color.rgb + d.a * d.rgb;
    }
    gl_FragColor = color;
}
