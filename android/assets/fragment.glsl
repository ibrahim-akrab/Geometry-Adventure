#ifdef GL_ES
precision lowp float;
precision lowp int;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec4 v_pos;

uniform vec2 u_lightPos[64];
uniform float u_lightIntensity[64];
uniform float u_radius[64];
uniform int u_lightSources;

uniform vec3 u_ambientLight;
uniform float u_ambientIntensity;
uniform float u_time;

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
    vec4 color = (v_color * texture2D(u_texture, v_texCoords));

    float lightIntensity = 0.0;

    for(int i = 0; i < u_lightSources; i++)
    {
        float radius = (v_pos.x - u_lightPos[i].x) * (v_pos.x - u_lightPos[i].x)
                            + (v_pos.y - u_lightPos[i].y) * (v_pos.y - u_lightPos[i].y) +
                            0.5 * rand((6.0 * u_time) * v_pos.xy);
        lightIntensity += u_lightIntensity[i] * u_radius[i]  / ((radius) * (radius) + 1.0);
                //pow(2.718, -radius / u_radius[i]);

    }

    lightIntensity = 1.0/(pow(2.718, -4.0 * lightIntensity + 2.0) + 1.0);
    lightIntensity = float(int(lightIntensity * 4.0)) / 4.0;


    color *= vec4(vec3(lightIntensity + u_ambientIntensity), 1);
    gl_FragColor = color;
}