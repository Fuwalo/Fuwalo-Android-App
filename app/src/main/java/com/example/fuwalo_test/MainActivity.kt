package com.example.fuwalo_test

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fuwalo_test.ui.MidiViewModel
import com.example.fuwalo_test.ui.screens.Login
import com.example.fuwalo_test.ui.screens.Piano
import com.example.fuwalo_test.ui.screens.Record
import com.example.fuwalo_test.ui.screens.Splash
import com.example.fuwalo_test.ui.theme.FuwalotestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MidiViewModel by viewModels()

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri: Uri? = data?.data
                if (uri != null) {


                    viewModel.updateUri(uri)
                    Log.d("Fuck", uri.toString())


                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT,
            )
        )
        setContent {
            FuwalotestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main(viewModel, filePickerLauncher)
                }
            }
        }
    }
}

@Composable
fun Main(
    viewModel: MidiViewModel,
    filePickerLauncher: ActivityResultLauncher<Intent>,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "splash"
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable("splash") {
            Splash {
                navController.navigate("login")
            }
        }
        composable("login") {
            Login {
                navController.navigate("record")
            }
        }
        composable("record") {
            Record(viewModel = viewModel, filePickerLauncher = filePickerLauncher) {
                navController.navigate("home")
            }
        }
        composable("piano") {
            Piano()
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FuwalotestTheme {
        Greeting("Android")
    }
}