package com.cospose.gallery.ui.detail

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.cospose.gallery.ui.components.AnnotationDialog
import com.cospose.gallery.ui.components.AnnotationOverlay
import com.cospose.gallery.ui.components.ImageCard
import com.cospose.gallery.ui.components.MetadataSheet
import com.cospose.gallery.ui.components.StarRating
import com.cospose.gallery.ui.components.TagGroupChip
import com.cospose.gallery.ui.components.TagGroupHeader
import com.cospose.gallery.ui.components.TagTreeSheet
import com.cospose.gallery.ui.theme.HeartRed
import com.cospose.gallery.ui.theme.Purple500
import com.cospose.gallery.ui.theme.StarYellow
import com.cospose.gallery.util.ShareHelper
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    imageId: String,
    onBack: () -> Unit,
    onImageClick: (String) -> Unit,
    onPreviewClick: (Int, String) -> Unit = { _, _ -> },
    onCompare: (String, String) -> Unit = { _, _ -> },
    viewModel: DetailViewModel = hiltViewModel()
) {
    val image by viewModel.image.collectAsState()
    val manualTags by viewModel.manualTags.collectAsState()
    val aiTags by viewModel.aiTags.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()
    val userRating by viewModel.userRating.collectAsState()
    val relatedImages by viewModel.relatedImages.collectAsState()
    val allTags by viewModel.allTags.collectAsState()
    val annotations by viewModel.annotations.collectAsState()

    var commentText by remember { mutableStateOf("") }
    var showTagManager by remember { mutableStateOf(false) }
    var showMetadata by remember { mutableStateOf(false) }
    var showAnnotationDialog by remember { mutableStateOf(false) }
    var annotationPosition by remember { mutableStateOf(Pair(0.5f, 0.5f)) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(image?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showMetadata = true }) {
                        Icon(Icons.Filled.Info, contentDescription = "详情")
                    }
                    IconButton(onClick = {
                        val img = image ?: return@IconButton
                        scope.launch {
                            val uri = ShareHelper.downloadAndShare(context, img.url, img.filename)
                            if (uri != null) {
                                ShareHelper.shareImage(context, uri, img.title)
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "分享")
                    }
                    IconButton(onClick = {
                        val img = image ?: return@IconButton
                        onPreviewClick(0, img.id)
                    }) {
                        Icon(Icons.Filled.Fullscreen, contentDescription = "全屏")
                    }
                }
            )
        }
    ) { padding ->
        val img = image ?: return@Scaffold

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Image with annotation overlay
            item {
                Box {
                    val imageFile = img.mediumPath?.let { File(it) }
                    AsyncImage(
                        model = imageFile ?: img.filePath,
                        contentDescription = img.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(img.width.toFloat().coerceAtLeast(1f) / img.height.toFloat().coerceAtLeast(1f)),
                        contentScale = ContentScale.Fit
                    )

                    // Annotation overlay
                    if (annotations.isNotEmpty()) {
                        AnnotationOverlay(
                            annotations = annotations,
                            onAnnotationClick = { /* show annotation content */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(img.width.toFloat().coerceAtLeast(1f) / img.height.toFloat().coerceAtLeast(1f))
                        )
                    }
                }
            }

            // Title & Author
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        img.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (!img.description.isNullOrBlank()) {
                        Text(
                            img.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Rating
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = StarYellow)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${String.format("%.1f", img.ratingAvg)} / 5",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "${img.ratingCount} 人评分",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("你的评分", style = MaterialTheme.typography.labelMedium)
                        StarRating(
                            rating = userRating,
                            onRatingChange = { viewModel.submitRating(it) },
                            size = 32
                        )
                    }
                }
            }

            // Tags section with manage button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("标签", style = MaterialTheme.typography.titleSmall)
                    TextButton(onClick = { showTagManager = true }) {
                        Text("管理标签")
                    }
                }
            }

            // Manual tags grouped by category
            if (manualTags.isNotEmpty()) {
                item {
                    Text(
                        "手动标签",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                item {
                    val groupedManual = manualTags.groupBy { it.category }
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        groupedManual.forEach { (category, tags) ->
                            TagGroupHeader(category = category, count = tags.size)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(start = 18.dp, bottom = 8.dp)
                            ) {
                                tags.forEach { tag ->
                                    TagGroupChip(
                                        tag = tag,
                                        onRemove = { viewModel.removeTagFromImage(tag.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // AI tags
            if (aiTags.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = Purple500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "AI 识别标签",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                item {
                    FlowRow(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        aiTags.forEach { tag ->
                            TagGroupChip(
                                tag = tag,
                                onRemove = { viewModel.removeTagFromImage(tag.id) }
                            )
                        }
                    }
                }
            }

            // Add tag + annotation buttons
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = { showTagManager = true }) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("添加标签")
                    }
                    TextButton(onClick = {
                        annotationPosition = Pair(0.5f, 0.5f)
                        showAnnotationDialog = true
                    }) {
                        Icon(Icons.Filled.NoteAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("添加注释")
                    }
                    TextButton(onClick = { viewModel.reclassify("additive") }) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("重新识别")
                    }
                }
            }

            // Annotations list
            if (annotations.isNotEmpty()) {
                item {
                    Text(
                        "注释 (${annotations.size})",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(annotations) { annotation ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                annotation.content,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "位置: (${((annotation.xRatio ?: 0f) * 100).toInt()}%, ${((annotation.yRatio ?: 0f) * 100).toInt()}%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.deleteAnnotation(annotation.id) }) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Action buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { viewModel.toggleLike() }) {
                        Icon(
                            if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "点赞",
                            tint = if (isLiked) HeartRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(onClick = {
                        scope.launch {
                            val uri = ShareHelper.downloadAndShare(context, img.url, img.filename)
                            if (uri != null) {
                                ShareHelper.saveToGallery(context, img.url, img.filename)
                            }
                        }
                    }) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = "保存到相册",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    if (relatedImages.size >= 2) {
                        IconButton(onClick = {
                            onCompare(relatedImages[0].id, relatedImages[1].id)
                        }) {
                            Icon(
                                Icons.Filled.Compare,
                                contentDescription = "对比",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Comment,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${comments.size}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Comments
            item {
                Text(
                    "评论",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("写评论...") },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.addComment(commentText)
                                commentText = ""
                            }
                        }
                    ) {
                        Text("发送")
                    }
                }
            }

            items(comments) { comment ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        comment.content,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(comment.createdAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                }
            }

            // Related
            if (relatedImages.isNotEmpty()) {
                item {
                    Text(
                        "相关推荐",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(relatedImages) { related ->
                            ImageCard(
                                image = related,
                                onClick = { onImageClick(related.id) },
                                modifier = Modifier.width(160.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Tag manager sheet
    if (showTagManager) {
        val currentTagIds = (manualTags + aiTags).map { it.id }.toSet()
        TagTreeSheet(
            allTags = allTags,
            selectedTagIds = currentTagIds,
            onTagToggle = { tagId ->
                if (tagId in currentTagIds) {
                    viewModel.removeTagFromImage(tagId)
                } else {
                    viewModel.addTagToImage(tagId)
                }
            },
            onDismiss = { showTagManager = false }
        )
    }

    // Metadata sheet
    if (showMetadata && image != null) {
        MetadataSheet(
            image = image!!,
            onDismiss = { showMetadata = false }
        )
    }

    // Annotation dialog
    if (showAnnotationDialog) {
        AnnotationDialog(
            onSave = { content ->
                viewModel.addAnnotation(content, annotationPosition.first, annotationPosition.second)
                showAnnotationDialog = false
            },
            onDismiss = { showAnnotationDialog = false }
        )
    }
}
