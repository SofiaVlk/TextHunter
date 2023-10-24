package com.sofiyavolkovaproyects.texthunter.ui.components

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun RequiresSimplePermission(
    permission: String = android.Manifest.permission.CAMERA,
    content: @Composable () -> Unit = {}
) {

    val permissionState = rememberPermissionState(permission)

    if (permissionState.status.isGranted) {
        content()
    } else {
        SideEffect {
            permissionState.launchPermissionRequest()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun RequiresMediaImagesPermission(
    content: @Composable () -> Unit = {}
) {

// Permission request logic
   val arrayPermissions = if (VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE) {
        arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
    } else if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
        arrayOf(READ_MEDIA_IMAGES)
    } else {
        arrayOf(READ_EXTERNAL_STORAGE)
    }

    val permissionsState: MutableList<PermissionState> = mutableListOf()

    arrayPermissions.forEach {
        permissionsState.add(rememberPermissionState(it))
    }

    if (permissionsState.all { it.status.isGranted }) {
        content()
    } else {
        SideEffect {
            permissionsState
                .filter { !it.status.isGranted }
                .forEach { it.launchPermissionRequest() }
        }
    }
}