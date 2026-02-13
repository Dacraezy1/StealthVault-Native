package com.dacraezy1.stealthvaultnative

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// --- NORD THEME PALETTE ---
val Nord0 = Color(0xFF2E3440) 
val Nord1 = Color(0xFF3B4252) 
val Nord3 = Color(0xFF4C566A) 
val Nord4 = Color(0xFFD8DEE9) 
val Nord6 = Color(0xFFECEFF4) 
val Nord8 = Color(0xFF88C0D0) 
val Nord11 = Color(0xFFBF616A) 
val Nord14 = Color(0xFFA3BE8C) 

class MainActivity : ComponentActivity() {

    init {
        System.loadLibrary("stealthvault")
    }

    external fun nEncryptFile(path: String, pass: String): Boolean
    external fun nPanicWipe()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StealthVaultTheme {
                MainScreen(
                    onEncrypt = { path -> nEncryptFile(path, "temp_session_key") },
                    onPanic = { nPanicWipe() }
                )
            }
        }
    }
}

@Composable
fun StealthVaultTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Nord0,
            surface = Nord1,
            primary = Nord8,
            onBackground = Nord4,
            onSurface = Nord4,
            error = Nord11
        ),
        typography = Typography(
            bodyLarge = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp
            ),
            headlineMedium = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onEncrypt: (String) -> Boolean,
    onPanic: () -> Unit
) {
    val context = LocalContext.current
    var terminalLog by remember { mutableStateOf("> System Initialized...\n> User: Dacraezy1\n> Repo: StealthVault-Native") }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            terminalLog += "\n> Target acquired: ${it.lastPathSegment}"
            terminalLog += "\n> Handing off to Native Layer..."
            val success = onEncrypt(it.toString())
            terminalLog += if (success) "\n> [SUCCESS] AES-256-GCM Encrypted." else "\n> [FAILURE] Aborted."
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("STEALTH VAULT // ROOT", color = Nord4) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Nord0)
            )
        },
        containerColor = Nord0
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TerminalView(log = terminalLog, modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(16.dp))

            Text("> ENCRYPTED_CONTAINER", color = Nord8, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            EncryptedGalleryGrid(modifier = Modifier.height(200.dp))

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { launcher.launch(arrayOf("*/*")) },
                    colors = ButtonDefaults.buttonColors(containerColor = Nord3),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Text("IMPORT FILE", color = Nord6)
                }

                Button(
                    onClick = { 
                        onPanic()
                        terminalLog += "\n> [PANIC] MEMORY WIPED. CACHE PURGED."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Nord11),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Text("WIPE CACHE", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun TerminalView(log: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Nord3, RoundedCornerShape(4.dp))
            .background(Nord1)
            .padding(12.dp)
    ) {
        Text(
            text = log,
            color = Nord14, 
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

@Composable
fun EncryptedGalleryGrid(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .background(Nord0)
            .border(1.dp, Nord3)
    ) {
        items(8) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(Nord3),
                contentAlignment = Alignment.Center
            ) {
                Text("LOCKED", fontSize = 8.sp, color = Nord4)
            }
        }
    }
}
