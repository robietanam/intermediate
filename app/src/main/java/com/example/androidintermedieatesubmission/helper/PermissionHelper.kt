package com.example.androidintermedieatesubmission.helper

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


//fun allPermissionsGranted(context: Activity, permission: String) =
//    ContextCompat.checkSelfPermission(
//        context,
//        permission
//    ) == PackageManager.PERMISSION_GRANTED



fun requestPermissionLauncher(context: AppCompatActivity, permission: String)  {

    context.registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted: Boolean ->
        if (isGranted) {

//            Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Permission $permission request denied", Toast.LENGTH_LONG).show()
        }
    }.launch(permission)
}