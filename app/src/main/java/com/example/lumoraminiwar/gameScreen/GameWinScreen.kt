package com.example.lumoraminiwar.gameScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumoraminiwar.ui.theme.LumoraMiniWarTheme

@Composable
fun GameWinScreen(
    currentLevel: Int,
    onPlayAgainClick: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "LEVEL $currentLevel CLEARED!",
                color = Color(0xFFFF6A00),
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You Eliminated All Enemies!",
                color = Color(0xFFFF6A00),
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(35.dp))
            Button(
                onClick = { onPlayAgainClick() },
                modifier = Modifier
                    .width(220.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6A00)
                ),
                shape = RoundedCornerShape(13.dp)
            ) {
                Text(
                    text = "NEXT LEVEL",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameWin() {
    LumoraMiniWarTheme {
        GameWinScreen(
            currentLevel = 1,
            onPlayAgainClick = {}
        )

    }
}