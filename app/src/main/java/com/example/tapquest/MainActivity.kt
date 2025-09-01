package com.example.tapquest

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// ---------------- Activity ----------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TapQuestApp() }
    }
}

// ---------------- Models ----------------
data class GameOption(
    val name: String,
    val imageRes: Int? = null,
    val soundRes: Int? = null,
    val color: Color? = null
)

data class ConfettiParticle(var x: Float, var y: Float, val color: Color, val rotation: Float)

// ---------------- App ----------------
@Composable
fun TapQuestApp() {
    val navController = rememberNavController()
    Surface {
        NavHost(navController = navController, startDestination = "welcome") {
            composable("welcome") { WelcomeScreen(navController) }
            composable("countdown/{playerName}/{theme}") { backStack ->
                val name = backStack.arguments?.getString("playerName") ?: "Player"
                val theme = backStack.arguments?.getString("theme") ?: "animals"
                CountdownScreen(navController, name, theme)
            }
            composable("game/{playerName}/{theme}") { backStack ->
                val name = backStack.arguments?.getString("playerName") ?: "Player"
                val theme = backStack.arguments?.getString("theme") ?: "animals"
                GameScreen(navController, name, theme)
            }
            composable("leaderboard/{score}/{playerName}") { backStack ->
                val score = backStack.arguments?.getString("score")?.toIntOrNull() ?: 0
                val name = backStack.arguments?.getString("playerName") ?: "Player"
                LeaderboardScreen(navController, score, name)
            }
        }
    }
}

// ---------------- Helpers ----------------
@Composable
fun AppGradientBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to Color(0xFFE3F2FD),
                    1f to Color(0xFFC8E6FF)
                )
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}

private fun playRawOnce(context: android.content.Context, resId: Int?) {
    if (resId == null) return
    val mp = MediaPlayer.create(context, resId)
    mp?.setOnCompletionListener { it.release() }
    mp?.start()
}

// ---------------- Welcome Screen ----------------
@Composable
fun WelcomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    var playerName by remember { mutableStateOf("") }
    var selectedTheme by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Intro sound after 1s
    LaunchedEffect(Unit) {
        delay(1000)
        playRawOnce(context, R.raw.intro)
    }

    AppGradientBackground {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(0.92f)
        ) {
            Text("TapQuest", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Tap the correct answer and win stars!",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text("Enter your name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(14.dp))

            Text("Choose a theme:", fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(10.dp))

            // Theme tiles (selectable)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ThemeTileSelectable(
                    label = "Animals",
                    emoji = "🐶",
                    selected = selectedTheme == "animals",
                    onClick = {
                        playRawOnce(context, R.raw.animals)
                        selectedTheme = "animals"
                    }
                )

                ThemeTileSelectable(
                    label = "Birds",
                    emoji = "🐦",
                    selected = selectedTheme == "birds",
                    onClick = {
                        playRawOnce(context, R.raw.birds)
                        selectedTheme = "birds"
                    }
                )

                ThemeTileSelectable(
                    label = "Colors",
                    emoji = "🎨",
                    selected = selectedTheme == "colors",
                    onClick = {
                        playRawOnce(context, R.raw.colors)
                        selectedTheme = "colors"
                    }
                )
            }

            Spacer(Modifier.height(20.dp))

            // Start button (validates name + theme)
            Button(onClick = {
                if (playerName.isBlank()) {
                    Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (selectedTheme == null) {
                    Toast.makeText(context, "Please select a theme", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                // Optional little delay for UX
                scope.launch {
                    // small delay so theme sound finishes if just tapped
                    delay(300)
                    navController.navigate("countdown/${playerName}/${selectedTheme}")
                }
            }) {
                Text("Start")
            }
        }
    }
}

