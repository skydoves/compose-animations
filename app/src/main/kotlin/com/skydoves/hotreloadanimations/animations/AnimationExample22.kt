/*
 * Original soap-bubble physics + AGSL thin-film shader by Kyriakos Georgiopoulos:
 *   https://gist.github.com/Kyriakos-Georgiopoulos/8d76b6ba97aea70762420bd88ed6dc4f
 *
 * Adapted to a bounded catalog card with all tunables (radii, springs, durations,
 * theme colors, AGSL constants) hoisted into local vals so HotSwan literal patching
 * and `remember(SHADER_SRC)` keying make the whole sample hot-reloadable.
 */
package com.skydoves.hotreloadanimations.animations

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.roundToInt

@Composable
fun AnimationExample22() {
  // 🎨 ONE-FLAG VISUAL SWITCH — change this number, save, watch the bubble morph.
  //   0 = Soap Bubble    (physics-true thin-film rainbow, default)
  //   1 = Neon Magenta   (solid magenta orb)
  //   2 = Slime Green    (slime / jelly orb)
  //   3 = Psychedelic    (rainbow with rotated hue + brighter)
  //   4 = Ghost          (faint white blob)
  //   5 = Fire Orb       (warm orange/red glow)
  //   6 = Cyber Cyan     (electric cyan)
  val LOOK_PRESET = 3

  // Preset table: [interferenceAmount, baseTintR, baseTintG, baseTintB, hueShift]
  val lookValues = remember(LOOK_PRESET) {
    when (LOOK_PRESET) {
      1 -> floatArrayOf(0.0f, 1.0f, 0.0f, 0.8f, 0.0f)
      2 -> floatArrayOf(0.05f, 0.2f, 1.0f, 0.4f, 0.0f)
      3 -> floatArrayOf(1.0f, 0.45f, 0.75f, 1.0f, 2.5f)
      4 -> floatArrayOf(0.4f, 0.95f, 0.95f, 1.0f, 0.0f)
      5 -> floatArrayOf(0.0f, 1.5f, 0.5f, 0.0f, 0.0f)
      6 -> floatArrayOf(0.1f, 0.0f, 1.6f, 1.6f, 0.0f)
      else -> floatArrayOf(1.0f, 0.45f, 0.75f, 1.0f, 0.0f)
    }
  }

  val CARD_HEIGHT_DP = 540f
  val MAX_ORB_RADIUS_DP = 180f
  val MIN_ORB_RADIUS_DP = 88f
  val BOTTOM_ORB_RATIO = 0.88f
  val TOP_ORB_RATIO = 0.22f
  val TEXT_Y_BOTTOM_RATIO = 0.46f
  val TEXT_Y_TOP_RATIO = 0.40f
  val SNAP_UNLOCK_THRESHOLD_PX = 10f
  val DRAG_OVERSHOOT_RATIO = 0.05f

  val DEFORMATION_FACTOR = 0.015f
  val DEFORMATION_CLAMP = 0.6f
  val VELOCITY_SMOOTHING = 0.15f
  val SPRING_STIFFNESS = 1500f
  val SPRING_DAMPING = 34.8f

  val POP_DURATION_MS = 150
  val POP_DELAY_MS = 1500L
  val THEME_REVEAL_DURATION_MS = 1100
  val TEXT_ANIM_DURATION_MS = 700

  val LIGHT_CENTER = Color(0xFFFFFFFF)
  val LIGHT_MID1 = Color(0xFFFBF8F6)
  val LIGHT_MID2 = Color(0xFFF5EFEE)
  val LIGHT_EDGE = Color(0xFFEEEAE8)
  val DARK_CENTER = Color(0xFF2A2D34)
  val DARK_MID = Color(0xFF16171B)
  val DARK_EDGE = Color(0xFF0A0B0D)

  val LIGHT_MAIN_TEXT = Color(0xFF4A403A)
  val DARK_MAIN_TEXT = Color(0xFFE5E5EA)
  val LIGHT_TITLE = Color(0xFF1F1A17)
  val DARK_TITLE = Color(0xFFF5F5F7)
  val LIGHT_SUBTITLE = Color(0xFF8A807A)
  val DARK_SUBTITLE = Color(0xFFA1A1A6)

  val SUN_COLOR = Color(0xFFFDB813)
  val MOON_COLOR = Color(0xFFE5E5EA)

  val SHADER_SRC = """
    // hot-reload trigger: PRESET=$LOOK_PRESET
    uniform shader composable;
    uniform float2 touchCenter;
    uniform float radius;
    uniform float progress;
    uniform float2 deformation;
    uniform float popProgress;
    uniform float sysTime;
    uniform float interferenceAmount;
    uniform float3 baseTint;
    uniform float hueShift;

    float hash(float2 p) {
      return fract(sin(dot(p, float2(12.9898, 78.233))) * 43758.5453);
    }

    float smoothNoise(float2 p) {
      float2 i = floor(p);
      float2 f = fract(p);
      float2 u = f * f * (3.0 - 2.0 * f);
      return mix(mix(hash(i + float2(0.0, 0.0)), hash(i + float2(1.0, 0.0)), u.x),
                 mix(hash(i + float2(0.0, 1.0)), hash(i + float2(1.0, 1.0)), u.x), u.y);
    }

    half4 main(float2 fragCoord) {
      // Tweak these to taste — they hot-reload because the whole shader source
      // is keyed into remember() in the Kotlin side.
      float THICKNESS_BASE = 300.0;
      float THICKNESS_GRAVITY = 120.0;
      float THICKNESS_SWIRL = 100.0;
      float THICKNESS_DETAIL = 40.0;
      float COLOR_INTENSITY = 2.0;
      float EDGE_FADE_END = 0.20;
      float ENV_REFLECTION_STRENGTH = 0.4;
      float ENV_BLUR_RADIUS = 50.0;

      // Look-mix uniforms (interferenceAmount / baseTint / hueShift) are driven
      // from Kotlin via the LOOK_PRESET flag at the top of AnimationExample22.

      half4 rawBackground = composable.eval(fragCoord);
      if (popProgress >= 1.0) return rawBackground;

      float2 rawUv = fragCoord - touchCenter;
      float speed = length(deformation);
      float2 moveDir = speed > 0.001 ? deformation / speed : float2(0.0, 1.0);

      float parallelDist = dot(rawUv, moveDir);
      float2 perpVector = rawUv - moveDir * parallelDist;

      float stretch = 1.0 + speed;
      float squash = 1.0 / sqrt(stretch);

      float2 uv = (moveDir * (parallelDist / stretch)) + (perpVector / squash);
      float dist = length(uv);
      float activeRadius = radius * (1.0 + popProgress * 1.5);

      if (dist >= activeRadius) {
        return rawBackground;
      }

      float2 nUv = uv / activeRadius;
      float distSq = dot(nUv, nUv);
      float z = sqrt(max(0.0, 1.0 - distSq));
      float3 normal = normalize(float3(nUv, z));
      float3 viewDir = float3(0.0, 0.0, 1.0);
      float NdotV = max(0.0, dot(normal, viewDir));

      float magnification = 0.45;
      float lensDeform = (1.0 - z) * magnification * (1.0 - popProgress);

      float2 refUvR = fragCoord - (nUv * activeRadius * (lensDeform * 0.88));
      float2 refUvG = fragCoord - (nUv * activeRadius * (lensDeform * 1.00));
      float2 refUvB = fragCoord - (nUv * activeRadius * (lensDeform * 1.12));

      half3 bgColor = half3(
        composable.eval(refUvR).r,
        composable.eval(refUvG).g,
        composable.eval(refUvB).b
      );

      float3 reflectionDir = reflect(-viewDir, normal);
      float3 lightDir1 = normalize(float3(0.6, 0.7, 0.8));
      float3 lightDir2 = normalize(float3(-0.5, -0.4, 0.6));
      float lightAlign1 = max(0.0, dot(reflectionDir, lightDir1));
      float lightAlign2 = max(0.0, dot(reflectionDir, lightDir2));

      // Thin-film interference. n_film = 1.33 (soapy water), n_air = 1.0.
      float n_film = 1.33;
      float n_air = 1.0;
      float R0 = pow((n_film - n_air) / (n_film + n_air), 2.0);
      float fresnel = R0 + (1.0 - R0) * pow(1.0 - NdotV, 5.0);

      float sinThetaI = sqrt(max(0.0, 1.0 - NdotV * NdotV));
      float sinThetaT = sinThetaI / n_film;
      float cosThetaT = sqrt(max(0.0, 1.0 - sinThetaT * sinThetaT));

      float swirl = smoothNoise(nUv * 3.0 + sysTime * 0.12);
      float thicknessNoise = smoothNoise(nUv * 5.0 - sysTime * 0.08);
      float baseThickness = THICKNESS_BASE + nUv.y * THICKNESS_GRAVITY;
      float thickness = baseThickness
        + swirl * THICKNESS_SWIRL
        + thicknessNoise * THICKNESS_DETAIL;
      thickness = clamp(thickness, 80.0, 900.0);

      float opd = 2.0 * n_film * thickness * cosThetaT;
      float lambda_R = 650.0;
      float lambda_G = 532.0;
      float lambda_B = 450.0;

      float TWO_PI = 6.2831853;
      float oscR = 0.5 + 0.5 * cos(TWO_PI * opd / lambda_R);
      float oscG = 0.5 + 0.5 * cos(TWO_PI * opd / lambda_G);
      float oscB = 0.5 + 0.5 * cos(TWO_PI * opd / lambda_B);

      half3 interferenceColor = half3(oscR, oscG, oscB);

      // Hue shift via YIQ rotation. 0 = unchanged, 2*PI = full wraparound.
      if (abs(hueShift) > 0.001) {
        float Y = dot(float3(interferenceColor), float3(0.299, 0.587, 0.114));
        float I = dot(float3(interferenceColor), float3(0.596, -0.275, -0.321));
        float Q = dot(float3(interferenceColor), float3(0.212, -0.523, 0.311));
        float c = cos(hueShift);
        float s = sin(hueShift);
        float Inew = I * c - Q * s;
        float Qnew = I * s + Q * c;
        interferenceColor = half3(
          Y + 0.956 * Inew + 0.619 * Qnew,
          Y - 0.272 * Inew - 0.647 * Qnew,
          Y - 1.106 * Inew + 1.703 * Qnew
        );
      }

      // Blend toward a solid base tint. Soap bubble at 1.0, neon orb at 0.0.
      half3 lookColor = mix(half3(baseTint), interferenceColor, interferenceAmount);

      float interferenceStrength = smoothstep(0.0, EDGE_FADE_END, NdotV);
      half3 filmReflection = lookColor * fresnel * COLOR_INTENSITY;
      half3 whiteReflection = half3(fresnel);
      half3 thinFilmColor = mix(whiteReflection, filmReflection, interferenceStrength);

      float spec1 = pow(lightAlign1, 250.0) * 2.5;
      float spec2 = pow(lightAlign2, 60.0) * 0.5;
      half3 highlights = half3(spec1 + spec2);

      float2 reflectOffset = normal.xy * ENV_BLUR_RADIUS;
      float2 envCenter = fragCoord + reflectOffset;
      float blurStep = ENV_BLUR_RADIUS * 0.4;
      half3 envSample = composable.eval(envCenter).rgb * 0.4
        + composable.eval(envCenter + float2(blurStep, 0.0)).rgb * 0.15
        + composable.eval(envCenter - float2(blurStep, 0.0)).rgb * 0.15
        + composable.eval(envCenter + float2(0.0, blurStep)).rgb * 0.15
        + composable.eval(envCenter - float2(0.0, blurStep)).rgb * 0.15;
      half3 envReflection = envSample * fresnel * ENV_REFLECTION_STRENGTH;

      float rimShadow = smoothstep(0.92, 1.0, sqrt(distSq));
      bgColor *= (1.0 - rimShadow * 0.25);

      half3 finalColor = bgColor * (1.0 - half3(fresnel))
        + thinFilmColor
        + envReflection
        + highlights;

      float fadeOut = 1.0 - pow(popProgress, 0.5);
      return half4(mix(rawBackground.rgb, finalColor, fadeOut), rawBackground.a);
    }
  """.trimIndent()

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .height(CARD_HEIGHT_DP.dp)
        .padding(horizontal = 12.dp)
        .clip(RoundedCornerShape(20.dp)),
    ) {
      val density = LocalDensity.current
      val widthPx = with(density) { maxWidth.toPx() }
      val heightPx = with(density) { maxHeight.toPx() }
      val maxRadiusPx = with(density) { MAX_ORB_RADIUS_DP.dp.toPx() }
      val minRadiusPx = with(density) { MIN_ORB_RADIUS_DP.dp.toPx() }

      val state = remember(
        widthPx,
        heightPx,
        BOTTOM_ORB_RATIO,
        TOP_ORB_RATIO,
        TEXT_Y_BOTTOM_RATIO,
        TEXT_Y_TOP_RATIO,
        DRAG_OVERSHOOT_RATIO,
      ) {
        BubbleState(
          screenHeightPx = heightPx,
          centerX = widthPx / 2f,
          bottomOrbRatio = BOTTOM_ORB_RATIO,
          topOrbRatio = TOP_ORB_RATIO,
          textYBottomRatio = TEXT_Y_BOTTOM_RATIO,
          textYTopRatio = TEXT_Y_TOP_RATIO,
          dragOvershootRatio = DRAG_OVERSHOOT_RATIO,
        )
      }

      // Smooth size transitions when MAX_ORB_RADIUS_DP / MIN_ORB_RADIUS_DP
      // change at runtime. The shader uniform reads these per frame, so the
      // bubble eases between sizes instead of jumping. Equivalent of
      // animateContentSize but for a shader-driven dimension.
      val radiusSpring = remember {
        spring<Float>(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow)
      }
      val animatedMaxRadiusPx by animateFloatAsState(
        targetValue = maxRadiusPx,
        animationSpec = radiusSpring,
        label = "bubbleMaxRadius",
      )
      val animatedMinRadiusPx by animateFloatAsState(
        targetValue = minRadiusPx,
        animationSpec = radiusSpring,
        label = "bubbleMinRadius",
      )

      val shader = remember(SHADER_SRC) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          RuntimeShader(SHADER_SRC)
        } else {
          null
        }
      }

      val scope = rememberCoroutineScope()
      var isDarkTheme by remember { mutableStateOf(false) }
      var previousIsDark by remember { mutableStateOf(false) }

      val lightBrush =
        remember(widthPx, heightPx, LIGHT_CENTER, LIGHT_MID1, LIGHT_MID2, LIGHT_EDGE) {
          radialBrush(widthPx, heightPx, LIGHT_CENTER, LIGHT_MID1, LIGHT_MID2, LIGHT_EDGE)
        }
      val darkBrush = remember(widthPx, heightPx, DARK_CENTER, DARK_MID, DARK_EDGE) {
        radialBrush(widthPx, heightPx, DARK_CENTER, DARK_MID, DARK_MID, DARK_EDGE)
      }

      val textTween = remember(TEXT_ANIM_DURATION_MS) {
        tween<Color>(TEXT_ANIM_DURATION_MS, easing = FastOutLinearInEasing)
      }
      val mainTextColor by animateColorAsState(
        if (isDarkTheme) DARK_MAIN_TEXT else LIGHT_MAIN_TEXT,
        animationSpec = textTween,
        label = "bubbleMainText",
      )
      val titleColor by animateColorAsState(
        if (isDarkTheme) DARK_TITLE else LIGHT_TITLE,
        animationSpec = textTween,
        label = "bubbleTitle",
      )
      val subtitleColor by animateColorAsState(
        if (isDarkTheme) DARK_SUBTITLE else LIGHT_SUBTITLE,
        animationSpec = textTween,
        label = "bubbleSubtitle",
      )

      DeformationFrameLoop(
        state = state,
        deformationFactor = DEFORMATION_FACTOR,
        deformationClamp = DEFORMATION_CLAMP,
        velocitySmoothing = VELOCITY_SMOOTHING,
        stiffness = SPRING_STIFFNESS,
        damping = SPRING_DAMPING,
      )

      // Smooth color transitions when LOOK_PRESET changes. Each look-mix
      // uniform is wrapped in animateFloatAsState so toggling between presets
      // (soap → magenta → slime ...) eases between palettes instead of
      // snapping. Reading these State<Float> inside the graphicsLayer block
      // means the layer auto-invalidates per animation tick.
      val lookSpring = remember {
        spring<Float>(dampingRatio = 0.85f, stiffness = Spring.StiffnessVeryLow)
      }
      val animInterference = animateFloatAsState(lookValues[0], lookSpring, label = "lookInterference")
      val animTintR = animateFloatAsState(lookValues[1], lookSpring, label = "lookTintR")
      val animTintG = animateFloatAsState(lookValues[2], lookSpring, label = "lookTintG")
      val animTintB = animateFloatAsState(lookValues[3], lookSpring, label = "lookTintB")
      val animHueShift = animateFloatAsState(lookValues[4], lookSpring, label = "lookHueShift")

      val revealClipPath = remember { Path() }

      Box(
        modifier = Modifier
          .fillMaxSize()
          .bubbleDragInput(state, scope, SNAP_UNLOCK_THRESHOLD_PX)
          .bubbleTapInput(state, scope, POP_DURATION_MS, POP_DELAY_MS)
          .bubbleShaderLayer(
            state,
            shader,
            animatedMaxRadiusPx,
            animatedMinRadiusPx,
            animInterference,
            animTintR,
            animTintG,
            animTintB,
            animHueShift,
          )
          .drawBehind {
            drawThemeBackground(
              isDarkTheme = isDarkTheme,
              previousIsDark = previousIsDark,
              revealProgress = state.themeRevealProgress.value,
              lightBrush = lightBrush,
              darkBrush = darkBrush,
              reusablePath = revealClipPath,
            )
          },
      ) {
        ThemeToggleButton(
          isDarkTheme = isDarkTheme,
          sunColor = SUN_COLOR,
          moonColor = MOON_COLOR,
          onToggle = {
            if (state.themeRevealProgress.isRunning) return@ThemeToggleButton
            previousIsDark = isDarkTheme
            isDarkTheme = !isDarkTheme
            scope.launch {
              state.themeRevealProgress.snapTo(0f)
              state.themeRevealProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                  durationMillis = THEME_REVEAL_DURATION_MS,
                  easing = CubicBezierEasing(0.1f, 0.8f, 0.2f, 1.0f),
                ),
              )
            }
          },
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 16.dp, end = 16.dp),
        )

        Text(
          text = "Drag the bubble.",
          fontSize = 22.sp,
          fontWeight = FontWeight.Medium,
          lineHeight = 28.sp,
          color = mainTextColor,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .align(Alignment.Center)
            .offset(y = (-50).dp)
            .graphicsLayer { alpha = 1f - (state.progress * 4).coerceIn(0f, 1f) },
        )

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(x = 0, y = state.textYOffsetPx.roundToInt()) },
          contentAlignment = Alignment.Center,
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .graphicsLayer { alpha = (state.progress * 3).coerceIn(0f, 1f) },
          ) {
            Text(
              text = "Thin film",
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = (-0.5).sp,
              color = titleColor,
            )
            Text(
              text = "Real-time interference\non a kinematic spring.",
              fontSize = 14.sp,
              lineHeight = 18.sp,
              textAlign = TextAlign.Center,
              color = subtitleColor,
              modifier = Modifier.padding(top = 8.dp),
            )
          }
        }
      }

      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        BubbleFallback(state, state.radiusFor(animatedMaxRadiusPx, animatedMinRadiusPx))
      }
    }

    Text(
      "Soap bubble drag with thin-film interference. Tweak Kotlin radii / spring values for the feel, or edit the AGSL constants (THICKNESS_BASE, COLOR_INTENSITY, EDGE_FADE_END...) inside SHADER_SRC for the optics.",
      style = MaterialTheme.typography.labelSmall,
      modifier = Modifier.padding(horizontal = 16.dp),
    )
  }
}

