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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private data class CardModel(
  val id: String,
  val title: String,
  val accent: Color,
  val body: String,
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AnimationExample12() {
  val BOUNDS_DURATION_MS = 500 // expand/collapse speed
  val BOUNDS_EASING = FastOutSlowInEasing // swap easing to feel different
  val DETAIL_HEIGHT_DP = 260 // expanded card height

  val CARDS = listOf(
    CardModel(
      id = "a",
      title = "Card A",
      accent = Color(0xFFEF5350),
      body = "This card expands using shared bounds. Tap to collapse back to the list.",
    ),
    CardModel(
      id = "b",
      title = "Card B",
      accent = Color(0xFF42A5F5),
      body = "Try changing BOUNDS_DURATION_MS or BOUNDS_EASING above and watch the timing morph live.",
    ),
    CardModel(
      id = "c",
      title = "Card C",
      accent = Color(0xFF66BB6A),
      body = "SharedTransitionLayout ties matching keys across AnimatedContent states for smooth bounds animation.",
    ),
  )

  var selectedId by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text("Shared Bounds Expansion", style = MaterialTheme.typography.titleMedium)
    Text(
      "Tap a card to expand into a detail view.",
      style = MaterialTheme.typography.bodySmall,
    )
    SharedTransitionLayout(modifier = Modifier.fillMaxWidth()) {
      AnimatedContent(
        targetState = selectedId,
        transitionSpec = {
          (fadeIn(tween(BOUNDS_DURATION_MS, easing = BOUNDS_EASING)) togetherWith
            fadeOut(tween(BOUNDS_DURATION_MS, easing = BOUNDS_EASING)))
        },
        label = "shared-bounds",
      ) { current ->
        if (current == null) {
          CardList(
            scope = this@SharedTransitionLayout,
            visibilityScope = this@AnimatedContent,
            cards = CARDS,
            boundsDurationMs = BOUNDS_DURATION_MS,
            boundsEasing = BOUNDS_EASING,
            onSelect = { selectedId = it },
          )
        } else {
          val model = CARDS.first { it.id == current }
          CardDetail(
            scope = this@SharedTransitionLayout,
            visibilityScope = this@AnimatedContent,
            model = model,
            detailHeightDp = DETAIL_HEIGHT_DP,
            boundsDurationMs = BOUNDS_DURATION_MS,
            boundsEasing = BOUNDS_EASING,
            onClose = { selectedId = null },
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun CardList(
  scope: SharedTransitionScope,
  visibilityScope: AnimatedVisibilityScope,
  cards: List<CardModel>,
  boundsDurationMs: Int,
  boundsEasing: Easing,
  onSelect: (String) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    cards.forEach { model ->
      with(scope) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .sharedElement(
              sharedContentState = rememberSharedContentState(key = "card-${model.id}"),
              animatedVisibilityScope = visibilityScope,
              boundsTransform = { _, _ ->
                tween(durationMillis = boundsDurationMs, easing = boundsEasing)
              },
            )
            .background(color = model.accent, shape = RoundedCornerShape(16.dp))
            .clickable { onSelect(model.id) }
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
          )
          Text(
            text = model.title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun CardDetail(
  scope: SharedTransitionScope,
  visibilityScope: AnimatedVisibilityScope,
  model: CardModel,
  detailHeightDp: Int,
  boundsDurationMs: Int,
  boundsEasing: Easing,
  onClose: () -> Unit,
) {
  with(scope) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .height(detailHeightDp.dp)
        .sharedElement(
          sharedContentState = rememberSharedContentState(key = "card-${model.id}"),
          animatedVisibilityScope = visibilityScope,
          boundsTransform = { _, _ ->
            tween(durationMillis = boundsDurationMs, easing = boundsEasing)
          },
        )
        .background(color = model.accent, shape = RoundedCornerShape(24.dp))
        .clickable { onClose() }
        .padding(24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(
        text = model.title,
        color = Color.White,
        style = MaterialTheme.typography.headlineLarge,
      )
      Spacer(Modifier.height(4.dp))
      Text(
        text = model.body,
        color = Color.White,
        style = MaterialTheme.typography.bodyLarge,
      )
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Text(
          text = "Tap to close",
          color = Color.White.copy(alpha = 0.85f),
          style = MaterialTheme.typography.labelLarge,
        )
      }
    }
  }
}
