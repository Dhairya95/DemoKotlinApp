package com.example.demokotlin

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.demokotlin.ui.theme.DemoKotlinTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : ComponentActivity() {

    // Change these values for your repo
    private val currentVersion = "1.0.2"
    private val githubApiUrl = "https://api.github.com/repos/Dhairya95/DemoKotlinApp/releases/latest"
    private val apkDownloadUrl = "https://github.com/Dhairya95/DemoKotlinApp/releases/latest/download/app-debug.apk"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoKotlinTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    UpdateUI(
                        currentVersion = currentVersion,
                        onCheckUpdate = { checkForUpdate(this) }
                    )
                }
            }
        }
    }

    private fun checkForUpdate(context: Context) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = URL(githubApiUrl).readText()
                val latestVersion = JSONObject(response).getString("tag_name")

                withContext(Dispatchers.Main) {
                    if (latestVersion != currentVersion) {
                        Toast.makeText(context, "New version available: $latestVersion", Toast.LENGTH_LONG).show()
                        downloadAndInstall(context)
                    } else {
                        Toast.makeText(context, "App is up to date", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error checking update: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun downloadAndInstall(context: Context) {
        val request = DownloadManager.Request(Uri.parse(apkDownloadUrl))
            .setTitle("Downloading Update")
            .setDescription("Please wait...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "update.apk")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
        Toast.makeText(context, "Downloading APK... install from notification", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun UpdateUI(currentVersion: String, onCheckUpdate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Current Version: $currentVersion", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onCheckUpdate() }) {
            Text("Check for Update")
        }
        Text(text = "Congrats. You updated to 1.0.2", style = MaterialTheme.typography.titleMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUpdateUI() {
    DemoKotlinTheme {
        UpdateUI("1.0.0") {}
    }
}