@Stable
private class BubbleState(
  screenHeightPx: Float,
  val centerX: Float,
  bottomOrbRatio: Float,
  topOrbRatio: Float,
  textYBottomRatio: Float,
  textYTopRatio: Float,
  dragOvershootRatio: Float,
) {
  val bottomOrbCenterY = screenHeightPx * bottomOrbRatio
  val topOrbCenterY = screenHeightPx * topOrbRatio
  val midPoint = (bottomOrbCenterY + topOrbCenterY) / 2f
  val maxDragY = bottomOrbCenterY + (screenHeightPx * dragOvershootRatio)

  private val orbRange = bottomOrbCenterY - topOrbCenterY
  private val textYBottom = screenHeightPx * textYBottomRatio
  private val textYTop = screenHeightPx * textYTopRatio

  val bubblePos = Animatable(Offset(centerX, bottomOrbCenterY), Offset.VectorConverter)
  val deformationAnim = Animatable(Offset.Zero, Offset.VectorConverter)
  val popAnim = Animatable(0f)
  val themeRevealProgress = Animatable(1f)
  val shaderTime = floatArrayOf(0f)

  val progress: Float
    get() = ((bottomOrbCenterY - bubblePos.value.y) / orbRange).coerceIn(0f, 1f)

  fun radiusFor(maxPx: Float, minPx: Float): Float = androidx.compose.ui.util.lerp(maxPx, minPx, progress)

  val textYOffsetPx: Float
    get() = androidx.compose.ui.util.lerp(textYBottom, textYTop, progress)

  fun isAtTop(unlockThresholdPx: Float): Boolean = bubblePos.value.y <= topOrbCenterY + unlockThresholdPx
}

