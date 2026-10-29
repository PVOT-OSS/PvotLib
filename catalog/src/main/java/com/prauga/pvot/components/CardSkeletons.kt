// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotSkeleton

/** Stands in for a [FeedCard] while the feed loads. */
@Composable
fun FeedCardSkeleton(modifier: Modifier = Modifier) {
    PvotCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PvotSkeleton(Modifier.fillMaxWidth(0.25f).height(10.dp))
            PvotSkeleton(Modifier.fillMaxWidth(0.7f).height(18.dp))
            PvotSkeleton(Modifier.fillMaxWidth().height(12.dp))
            PvotSkeleton(Modifier.fillMaxWidth(0.9f).height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PvotSkeleton(Modifier.fillMaxWidth(0.3f).height(10.dp))
                PvotSkeleton(Modifier.fillMaxWidth(0.3f).height(10.dp))
            }
        }
    }
}

/** Stands in for an [AppCard] while the repository list loads. */
@Composable
fun AppCardSkeleton(modifier: Modifier = Modifier) {
    PvotCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            PvotSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PvotSkeleton(Modifier.fillMaxWidth(0.45f).height(18.dp))
                    PvotSkeleton(Modifier.fillMaxWidth(0.2f).height(14.dp))
                }
                PvotSkeleton(Modifier.fillMaxWidth().height(12.dp))
            }
        }
    }
}

/** Stands in for a [DeveloperCard] while the team profiles load. */
@Composable
fun DeveloperCardSkeleton(modifier: Modifier = Modifier) {
    PvotCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PvotSkeleton(
                modifier = Modifier.size(64.dp),
                shape = CircleShape
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PvotSkeleton(Modifier.fillMaxWidth(0.5f).height(16.dp))
                PvotSkeleton(Modifier.fillMaxWidth(0.35f).height(12.dp))
                PvotSkeleton(Modifier.fillMaxWidth(0.8f).height(10.dp))
            }
        }
    }
}
