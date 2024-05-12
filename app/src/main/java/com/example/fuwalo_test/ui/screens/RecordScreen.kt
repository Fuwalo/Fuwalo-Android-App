package com.example.fuwalo_test.ui.screens

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fuwalo_test.R
import com.example.fuwalo_test.ui.MidiViewModel
import com.example.fuwalo_test.ui.theme.BOX_BACKGROUND
import com.example.fuwalo_test.ui.theme.DarkBlue
import com.example.fuwalo_test.ui.theme.Purple
import com.example.fuwalo_test.utils.Wave
import java.io.File
import java.io.IOException


@Composable
fun Record(
    viewModel: MidiViewModel,
    filePickerLauncher: ActivityResultLauncher<Intent>?,
    onNavigateToHome: () -> Unit
) {

    val context = LocalContext.current
    val selectedUri by viewModel.userFlow.collectAsStateWithLifecycle()
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val mediaPlayer = remember {
        MediaPlayer()
    }
    val wave = remember {
        Wave(context.getExternalFilesDir(null)?.absolutePath)
    }
    var recordedOutputFile = remember {
        mutableStateOf("")
    }
    var isRecording by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            recordedOutputFile.value = startStopRecording(wave, context, isRecording)
            isRecording = !isRecording
        }
    }

    val isPlaying = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.convert_audio_to_music_sheet),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
        )
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.with_fuwalo),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5F)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(DarkBlue, Color.Black)
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 100.dp)
                    .clip(shape = RoundedCornerShape(26.dp, 26.dp, 26.dp, 26.dp))
                    .background(color = BOX_BACKGROUND)
                    .padding(16.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "audio/*"
                        }
                        filePickerLauncher?.launch(intent)
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painterResource(id = R.drawable.upload_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                    )

                    Text(
                        text = "Choose File",
                        modifier = Modifier.padding(start = 32.dp, top = 8.dp),
                        color = Color.White
                    )
                }

            }
            if (uiState is MidiViewModel.UiState.IDLE) {
                if (selectedUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                            .clip(shape = RoundedCornerShape(26.dp, 26.dp, 26.dp, 26.dp))
                            .background(color = BOX_BACKGROUND)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                bitmap = selectedUri?.let {
                                    loadThumbnail(
                                        context,
                                        it
                                    )?.asImageBitmap()
                                }
                                    ?: ImageBitmap(40, 40),
                                contentDescription = "",
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .clip(shape = RoundedCornerShape(26.dp, 26.dp, 26.dp, 26.dp))
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp)
                            ) {
                                Text(
                                    text = selectedUri?.let { loadSongName(context, it) } ?: "",
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .heightIn(max = 40.dp) // Fixing the height to 40dp
                                        .fillMaxWidth(),
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                            }
                            val icon = if (isPlaying.value) {
                                R.drawable.baseline_stop_24
                            } else {
                                R.drawable.baseline_play_arrow_24
                            }
                            Image(
                                imageVector = ImageVector.vectorResource(id = icon),
                                contentDescription = "",
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .clickable {
                                        if (isPlaying.value) {
                                            // Stop playback
                                            mediaPlayer.stop()
                                            mediaPlayer.reset()
                                            isPlaying.value = false
                                        } else {
                                            // Start playback
                                            selectedUri?.let { uri ->
                                                mediaPlayer.apply {
                                                    setDataSource(context, uri)
                                                    isLooping = true
                                                    prepare()
                                                    start()
                                                }
                                            }
                                            isPlaying.value = true
                                        }
                                    }
                            )
                        }

                    }

                    ConvertButton(viewModel, context, selectedUri!!)
                }
                
                if (!isRecording && recordedOutputFile.value.isNotEmpty()){
                    Spacer(Modifier.height(150.dp))
                    ConvertButton(viewModel, context, File(recordedOutputFile.value).toUri())
                }
                if (isRecording){
                    Spacer(Modifier.height(150.dp))
                    Text(text = "Listening...", color = Color.White, fontSize = 30.sp)
                }
            } else if (uiState is MidiViewModel.UiState.LOADING) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState is MidiViewModel.UiState.DONE) {

                val file = (uiState as MidiViewModel.UiState.DONE).file

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                        .clip(shape = RoundedCornerShape(26.dp, 26.dp, 26.dp, 26.dp))
                        .background(color = BOX_BACKGROUND)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        ) {
                            Text(
                                text = file.name,
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .heightIn(max = 40.dp) // Fixing the height to 40dp
                                    .fillMaxWidth(),
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                        Image(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_file_download_24),
                            contentDescription = "",
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    val fileUri = FileProvider.getUriForFile(
                                        context,
                                        context.packageName + ".provider",
                                        file
                                    )
                                    intent.setDataAndType(fileUri, "audio/midi")
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    context.startActivity(intent)
                                }
                        )
                    }
                }

            }


        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                val icon = if (isRecording) R.drawable.record_bottom_enabled else R.drawable.record_bottom
                Image(
                    painterResource(id = icon),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth()
                )


                Image(
                    painterResource(id = R.drawable.record_button_unselected),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(bottom = 36.dp)
                        .width(96.dp)
                        .height(96.dp)
                        .align(Alignment.BottomCenter)
                        .clickable {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                recordedOutputFile.value =
                                    startStopRecording(wave, context, isRecording)
                                Log.d("Babakk", recordedOutputFile.value)
                                isRecording = !isRecording
                            } else {
                                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }

                )

            }


        }

    }
}

private fun startStopRecording(wave: Wave, context: Context, isRecording: Boolean): String {
    if (isRecording) {
        wave.stopRecording()
        /*mediaRecorder.apply {
            stop()
            release()
        }*/
        return context.getExternalFilesDir(null)?.absolutePath + "/final_record.wav"
    } else {
        wave.startRecording()
        return context.getExternalFilesDir(null)?.absolutePath + "/final_record.wav"
    }
}

fun loadThumbnail(context: Context, uri: Uri): Bitmap? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(context, uri)
    val data = retriever.embeddedPicture
    retriever.release()
    return if (data == null) {
        null
    } else BitmapFactory.decodeByteArray(data, 0, data.size)
}

fun loadSongName(context: Context, uri: Uri): String? {
    val contentResolver: ContentResolver = context.contentResolver
    val cursor = contentResolver.query(uri, null, null, null, null)
    if (cursor != null && cursor.moveToFirst()) {
        val displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        cursor.close()
        return displayName
    }
    return ""
}

@Composable
fun ConvertButton(viewModel: MidiViewModel, context: Context , selectedUri:Uri){
    Button(
        onClick = {
            viewModel.transcribeAudio(context, selectedUri, "myAudio")
        },
        colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
        modifier = Modifier
            .width(150.dp)
            .padding(horizontal = 8.dp, vertical = 24.dp)
            .border(width = 1.dp, color = Purple, shape = RoundedCornerShape(32.dp))
    ) {
        Text(
            text = "Convert", color = Color.White
        )
    }
}


/*
@Preview
@Composable
fun recordPreview(){
    com.example.fuwalo_test.ui.screens.Record(null) {

    }
}*/