private val SnapBackSpring = spring<Offset>(
  dampingRatio = 0.65f,
  stiffness = Spring.StiffnessLow,
)

private val UnlockedSnapSpring = spring<Offset>(
  dampingRatio = 0.45f,
  stiffness = Spring.StiffnessLow,
)

@Composable
private fun DeformationFrameLoop(
  state: BubbleState,
  deformationFactor: Float,
  deformationClamp: Float,
  velocitySmoothing: Float,
  stiffness: Float,
  damping: Float,
) {
  LaunchedEffect(
    state,
    deformationFactor,
    deformationClamp,
    velocitySmoothing,
    stiffness,
    damping,
  ) {
    var previousActualPos = state.bubblePos.value
    val startTime = withFrameNanos { it }
    var lastFrameTime = startTime
    var smoothedVelocity = Offset.Zero
    var defVelocity = Offset.Zero

    while (true) {
      val frameTime = withFrameNanos { it }
      val dt = ((frameTime - lastFrameTime) / 1_000_000_000f).coerceAtMost(0.032f)
      lastFrameTime = frameTime

      state.shaderTime[0] = (frameTime - startTime) / 1_000_000_000f

      val currentActualPos = state.bubblePos.value
      val rawVelocity = currentActualPos - previousActualPos

      smoothedVelocity = Offset(
        x = smoothedVelocity.x + (rawVelocity.x - smoothedVelocity.x) * velocitySmoothing,
        y = smoothedVelocity.y + (rawVelocity.y - smoothedVelocity.y) * velocitySmoothing,
      )

      if (state.popAnim.value == 0f) {
        val targetDeformation = Offset(
          x = (smoothedVelocity.x * deformationFactor).coerceIn(
            -deformationClamp,
            deformationClamp,
          ),
          y = (smoothedVelocity.y * deformationFactor).coerceIn(
            -deformationClamp,
            deformationClamp,
          ),
        )

        val currentDef = state.deformationAnim.value
        val forceX = (targetDeformation.x - currentDef.x) * stiffness - defVelocity.x * damping
        val forceY = (targetDeformation.y - currentDef.y) * stiffness - defVelocity.y * damping

        defVelocity = Offset(defVelocity.x + forceX * dt, defVelocity.y + forceY * dt)
        val nextDef = Offset(currentDef.x + defVelocity.x * dt, currentDef.y + defVelocity.y * dt)

        state.deformationAnim.snapTo(nextDef)
      } else {
        state.deformationAnim.snapTo(Offset.Zero)
        defVelocity = Offset.Zero
      }
      previousActualPos = currentActualPos
    }
  }
}

