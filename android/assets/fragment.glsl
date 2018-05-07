#ifdef GL_ES
precision mediump float;
precision mediump int;

#endif

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec4 v_pos;

uniform vec2 u_lightPos[10];
uniform vec3 u_lightColor[10];
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
    vec4 light = vec4(u_ambientLight * u_ambientIntensity, 1);
    vec4 maxLight = vec4(0.f,0.f,0.f,0.f);
    vec4 tmp = vec4(0.f,0.f,0.f,0.f);

    for(int i = 0; i < u_lightSources; i++)
    {
        float radius = (v_pos.x - u_lightPos[i].x) * (v_pos.x - u_lightPos[i].x)
                            + (v_pos.y - u_lightPos[i].y) * (v_pos.y - u_lightPos[i].y);
        if(radius < u_radius[i].x + 4.f * rand(v_pos.xy * t))
            tmp = vec4(u_lightColor[i], 0);
        else if(radius < u_radius[i].y + 4.f * rand(v_pos.xy * t))
             tmp = vec4(0.5f * u_lightColor[i], 0);
        else if(radius < u_radius[i].y * 2.f + 4.f * rand(v_pos.xy * t))
                     tmp = vec4(0.25f * u_lightColor[i], 0);
        if(tmp.x > maxLight.x) maxLight.x = tmp.x;
        if(tmp.y > maxLight.y) maxLight.y = tmp.y;
        if(tmp.z > maxLight.z) maxLight.z = tmp.z;
        tmp = vec4(0.f,0.f,0.f,0.f);
    }

    light += maxLight;
    color *= light;
    gl_FragColor = color;
}