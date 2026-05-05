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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AnimationExample2() {
  val ANIM_DURATION_MS = 2600 // Tweak shared duration for slide/fade/scale together
  // Swap slideInHorizontally direction below with negative/positive { fullWidth -> ... }
  val SLIDE_FROM_RIGHT = true
  var visible by remember { mutableStateOf(true) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "AnimatedVisibility: slide / fade / scale",
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onSurface,
    )

    Button(onClick = { visible = !visible }) {
      Text(text = if (visible) "Hide all" else "Show all")
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      DemoCell(label = "SLIDE", modifier = Modifier.weight(1f)) {
        AnimatedVisibility(
          visible = visible,
          enter = slideInHorizontally(
            animationSpec = tween(ANIM_DURATION_MS),
            initialOffsetX = { fullWidth -> if (SLIDE_FROM_RIGHT) fullWidth else -fullWidth },
          ) + fadeIn(animationSpec = tween(ANIM_DURATION_MS)),
          exit = slideOutHorizontally(
            animationSpec = tween(ANIM_DURATION_MS),
            targetOffsetX = { fullWidth -> if (SLIDE_FROM_RIGHT) fullWidth else -fullWidth },
          ) + fadeOut(animationSpec = tween(ANIM_DURATION_MS)),
        ) {
          AnimChip(text = "SLIDE")
        }
      }

      DemoCell(label = "FADE", modifier = Modifier.weight(1f)) {
        AnimatedVisibility(
          visible = visible,
          enter = fadeIn(animationSpec = tween(ANIM_DURATION_MS)),
          exit = fadeOut(animationSpec = tween(ANIM_DURATION_MS)),
        ) {
          AnimChip(text = "FADE")
        }
      }

      DemoCell(label = "SCALE", modifier = Modifier.weight(1f)) {
        AnimatedVisibility(
          visible = visible,
          enter = scaleIn(
            animationSpec = tween(ANIM_DURATION_MS),
            transformOrigin = TransformOrigin(0.5f, 0.5f),
          ) + fadeIn(animationSpec = tween(ANIM_DURATION_MS)),
          exit = scaleOut(
            animationSpec = tween(ANIM_DURATION_MS),
            transformOrigin = TransformOrigin(0.5f, 0.5f),
          ) + fadeOut(animationSpec = tween(ANIM_DURATION_MS)),
        ) {
          AnimChip(text = "SCALE")
        }
      }
    }
  }
}

@Composable
private fun DemoCell(
  label: String,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(96.dp),
      contentAlignment = Alignment.Center,
    ) {
      content()
    }
  }
}

@Composable
private fun AnimChip(text: String) {
  Card(
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ),
    shape = MaterialTheme.shapes.medium,
  ) {
    Text(
      text = text,
      modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
      style = MaterialTheme.typography.titleSmall,
      fontWeight = FontWeight.Bold,
    )
  }
}