private fun Modifier.bubbleDragInput(
  state: BubbleState,
  scope: CoroutineScope,
  snapUnlockThresholdPx: Float,
): Modifier = pointerInput(state, snapUnlockThresholdPx) {
  var isUnlocked = false
  detectDragGestures(
    onDragStart = { isUnlocked = state.isAtTop(snapUnlockThresholdPx) },
    onDragEnd = {
      scope.launch {
        if (isUnlocked) {
          if (state.bubblePos.value.y < state.midPoint) {
            state.bubblePos.animateTo(
              Offset(state.centerX, state.topOrbCenterY),
              UnlockedSnapSpring,
            )
          } else {
            state.bubblePos.animateTo(
              Offset(state.centerX, state.bottomOrbCenterY),
              SnapBackSpring,
            )
          }
        } else {
          val targetY = if (state.bubblePos.value.y < state.midPoint) {
            state.topOrbCenterY
          } else {
            state.bottomOrbCenterY
          }
          state.bubblePos.animateTo(Offset(state.centerX, targetY), SnapBackSpring)
        }
      }
    },
  ) { change, dragAmount ->
    if (state.popAnim.value > 0f) return@detectDragGestures
    change.consume()
    val proposedY = state.bubblePos.value.y + dragAmount.y
    if (!isUnlocked && proposedY <= state.topOrbCenterY) isUnlocked = true
    if (isUnlocked) {
      scope.launch {
        state.bubblePos.snapTo(Offset(state.bubblePos.value.x + dragAmount.x, proposedY))
      }
    } else {
      val clampedY = proposedY.coerceAtMost(state.maxDragY)
      scope.launch { state.bubblePos.snapTo(Offset(state.centerX, clampedY)) }
    }
  }
}

