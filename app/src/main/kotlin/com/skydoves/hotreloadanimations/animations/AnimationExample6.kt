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

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimationExample6() {
  val crossfadeDurationMs = 6600 // Try snap (1) or slow (2000) for very different feels.

  val color1 = Color(0xFFFFB74D)
  val color2 = Color(0xFF4FC3F7)
  val color3 = Color(0xFF5C6BC0)

  val tabs = listOf(
    Triple("🌅", "Morning", color1),
    Triple("🌞", "Noon", color2),
    Triple("🌙", "Night", color3),
  )

  var selected by remember { mutableIntStateOf(0) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      tabs.forEachIndexed { index, (emoji, name, _) ->
        val isSelected = index == selected
        if (isSelected) {
          Button(
            onClick = { selected = index },
            modifier = Modifier.height(44.dp),
          ) {
            Text(text = "$emoji $name", fontSize = 14.sp)
          }
        } else {
          OutlinedButton(
            onClick = { selected = index },
            modifier = Modifier.height(44.dp),
            colors = ButtonDefaults.outlinedButtonColors(),
          ) {
            Text(text = "$emoji $name", fontSize = 14.sp)
          }
        }
      }
    }

    Crossfade(
      targetState = selected,
      animationSpec = tween(crossfadeDurationMs),
      label = "tab-crossfade",
    ) { current ->
      val (emoji, name, color) = tabs[current]
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .height(260.dp),
        shape = RoundedCornerShape(20.dp),
        color = color,
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center,
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Text(text = emoji, fontSize = 96.sp)
            Text(
              text = name,
              fontSize = 36.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
            )
          }
        }
      }
    }
  }
}
