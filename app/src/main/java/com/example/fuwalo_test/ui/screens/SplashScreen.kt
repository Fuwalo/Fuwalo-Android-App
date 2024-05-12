package com.example.fuwalo_test.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fuwalo_test.R
import com.example.fuwalo_test.ui.theme.BUTTON_GRAD_1
import com.example.fuwalo_test.ui.theme.BUTTON_GRAD_2
import com.example.fuwalo_test.ui.theme.BUTTON_GRAD_3
import com.example.fuwalo_test.ui.theme.BUTTON_GRAD_4
import com.example.fuwalo_test.ui.theme.GradientBox
import com.example.fuwalo_test.ui.theme.LIGHT_BLUE

@Composable
fun Splash(onNavigateToLogin: () -> Unit) {

    GradientBox(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.55f),
            ) {
                Image(
                    painterResource(id = R.drawable.splash_back),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.splash_text1),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
            )
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.with_fuwalo),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
            Text(
                text = stringResource(id = R.string.splash_description),
                color = LIGHT_BLUE,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 48.dp)
                    .background(brush = Brush.horizontalGradient(
                        listOf(BUTTON_GRAD_1, BUTTON_GRAD_2, BUTTON_GRAD_3, BUTTON_GRAD_4)
                    ), shape = ButtonDefaults.shape),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            ) {

                Text(text = "Get Started", color = Color.Black)

            }

        }
    }

}