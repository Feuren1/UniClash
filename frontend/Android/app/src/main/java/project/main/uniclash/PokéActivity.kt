package project.main.uniclash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.main.uniclash.ui.theme.UniClashTheme

class PokéActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UniClashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clickable {
                                        // TODO: Handle profile click
                                    }
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                        ) {
                            Column {
                                // Subscribe to "Red" channel
                                SubscribeCard("Red", "red")

                                // Unsubscribe from "Red" channel
                                UnsubscribeCard("Red", "red")

                                // Subscribe to "Blue" channel
                                SubscribeCard("Blue", "blue")

                                // Unsubscribe from "Blue" channel
                                UnsubscribeCard("Blue", "blue")
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SubscribeCard(title: String, channel: String) {
        MenuCard(
            title = "Subscribe $title",
            painter = painterResource(id = R.drawable.borzoi), // Change the icon as needed
            onClick = { subscribeToTopic(channel) }
        )
    }

    @Composable
    fun UnsubscribeCard(title: String, channel: String) {
        MenuCard(
            title = "Unsubscribe $title",
            painter = painterResource(id = R.drawable.borzoi), // Change the icon as needed
            onClick = { unsubscribeFromTopic(channel) }
        )
    }

    @Composable
    fun MenuCard(title: String, painter: Painter, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth()

                .clickable { onClick() }
        ) {
            Row(modifier = Modifier.padding(all = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }

    // Mock functions for Firebase Messaging
    private fun subscribeToTopic(topic: String) {
        // Implement Firebase subscription logic
        // This is a mock function, replace it with actual Firebase logic
        println("Subscribed to $topic")
    }

    private fun unsubscribeFromTopic(topic: String) {
        // Implement Firebase unsubscription logic
        // This is a mock function, replace it with actual Firebase logic
        println("Unsubscribed from $topic")
    }

    @Preview(showBackground = true)
    @Composable
    fun MenuActivityPreview() {
        UniClashTheme {
            // Mocking the content as the actual content relies on Firebase, which can't be previewed
            MenuCard("Subscribe Red", painterResource(id = R.drawable.borzoi)) {}
        }
    }
}