private fun Modifier.bubbleTapInput(
  state: BubbleState,
  scope: CoroutineScope,
  popDurationMs: Int,
  popDelayMs: Long,
): Modifier = pointerInput(state, popDurationMs, popDelayMs) {
  detectTapGestures(
    onTap = {
      if (state.popAnim.value == 0f) {
        scope.launch {
          state.popAnim.animateTo(
            1f,
            tween(popDurationMs, easing = FastOutLinearInEasing),
          )
          delay(popDelayMs)
          state.popAnim.snapTo(0f)
          state.bubblePos.snapTo(Offset(state.centerX, state.bottomOrbCenterY))
        }
      }
    },
  )
}

private fun Modifier.bubbleShaderLayer(
  state: BubbleState,
  shader: RuntimeShader?,
  maxRadiusPx: Float,
  minRadiusPx: Float,
  interferenceAmount: State<Float>,
  tintR: State<Float>,
  tintG: State<Float>,
  tintB: State<Float>,
  hueShift: State<Float>,
): Modifier = graphicsLayer {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && shader != null) {
    shader.setFloatUniform("touchCenter", state.bubblePos.value.x, state.bubblePos.value.y)
    shader.setFloatUniform("radius", state.radiusFor(maxRadiusPx, minRadiusPx))
    shader.setFloatUniform("progress", state.progress)
    shader.setFloatUniform(
      "deformation",
      state.deformationAnim.value.x,
      state.deformationAnim.value.y,
    )
    shader.setFloatUniform("popProgress", state.popAnim.value)
    shader.setFloatUniform("sysTime", state.shaderTime[0])
    shader.setFloatUniform("interferenceAmount", interferenceAmount.value)
    shader.setFloatUniform("baseTint", tintR.value, tintG.value, tintB.value)
    shader.setFloatUniform("hueShift", hueShift.value)

    renderEffect = RenderEffect.createRuntimeShaderEffect(shader, "composable")
      .asComposeRenderEffect()
  }
}

