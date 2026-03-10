package com.example.gdminterview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gdminterview.ui.components.audiowidget.AudioWidget
import com.example.gdminterview.ui.components.audiowidget.AudioWidgetViewModel
import com.example.gdminterview.ui.theme.GDMInterviewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GDMInterviewTheme {
                GDMInterviewApp()
            }
        }
    }
}

@Composable
fun GDMInterviewApp(
    fullViewModel: AudioWidgetViewModel = viewModel(key = "full"),
    mediumViewModel: AudioWidgetViewModel = viewModel(key = "medium"),
    compactViewModel: AudioWidgetViewModel = viewModel(key = "compact"),
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    "Full size",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
            item {
                AudioWidget(fullViewModel, modifier = Modifier.fillMaxWidth())
            }
            item {
                Text(
                    "Medium (480dp) - same size as Figma spec",
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            item {
                AudioWidget(mediumViewModel, modifier = Modifier.width(480.dp))
            }
            item {
                Text(
                    "Compact (320dp)",
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            item {
                AudioWidget(compactViewModel, modifier = Modifier.width(320.dp))
            }
        }
    }
}