package com.cospose.gallery.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cospose.gallery.ui.theme.StarYellow

@Composable
fun StarRating(
    rating: Int,
    onRatingChange: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
    size: Int = 24
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "星 $i",
                tint = if (i <= rating) StarYellow else StarYellow.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(size.dp)
                    .then(
                        if (onRatingChange != null) Modifier.clickable { onRatingChange(i) }
                        else Modifier
                    )
            )
        }
    }
}
