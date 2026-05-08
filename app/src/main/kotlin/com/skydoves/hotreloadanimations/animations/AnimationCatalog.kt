/*
 * Designed and developed by 2026 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skydoves.hotreloadanimations.animations

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class AnimationEntry(
  val id: Int,
  val title: String,
  val subtitle: String,
  val accent: Color,
  val content: @Composable () -> Unit,
)

object AnimationCatalog {
  val entries: List<AnimationEntry> = listOf(
    AnimationEntry(
      id = 1,
      title = "Animate Content Size",
      subtitle = "Expandable card. Tweak duration and easing in the source, watch the unfold change feel.",
      accent = Color(0xFFEF5350),
      content = { AnimationExample1() },
    ),
    AnimationEntry(
      id = 2,
      title = "Animated Visibility",
      subtitle = "Enter / exit transitions. Swap slideIn directions, fade durations, scale origins.",
      accent = Color(0xFFAB47BC),
      content = { AnimationExample2() },
    ),
    AnimationEntry(
      id = 3,
      title = "Color State Morph",
      subtitle = "animateColorAsState across themes. Change palette colors and animation spec live.",
      accent = Color(0xFF7E57C2),
      content = { AnimationExample3() },
    ),
    AnimationEntry(
      id = 4,
      title = "FAB Spring Morph",
      subtitle = "Float / Dp animateAsState with Spring physics. Change stiffness, dampingRatio.",
      accent = Color(0xFF42A5F5),
      content = { AnimationExample4() },
    ),
    AnimationEntry(
      id = 5,
      title = "Animated Counter",
      subtitle = "AnimatedContent with directional slide + fade. Tweak SizeTransform and durations.",
      accent = Color(0xFF26C6DA),
      content = { AnimationExample5() },
    ),
    AnimationEntry(
      id = 6,
      title = "Crossfade Switcher",
      subtitle = "Crossfade between selectable panels. Swap animationSpec for snappy vs lazy.",
      accent = Color(0xFF26A69A),
      content = { AnimationExample6() },
    ),
    AnimationEntry(
      id = 7,
      title = "Pulsing Heart",
      subtitle = "rememberInfiniteTransition with scale + alpha. Tweak duration, RepeatMode.",
      accent = Color(0xFF66BB6A),
      content = { AnimationExample7() },
    ),
    AnimationEntry(
      id = 8,
      title = "Custom Loading Spinner",
      subtitle = "Infinite rotation + color sweep. Try keyframes vs tween, change colors.",
      accent = Color(0xFF9CCC65),
      content = { AnimationExample8() },
    ),
    AnimationEntry(
      id = 9,
      title = "Spring Drag Box",
      subtitle = "Animatable with Spring. Drag and release, then change stiffness for snap vs wobble.",
      accent = Color(0xFFFFCA28),
      content = { AnimationExample9() },
    ),
    AnimationEntry(
      id = 10,
      title = "Easing Showcase",
      subtitle = "Six easings racing side by side. Replace any Easing function and race again.",
      accent = Color(0xFFFFA726),
      content = { AnimationExample10() },
    ),
    AnimationEntry(
      id = 11,
      title = "Play / Pause Morph",
      subtitle = "Animated icon shape morph via path interpolation. Swap target shapes.",
      accent = Color(0xFFFF7043),
      content = { AnimationExample11() },
    ),
    AnimationEntry(
      id = 12,
      title = "Shared Bounds Expansion",
      subtitle = "SharedTransitionLayout: card → detail. Tweak boundsTransform spec.",
      accent = Color(0xFF8D6E63),
      content = { AnimationExample12() },
    ),
    AnimationEntry(
      id = 13,
      title = "Swipeable Cards",
      subtitle = "Tinder style swipe with fling spec. Change threshold, rotation factor.",
      accent = Color(0xFFEC407A),
      content = { AnimationExample13() },
    ),
    AnimationEntry(
      id = 14,
      title = "Radial FAB Menu",
      subtitle = "Tap the center button. Satellites scatter with staggered spring physics.",
      accent = Color(0xFF5C6BC0),
      content = { AnimationExample14() },
    ),
    AnimationEntry(
      id = 15,
      title = "3D Card Flip",
      subtitle = "graphicsLayer rotationY with cameraDistance. Tweak axis, duration, perspective.",
      accent = Color(0xFF78909C),
      content = { AnimationExample15() },
    ),
    AnimationEntry(
      id = 16,
      title = "Confetti Burst",
      subtitle = "Tap anywhere to launch a burst. Each piece spins, flutters, and fades on its own timeline.",
      accent = Color(0xFFFF5252),
      content = { AnimationExample16() },
    ),
    AnimationEntry(
      id = 17,
      title = "Wave Field",
      subtitle = "Multi layer sine grid filling the screen. Frequency, amplitude, and layer count tweaks turn it into an entirely new landscape.",
      accent = Color(0xFF40C4FF),
      content = { AnimationExample17() },
    ),
    AnimationEntry(
      id = 18,
      title = "Metaball Liquid",
      subtitle = "Drifting blobs that fuse into liquid as they touch. Tweak ball count, threshold, drift speed, and change the viscosity live.",
      accent = Color(0xFF69F0AE),
      content = { AnimationExample18() },
    ),
    AnimationEntry(
      id = 19,
      title = "Mesh Aurora",
      subtitle = "Orbiting radial color orbs blended into a living mesh gradient. Tweak orb count, orbit radius, hue speed for a new aurora.",
      accent = Color(0xFFE040FB),
      content = { AnimationExample19() },
    ),
    AnimationEntry(
      id = 20,
      title = "Pendulum Wave",
      subtitle = "N pendulums with progressively shorter periods sync, diverge, and resync. Tweak count and base period to transform the pattern.",
      accent = Color(0xFFFFD740),
      content = { AnimationExample20() },
    ),
    AnimationEntry(
      id = 21,
      title = "Rainy",
      subtitle = "Continuous rain streaks falling from upper right toward lower left. Tweak angle, density, speed, length for any storm.",
      accent = Color(0xFF4FC3F7),
      content = { AnimationExample21() },
    ),
    AnimationEntry(
      id = 22,
      title = "Soap Bubble Drag",
      subtitle = "Drag the bubble up to morph the layout, tap to pop. Thin-film interference + Schlick Fresnel in AGSL, kinematic spring deformation. Tweak radii and spring values in Kotlin or AGSL constants in the shader.",
      accent = Color(0xFFB388FF),
      content = { AnimationExample22() },
    ),
  )

  fun find(id: Int): AnimationEntry = entries.firstOrNull { it.id == id } ?: entries.first()
}