@Composable
private fun ThemeToggleButton(
  isDarkTheme: Boolean,
  sunColor: Color,
  moonColor: Color,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val progress by animateFloatAsState(
    targetValue = if (isDarkTheme) 1f else 0f,
    animationSpec = spring(dampingRatio = 0.75f, stiffness = 300f),
    label = "themeMorph",
  )

  val scaleAnim = remember { Animatable(1f) }
  LaunchedEffect(isDarkTheme) {
    scaleAnim.snapTo(0.85f)
    scaleAnim.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 400f))
  }

  val mainPath = remember { Path() }
  val cutoutPath = remember { Path() }
  val finalPath = remember { Path() }

  Canvas(
    modifier = modifier
      .size(40.dp)
      .graphicsLayer {
        scaleX = scaleAnim.value
        scaleY = scaleAnim.value
      }
      .clip(CircleShape)
      .clickable(onClick = onToggle)
      .padding(6.dp),
  ) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val maxRadius = size.width / 2f

    val currentColor = androidx.compose.ui.graphics.lerp(sunColor, moonColor, progress)

    rotate(degrees = progress * -90f, pivot = center) {
      val rayAlpha = (1f - progress * 2.5f).coerceIn(0f, 1f)
      if (rayAlpha > 0f) {
        val rayLength = maxRadius * 0.25f
        val rayOffset = maxRadius * 0.6f
        for (i in 0 until 8) {
          rotate(degrees = i * 45f, pivot = center) {
            drawLine(
              color = currentColor.copy(alpha = rayAlpha),
              start = center.copy(y = center.y - rayOffset),
              end = center.copy(y = center.y - rayOffset - rayLength),
              strokeWidth = maxRadius * 0.15f,
              cap = StrokeCap.Round,
            )
          }
        }
      }

      val sunRadius = maxRadius * 0.45f
      val moonRadius = maxRadius * 0.85f
      val currentRadius = sunRadius + (moonRadius - sunRadius) * progress

      mainPath.reset()
      mainPath.addOval(
        Rect(
          left = center.x - currentRadius,
          top = center.y - currentRadius,
          right = center.x + currentRadius,
          bottom = center.y + currentRadius,
        ),
      )

      val cutoutStartOffset = Offset(center.x + maxRadius * 2f, center.y - maxRadius * 2f)
      val cutoutEndOffset =
        Offset(center.x + currentRadius * 0.3f, center.y - currentRadius * 0.3f)
      val cutoutX = cutoutStartOffset.x + (cutoutEndOffset.x - cutoutStartOffset.x) * progress
      val cutoutY = cutoutStartOffset.y + (cutoutEndOffset.y - cutoutStartOffset.y) * progress
      val cutoutRadius = currentRadius * 0.95f

      cutoutPath.reset()
      cutoutPath.addOval(
        Rect(
          left = cutoutX - cutoutRadius,
          top = cutoutY - cutoutRadius,
          right = cutoutX + cutoutRadius,
          bottom = cutoutY + cutoutRadius,
        ),
      )

      finalPath.reset()
      finalPath.op(mainPath, cutoutPath, PathOperation.Difference)

      drawPath(path = finalPath, color = currentColor)
    }
  }
}

