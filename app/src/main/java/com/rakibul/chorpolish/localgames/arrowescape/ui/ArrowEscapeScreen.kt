package com.rakibul.chorpolish.localgames.arrowescape.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rakibul.chorpolish.localgames.arrowescape.model.ArrowDirection
import com.rakibul.chorpolish.localgames.arrowescape.model.ArrowTile
import com.rakibul.chorpolish.localgames.arrowescape.model.LevelData
import com.rakibul.chorpolish.localgames.arrowescape.viewmodel.ArrowEscapeViewModel
import com.rakibul.chorpolish.ui.theme.GameScreenWrapper
import com.rakibul.chorpolish.ui.theme.GameSecondary
import com.rakibul.chorpolish.ui.theme.SurfaceGlass

@Composable
fun ArrowEscapeScreen(
    onBack: () -> Unit,
    viewModel: ArrowEscapeViewModel = viewModel()
) {
    val levelData by viewModel.currentLevel.collectAsState()
    val message by viewModel.motivationalMessage.collectAsState()

    GameScreenWrapper {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ArrowEscapeTopBar(
                levelNumber = levelData.levelNumber,
                levelName = levelData.levelName,
                onBack = onBack
            )

            Spacer(modifier = Modifier.weight(0.5f))

            MotivationalMessageArea(message = message)

            Spacer(modifier = Modifier.height(16.dp))

            ArrowEscapeBoard(
                levelData = levelData,
                modifier = Modifier
                    .weight(3f)
                    .aspectRatio(1f)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(0.5f))

            ArrowEscapeBottomBar(
                onUndo = viewModel::onUndo,
                onHint = viewModel::onHint,
                onRestart = viewModel::onRestart
            )
        }
    }
}

@Composable
fun ArrowEscapeTopBar(
    levelNumber: Int,
    levelName: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "ARROW ESCAPE",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = Color.White
            )
            Text(
                if (levelName.isNotEmpty()) "LEVEL $levelNumber - $levelName" else "LEVEL $levelNumber",
                style = MaterialTheme.typography.labelMedium,
                color = GameSecondary,
                fontWeight = FontWeight.Bold
            )
        }

        // Empty box for symmetry
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
fun MotivationalMessageArea(message: String?) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (message != null) {
            Text(
                message,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ArrowEscapeBoard(
    levelData: LevelData,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceGlass)
            .border(2.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .padding(12.dp)
    ) {
        val boardWidth = maxWidth
        val boardHeight = maxHeight
        val cellSize = minOf(boardWidth / levelData.cols, boardHeight / levelData.rows)

        // Draw grid or placeholder board content
        levelData.arrows.forEach { arrow ->
            ArrowTileView(
                arrow = arrow,
                cellSize = cellSize,
                modifier = Modifier.offset(
                    x = cellSize * arrow.col,
                    y = cellSize * arrow.row
                )
            )
        }
    }
}

@Composable
fun ArrowTileView(
    arrow: ArrowTile,
    cellSize: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val icon = when (arrow.direction) {
        ArrowDirection.UP -> Icons.Rounded.ArrowUpward
        ArrowDirection.DOWN -> Icons.Rounded.ArrowDownward
        ArrowDirection.LEFT -> Icons.Rounded.ArrowBack
        ArrowDirection.RIGHT -> Icons.Rounded.ArrowForward
    }

    Box(
        modifier = modifier
            .size(cellSize)
            .padding(4.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(cellSize * 0.6f)
        )
    }
}

@Composable
fun ArrowEscapeBottomBar(
    onUndo: () -> Unit,
    onHint: () -> Unit,
    onRestart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        GameActionButton(text = "Undo", icon = Icons.Rounded.Undo, onClick = onUndo)
        GameActionButton(text = "Hint", icon = Icons.Rounded.Lightbulb, onClick = onHint)
        GameActionButton(text = "Restart", icon = Icons.Rounded.Refresh, onClick = onRestart)
    }
}

@Composable
fun GameActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceGlass)
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = text, tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
