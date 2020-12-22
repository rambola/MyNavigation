package com.rr.mynavigation.service

import android.R.layout
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.PopupWindow
import com.rr.mynavigation.R


class AssistTouchService : Service() {
    private lateinit var mWindowManager: WindowManager
    private lateinit var mChatHeadLayout: View
    //private lateinit var mExpandedAssistView: View
    private lateinit var mCollapseAssistView: View
    private lateinit var window: PopupWindow

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        //Inflate the chat head layout we created
        mChatHeadLayout = LayoutInflater.from(this).inflate(
                R.layout.chat_head, null
        )

        mCollapseAssistView = mChatHeadLayout.findViewById(R.id.collapse_view)
//        mExpandedAssistView = mAssistTouchView.findViewById(R.id.expanded_container)

        val LAYOUT_FLAG: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        //Add the view to the window.
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        )

        //Specify the chat head position
        //Initially view will be added to top-left corner
        params.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        params.x = 5
        params.y = -150

        //Add the view to the window
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mChatHeadLayout, params)

        //Set the close button.

        //Set the close button.
        /*val closeButton: ImageView = mAssistTouchView.findViewById(R.id.close_btn) as ImageView
        closeButton.setOnClickListener {
            //close the service and remove the chat head from the window
            stopSelf()
        }*/

        //Drag and move chat head using user's touch action.
        val chatHeadImage =
            mChatHeadLayout.findViewById(R.id.collapsed_iv) as ImageView
        chatHeadImage.setOnTouchListener(object : OnTouchListener {
            private var lastAction = 0
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY: Float = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        //remember the initial position.
                        initialX = params.x
                        initialY = params.y

                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        lastAction = event.action
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        /*if (lastAction == MotionEvent.ACTION_DOWN) {
                            //Open the chat conversation click.
                            val intent = Intent(this, ChatActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            //close the service and remove the chat heads
                            stopSelf()
                            mExpandedAssistView.visibility = View.VISIBLE
                        }*/

                        val Xdiff = (event.rawX - initialTouchX).toInt()
                        val Ydiff = (event.rawY - initialTouchY).toInt()

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                Log.e("AssistTouchService", "onclick...");
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
//                                mCollapseAssistView.visibility = View.INVISIBLE
//                                mExpandedAssistView.visibility = View.VISIBLE
//                                val intent = Intent(applicationContext, MainActivity::class.java)
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                intent.putExtra("showDialog", true);
//                                startActivity(intent)
                                mCollapseAssistView.visibility = View.GONE
                                showNavigationDialog()
//                                showPopup()
                            }
                        }

                        lastAction = event.action
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mChatHeadLayout, params)
                        lastAction = event.action
                        return true
                    }
                }

                return false
            }
        })
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private fun isViewCollapsed(): Boolean {
        return mCollapseAssistView.visibility == View.VISIBLE
    }

    fun showNavigationDialog() {
        val dialogLayout = LayoutInflater.from(applicationContext).inflate(
                R.layout.fragment_dialog, null
        )

        dialogLayout.findViewById<ImageView>(R.id.)

        val builder = AlertDialog.Builder(applicationContext, R.style.CustomDialogTheme)
            .setView(dialogLayout)
//        builder.setTitle("Test dialog")
//        builder.setIcon(R.drawable.icon)
//        builder.setMessage("Content");

        val LAYOUT_FLAG: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val alert = builder.create() as AlertDialog
        alert.window?.setType(LAYOUT_FLAG)
//        alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alert.setOnDismissListener {
            mCollapseAssistView.visibility = View.VISIBLE
        }

        alert.show()
        
        val displayMetrics = DisplayMetrics()
        mWindowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alert.window?.attributes)
        Log.e("width", "displayWidth: $displayWidth")
        Log.e("height", "displayHeight: $displayHeight")
        val dialogWindowWidth = (displayWidth * 0.55f).toInt()
        //val dialogWindowHeight = (displayHeight * 0.1f).toInt()
        Log.e("width", "dialogWindowWidth: $dialogWindowWidth")
        //Log.e("height", "dialogWindowHeight: $dialogWindowHeight")
        layoutParams.width = dialogWindowWidth
        //layoutParams.height = dialogWindowHeight
        alert.window?.attributes = layoutParams
    }

    /*fun showPopup() {
        val layout = LayoutInflater.from(applicationContext).inflate(R.layout.fragment_dialog, null)
        window = PopupWindow(layout, 310, 450, true)

        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.isOutsideTouchable = true
        window.showAtLocation(layout, Gravity.CENTER, 40, 60)
    }*/

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager.removeView(mChatHeadLayout)
    }
}