@Composable
private fun BubbleFallback(state: BubbleState, currentRadius: Float) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color.White.copy(alpha = 0.6f), Color.Transparent),
        center = state.bubblePos.value,
        radius = currentRadius,
      ),
    )
  }
}

private fun radialBrush(
  screenWidthPx: Float,
  screenHeightPx: Float,
  center: Color,
  mid1: Color,
  mid2: Color,
  edge: Color,
): Brush = Brush.radialGradient(
  0.0f to center,
  0.3f to mid1,
  0.7f to mid2,
  1.0f to edge,
  center = Offset(screenWidthPx / 2f, screenHeightPx * 0.4f),
)

private fun DrawScope.drawThemeBackground(
  isDarkTheme: Boolean,
  previousIsDark: Boolean,
  revealProgress: Float,
  lightBrush: Brush,
  darkBrush: Brush,
  reusablePath: Path,
) {
  val currentBrush = if (isDarkTheme) darkBrush else lightBrush
  val prevBrush = if (previousIsDark) darkBrush else lightBrush

  drawRect(brush = prevBrush)

  if (revealProgress < 1f) {
    val maxRadius = hypot(size.width, size.height)
    val currentRevealRadius = revealProgress * maxRadius
    val epicenter = Offset(size.width - 60f, 60f)

    reusablePath.reset()
    reusablePath.addOval(
      Rect(
        left = epicenter.x - currentRevealRadius,
        top = epicenter.y - currentRevealRadius,
        right = epicenter.x + currentRevealRadius,
        bottom = epicenter.y + currentRevealRadius,
      ),
    )

    clipPath(reusablePath) {
      drawRect(brush = currentBrush)
    }
  } else {
    drawRect(brush = currentBrush)
  }
}
