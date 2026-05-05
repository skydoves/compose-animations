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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AnimationExample9() {
  val SPRING_STIFFNESS = 600f // try 50f (loose) / 1500f (snappy)
  val SPRING_DAMPING = 0.55f // try 0.2f (very bouncy) / 1.0f (no bounce)
  val BOX_COLOR = Color(0xFF26A69A) // try any color
  val BOX_SIZE_DP = 80.dp
  val BOX_CORNER_DP = 16.dp

  val offsetX = remember { Animatable(0f) }
  val offsetY = remember { Animatable(0f) }
  val scope = rememberCoroutineScope()

  LaunchedEffect(SPRING_STIFFNESS, SPRING_DAMPING) {
    offsetX.snapTo(0f)
    offsetY.snapTo(0f)
  }

  Column(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Spring Drag Box",
      fontWeight = FontWeight.SemiBold,
    )
    Text(
      text = "Drag me, then release",
      fontSize = 12.sp,
      color = Color(0xFF666666),
    )
    Text(
      text = "offset = (${offsetX.value.roundToInt()}, ${offsetY.value.roundToInt()})",
      fontSize = 10.sp,
      color = Color(0xFF999999),
    )
    Box(
      modifier = Modifier.fillMaxWidth().heightIn(min = 280.dp),
      contentAlignment = Alignment.Center,
    ) {
      Box(
        modifier = Modifier
          .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
          .size(BOX_SIZE_DP)
          .clip(RoundedCornerShape(BOX_CORNER_DP))
          .background(BOX_COLOR)
          .pointerInput(SPRING_STIFFNESS, SPRING_DAMPING) {
            detectDragGestures(
              onDrag = { change, dragAmount ->
                change.consume()
                scope.launch {
                  offsetX.snapTo(offsetX.value + dragAmount.x)
                  offsetY.snapTo(offsetY.value + dragAmount.y)
                }
              },
              onDragEnd = {
                scope.launch {
                  launch {
                    offsetX.animateTo(
                      targetValue = 0f,
                      animationSpec = spring(
                        stiffness = SPRING_STIFFNESS,
                        dampingRatio = SPRING_DAMPING,
                      ),
                    )
                  }
                  launch {
                    offsetY.animateTo(
                      targetValue = 0f,
                      animationSpec = spring(
                        stiffness = SPRING_STIFFNESS,
                        dampingRatio = SPRING_DAMPING,
                      ),
                    )
                  }
                }
              },
            )
          },
      )
    }
  }
}
