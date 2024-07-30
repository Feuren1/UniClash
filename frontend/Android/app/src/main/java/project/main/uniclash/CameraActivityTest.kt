package project.main.uniclash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import android.annotation.SuppressLint
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import project.main.uniclash.ui.theme.UniClashTheme


class CameraActivityTest: ComponentActivity() {
    private val TAG = CameraActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniClashTheme {
                // A surface container using the 'background' color from the theme
            MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(){
    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    MainContent(
        hasPermission = cameraPermissionState.status.isGranted,
        onRequestPermission = cameraPermissionState::launchPermissionRequest
    )
}

@Composable
fun MainContent(hasPermission: Boolean, onRequestPermission: () -> Unit) {
    if (hasPermission) {
        CameraScreen()
    }
    else {
        NoPermissionScreen(onRequestPermission)
    }

}

@Composable
fun CameraScreen(
    //viewModel: CameraViewModel = viewModel()
) {
    CameraContent()
}
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CameraContent() {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {LifecycleCameraController(context)}

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val mainExecutor = ContextCompat.getMainExecutor(context)
                    cameraController.takePicture(mainExecutor, object: ImageCapture.OnImageCapturedCallback(){
                        override fun onCaptureSuccess(image: ImageProxy) {

                        }
                    }) }

            ) {

            }
        }
    ) {  paddingValues: PaddingValues ->
    AndroidView(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(paddingValues),
        factory = { context ->
        PreviewView(context).apply{
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT,MATCH_PARENT )
            setBackgroundColor(0)
            scaleType = PreviewView.ScaleType.FILL_START

        }.also {PreviewView ->
            PreviewView.controller = cameraController
            cameraController.bindToLifecycle(lifecycleOwner)

        }

    })
    }
}

@Composable
fun NoPermissionScreen(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "The Camera Permission is needed to use this part of the App!")
        Button(onClick = { onRequestPermission() }) {
            Icon(imageVector = Icons.Default.Build, contentDescription = "Camera")
            Text(text = "Grant Permission")
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
    UniClashTheme {
        Greeting("Android")
    }
}