@Composable
fun ThemeTileSelectable(label: String, emoji: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(width = 120.dp, height = 110.dp)
            .background(Color.White, RoundedCornerShape(18.dp))
            .border(2.dp, Color(0xFF90CAF9), RoundedCornerShape(18.dp))
            .clickable { onClick() },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 34.sp)
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        // Selected tick at top-right
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .background(Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ---------------- Countdown ----------------
@Composable
fun CountdownScreen(navController: NavHostController, playerName: String, theme: String) {
    var count by remember { mutableIntStateOf(3) }

    LaunchedEffect(Unit) {
        for (i in 3 downTo 1) {
            count = i
            delay(1000)
        }
        count = 0
        delay(500)
        navController.navigate("game/$playerName/$theme")
    }

    AppGradientBackground {
        Text(
            text = if (count == 0) "Start!" else count.toString(),
            fontSize = 64.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1565C0)
        )
    }
}

// ---------------- Game Screen ----------------
@Composable
fun GameScreen(navController: NavHostController, playerName: String, theme: String) {
    val context = LocalContext.current

    val options: List<GameOption> = remember(theme) {
        when (theme) {
            "animals" -> listOf(
                GameOption("DOG", imageRes = R.drawable.dog, soundRes = R.raw.dog),
                GameOption("CAT", imageRes = R.drawable.cat, soundRes = R.raw.cat),
                GameOption("COW", imageRes = R.drawable.cow, soundRes = R.raw.cow)
            )
            "birds" -> listOf(
                GameOption("CROW", imageRes = R.drawable.crow, soundRes = R.raw.crow),
                GameOption("PARROT", imageRes = R.drawable.parrot, soundRes = R.raw.parrot),
                GameOption("PEACOCK", imageRes = R.drawable.peacock, soundRes = R.raw.peacock)
            )
            "colors" -> listOf(
                GameOption("RED", color = Color.Red, soundRes = R.raw.red),
                GameOption("BLUE", color = Color.Blue, soundRes = R.raw.blue),
                GameOption("GREEN", color = Color.Green, soundRes = R.raw.green),
                GameOption("YELLOW", color = Color.Yellow, soundRes = R.raw.yellow),
                GameOption("ORANGE", color = Color(0xFFFF9800), soundRes = R.raw.orange)
            )
            else -> emptyList()
        }
    }

    val totalQuestions = options.size
    var questionIndex by remember { mutableIntStateOf(0) }
    // derive current from options + questionIndex to avoid stale state
    val current: GameOption = options.getOrNull(questionIndex) ?: options.first()
    var score by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("") }
    var tries by remember { mutableIntStateOf(0) }
    var showConfetti by remember { mutableStateOf(false) }
    var nextTrigger by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    // displayedOptions gets refreshed each question
    var displayedOptions by remember { mutableStateOf(options.shuffled()) }
    fun refreshDisplayedOptions() {
        displayedOptions = options.shuffled()
    }

    // Play question sound on load (skip when finished)
    LaunchedEffect(questionIndex) {
        if (!isFinished) {
            playRawOnce(context, current.soundRes)
            feedback = ""
            tries = 0
            showConfetti = false
            refreshDisplayedOptions()
        }
    }

    // Move to next question or leaderboard
    LaunchedEffect(nextTrigger) {
        if (nextTrigger) {
            delay(1200)
            questionIndex++
            if (questionIndex < totalQuestions) {
                // next question will trigger the LaunchedEffect(questionIndex) above
                showConfetti = false
                nextTrigger = false
            } else {
                // stop any further question sounds and go to leaderboard
                isFinished = true
                // small delay so end sound can finish
                delay(200)
                navController.navigate("leaderboard/$score/$playerName")
            }
        }
    }

    AppGradientBackground {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Q ${questionIndex + 1} / $totalQuestions", fontSize = 20.sp)
                Text("⭐ $score", fontSize = 20.sp, color = Color(0xFFFFA000))
            }
            Spacer(Modifier.height(12.dp))

            // Word (big)
            Text(
                text = current.name,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0D47A1)
            )
            Spacer(Modifier.height(8.dp))

            if (current.soundRes != null) {
                Button(onClick = { playRawOnce(context, current.soundRes) }) {
                    Text("🔊 Replay Sound")
                }
            }
            Spacer(Modifier.height(16.dp))

            // Show options in chunked rows (max 3 per row) to avoid clipping
            val chunks = displayedOptions.chunked(3)
            chunks.forEach { chunk ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    chunk.forEach { opt ->
                        if (theme == "colors") {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .background(opt.color ?: Color.Gray, RoundedCornerShape(16.dp))
                                    .border(3.dp, Color(0xFF1976D2), RoundedCornerShape(16.dp))
                                    .clickable {
                                        if (opt == current) {
                                            playRawOnce(context, R.raw.correct)
                                            score++
                                            feedback = "✅ Correct!"
                                            showConfetti = true
                                            nextTrigger = true
                                        } else {
                                            tries++
                                            playRawOnce(context, R.raw.wrong)
                                            feedback = if (tries == 1) "❌ Try again!" else "⚠️ Better luck next time!"
                                            if (tries >= 2) nextTrigger = true
                                        }
                                    }
                            )
                        } else {
                            // image option
                            Image(
                                painter = painterResource(opt.imageRes!!),
                                contentDescription = opt.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(110.dp)
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .border(3.dp, Color(0xFF1976D2), RoundedCornerShape(16.dp))
                                    .clickable {
                                        if (opt == current) {
                                            playRawOnce(context, R.raw.correct)
                                            score++
                                            feedback = "✅ Correct!"
                                            showConfetti = true
                                            nextTrigger = true
                                        } else {
                                            tries++
                                            playRawOnce(context, R.raw.wrong)
                                            feedback = if (tries == 1) "❌ Try again!" else "⚠️ Better luck next time!"
                                            if (tries >= 2) nextTrigger = true
                                        }
                                    }
                            )
                        }
                    }

                    // If chunk has less than 3 items, fill with empty spacers to keep spacing consistent
                    if (chunk.size < 3) {
                        repeat(3 - chunk.size) {
                            Spacer(modifier = Modifier.size(110.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                feedback,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (feedback.startsWith("✅")) Color(0xFF2E7D32) else Color(0xFFD32F2F)
            )
        }

        if (showConfetti) ConfettiAnimation()
    }
}

