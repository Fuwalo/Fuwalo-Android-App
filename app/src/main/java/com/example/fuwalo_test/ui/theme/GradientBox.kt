package com.example.fuwalo_test.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MovableContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun GradientBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
){
    Box(modifier = modifier.background(brush = Brush.linearGradient(
        listOf(Color.Black, DarkBlue)
    ))) {
        content()
    }
}

@Composable
fun GradientBox2(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
){
    Box(modifier = modifier.background(brush = Brush.linearGradient(
        listOf(DarkBlue,Color.Black)
    ))) {
        content()
    }
}