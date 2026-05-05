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
package com.skydoves.hotreloadanimations.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skydoves.hotreloadanimations.animations.AnimationEntry

@Composable
fun HomeScreen(
  examples: List<AnimationEntry>,
  onClick: (Int) -> Unit,
) {
  Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(
        top = statusBarPadding.calculateTopPadding() + 16.dp,
        bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding() + 24.dp,
        start = 16.dp,
        end = 16.dp,
      ),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
          Text(
            "Compose Animations",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
          )
          Spacer(Modifier.height(4.dp))
          Text(
            "21 animation playgrounds. Tweak values, save, watch them morph live.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
      items(examples, key = { it.id }) { entry ->
        EntryCard(entry = entry, onClick = { onClick(entry.id) })
      }
    }
  }
}

@Composable
private fun EntryCard(entry: AnimationEntry, onClick: () -> Unit) {
  Card(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(entry.accent.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          entry.id.toString(),
          color = entry.accent,
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.titleMedium,
        )
      }
      Spacer(Modifier.size(16.dp))
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          entry.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
          entry.subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}
