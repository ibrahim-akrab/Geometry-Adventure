#ifdef GL_ES
precision lowp float;
precision lowp int;

#endif

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec4 v_pos;

uniform vec2 u_lightPos[10];
uniform float u_lightIntensity[10];
uniform vec2 u_radius[10];
uniform int u_lightSources;

uniform vec3 u_ambientLight;
uniform float u_ambientIntensity;

uniform int u_time;

uniform sampler2D u_texture;

float rand(vec2 co)
{
    float a = 12.9898;
    float b = 78.233;
    float c = 43758.5453;
    float dt = dot(co.xy ,vec2(a,b));
    float sn = mod(dt,3.14);
    return fract(sin(sn) * c);
}

void main()
{
    float t = float(u_time);
    vec4 color = (v_color * texture2D(u_texture, v_texCoords));
    vec3 light = u_ambientLight * u_ambientIntensity;

    float lightIntensity = 0.f;
    for(int i = 0; i < u_lightSources; i++)
    {
        float radius = (v_pos.x - u_lightPos[i].x) * (v_pos.x - u_lightPos[i].x)
                            + (v_pos.y - u_lightPos[i].y) * (v_pos.y - u_lightPos[i].y) +
                             2.f * rand((6.f * t) * v_pos.xy);
        lightIntensity += u_lightIntensity[i] * pow(2.718, -radius / u_radius[i].x);
    }

    if(lightIntensity > 1.f) lightIntensity = 1.f;
    lightIntensity = float(int(lightIntensity * 4.f)) / 4.f;

    color *= vec4(vec3(lightIntensity + u_ambientIntensity), 1);
    gl_FragColor = color;
}