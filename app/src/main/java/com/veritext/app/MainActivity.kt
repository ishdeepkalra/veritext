package com.veritext.app

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var modelController: VeriTextModelController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {}
        
        val permissions = mutableListOf(Manifest.permission.RECEIVE_SMS)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        requestPermissionLauncher.launch(permissions.toTypedArray())

        val serviceIntent = Intent(this, ProtectionService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        modelController = VeriTextModelController(this)
        handleIntent(intent)

        setContent {
            // Force Dark Theme for a premium hacker aesthetic
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0D0D0D) // Deep black background
                ) {
                    VeriTextScreen(modelController)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val smsBody = intent?.getStringExtra("sms_body")
        if (!smsBody.isNullOrBlank()) {
            SmsRepository.emitSms(smsBody)
        }
    }

    override fun onStart() {
        super.onStart()
        SmsRepository.isUiActive = true
    }

    override fun onStop() {
        super.onStop()
        SmsRepository.isUiActive = false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeriTextScreen(modelController: VeriTextModelController) {
    var message by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<VerificationResult?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var streamedText by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Listen to incoming SMS automatically
    LaunchedEffect(Unit) {
        SmsRepository.incomingSmsFlow.collect { newSms ->
            message = newSms
            // Auto-trigger analysis
            if (!isProcessing) {
                isProcessing = true
                result = null
                streamedText = ""
                coroutineScope.launch {
                    modelController.analyzeMessageStream(message)
                        .catch { e -> streamedText += "\nError: ${e.message}" }
                        .onCompletion {
                            result = modelController.parseStreamedResult(streamedText)
                            isProcessing = false
                        }
                        .collect { chunk -> streamedText += chunk }
                }
            }
        }
    }

    // A pulsing animation for the "Analyzing" state
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Premium Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.CheckCircle, 
                contentDescription = null, 
                tint = Color(0xFFBB86FC),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "VeriText", 
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "On-device AI Protection (Gemma 4)",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFBB86FC)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Modern Input Field
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Awaiting SMS or type here...", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp)),
            minLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFBB86FC),
                unfocusedBorderColor = Color(0xFF333333),
                cursorColor = Color(0xFFBB86FC),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // Premium Button
        Button(
            onClick = {
                isProcessing = true
                result = null
                streamedText = ""
                
                coroutineScope.launch {
                    modelController.analyzeMessageStream(message)
                        .catch { e -> streamedText += "\nError: ${e.message}" }
                        .onCompletion {
                            result = modelController.parseStreamedResult(streamedText)
                            isProcessing = false
                        }
                        .collect { chunk -> streamedText += chunk }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isProcessing && message.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFBB86FC),
                disabledContainerColor = Color(0xFF333333),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isProcessing) "ANALYZING..." else "ANALYZE MESSAGE",
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Streaming Terminal View
        if (isProcessing || (streamedText.isNotBlank() && result == null)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                border = BorderStroke(1.dp, Color(0xFFBB86FC).copy(alpha = pulseAlpha)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = streamedText.ifBlank { "Initializing LiteRT Engine..." },
                        color = Color(0xFF00FF00),
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Final Result Card with smooth expansion
        if (!isProcessing && result != null) {
            val res = result!!
            val (badgeColor, icon) = when (res.label.uppercase()) {
                "SCAM" -> Color(0xFFFF3B30) to Icons.Filled.Warning
                "SUSPICIOUS" -> Color(0xFFFF9500) to Icons.Filled.Info
                "ERROR" -> Color.Gray to Icons.Filled.Warning
                else -> Color(0xFF34C759) to Icons.Filled.CheckCircle
            }

            val animatedColor by animateColorAsState(targetValue = badgeColor.copy(alpha = 0.15f), label = "BadgeColor")

            Card(
                colors = CardDefaults.cardColors(containerColor = animatedColor),
                border = BorderStroke(2.dp, badgeColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = res.label.uppercase(),
                            color = badgeColor,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${res.confidence}%",
                            color = badgeColor,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "REASON",
                        color = badgeColor.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = res.reason,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Text(
                        text = "RECOMMENDED ACTION",
                        color = badgeColor.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = res.action,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
