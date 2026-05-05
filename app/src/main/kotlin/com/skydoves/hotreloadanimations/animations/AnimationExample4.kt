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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimationExample4() {
  val springStiffness = Spring.StiffnessMediumLow // try Spring.StiffnessLow / StiffnessHigh
  val springDamping = Spring.DampingRatioMediumBouncy // try NoBouncy / HighBouncy

  val collapsedSize = 74.dp // idle FAB size
  val expandedSize = 196.dp // morphed FAB size
  val collapsedRotation = 0f // idle rotation in degrees
  val expandedRotation = 135f // morphed rotation (try 45f / 180f / 360f)
  val expandedCornerDp = 24.dp // morphed corner radius

  var morphed by remember { mutableStateOf(false) }

  val size by animateDpAsState(
    targetValue = if (morphed) expandedSize else collapsedSize,
    animationSpec = spring(dampingRatio = springDamping, stiffness = springStiffness),
    label = "size",
  )
  val rotation by animateFloatAsState(
    targetValue = if (morphed) expandedRotation else collapsedRotation,
    animationSpec = spring(dampingRatio = springDamping, stiffness = springStiffness),
    label = "rotation",
  )
  val cornerDp by animateDpAsState(
    targetValue = if (morphed) expandedCornerDp else collapsedSize / 2,
    animationSpec = spring(dampingRatio = springDamping, stiffness = springStiffness),
    label = "corner",
  )
  val color by animateColorAsState(
    targetValue = if (morphed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
    animationSpec = spring(dampingRatio = springDamping, stiffness = springStiffness),
    label = "color",
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(240.dp),
      contentAlignment = Alignment.Center,
    ) {
      Box(
        modifier = Modifier
          .size(size)
          .rotate(rotation)
          .background(color = color, shape = RoundedCornerShape(cornerDp))
          .clickable { morphed = !morphed },
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = "+",
          color = Color.White,
          fontSize = 32.sp,
          fontWeight = FontWeight.Bold,
        )
      }
    }
    Text(
      text = "Tap to morph",
      modifier = Modifier.fillMaxWidth(),
      style = MaterialTheme.typography.labelLarge,
    )
  }
}
