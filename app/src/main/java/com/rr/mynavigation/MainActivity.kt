package com.rr.mynavigation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rr.mynavigation.service.AssistTouchService


@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity() {
    private val DRAW_OVER_OTHER_APP_PERMISSION_CODE = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val showDialog = intent.getBooleanExtra("showDialog", false)
        if (showDialog)
            DialogFragment().show(supportFragmentManager, "DialogFragment")
        else {
            //Check if the application has draw over other apps permission or not?
            //This permission is by default available for API<23. But for API > 23
            //you have to ask for the permission in runtime.
            if (!Settings.canDrawOverlays(this)) {
                //If the draw over permission is not available open the settings screen
                //to grant the permission.
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_CODE)
            } else {
                initializeView()
            }
        }
    }

    /**
     * Set and initialize the view elements.
     */
    private fun initializeView() {
        findViewById<View>(R.id.notify_me).setOnClickListener {
            startService(Intent(applicationContext, AssistTouchService::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_CODE) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initializeView()
            } else { //Permission is not available
                Toast.makeText(
                    this,
                    "Draw over other app permission not available. Closing the application",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}