// ---------------- Confetti ----------------
@Composable
fun ConfettiAnimation() {
    val confettiCount = 40
    val confetti = remember {
        List(confettiCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f),
                rotation = Random.nextFloat() * 360f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing))
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        confetti.forEach { c ->
            val xPos = c.x * size.width
            val yPos = (c.y + animProgress) * size.height % size.height
            rotate(c.rotation) {
                drawRect(
                    color = c.color,
                    topLeft = Offset(xPos, yPos),
                    size = androidx.compose.ui.geometry.Size(8f, 8f)
                )
            }
        }
    }
}

// ---------------- Leaderboard ----------------
@Composable
fun LeaderboardScreen(navController: NavHostController, score: Int, playerName: String) {
    val context = LocalContext.current

    val base = listOf(
        "Alice" to 5,
        "Bob" to 4,
        "Charlie" to 3
    )

    val all = remember(score, playerName) { (base + (playerName to score)).sortedByDescending { it.second } }
    val rank = all.indexOfFirst { it.first == playerName && it.second == score } + 1

    var showRankMsg by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1000)
        showRankMsg = true
        playRawOnce(context, R.raw.rank) // user will add rank.mp3 in res/raw
    }

    AppGradientBackground {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(0.92f)
        ) {
            Text("🏆 Leaderboard", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(16.dp))

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFBBDEFB), RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Player", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text("Stars", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFF90CAF9), RoundedCornerShape(12.dp))
            ) {
                all.forEachIndexed { i, (name, pts) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${i + 1}. $name", fontSize = 20.sp)
                        Text("⭐".repeat(pts.coerceAtMost(5)), fontSize = 20.sp, color = Color(0xFFFFA000))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (showRankMsg) {
                Text(
                    text = "🎉 You are at Rank $rank!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(Modifier.height(22.dp))

            Button(onClick = { navController.navigate("welcome") }) {
                Text("Play Again")
            }
        }
    }
}
