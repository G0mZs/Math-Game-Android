package pt.isec.a21280210_a21280183_a21280330.tp_amov.game.singleplayer

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.GridView
import kotlin.math.abs

open class OnSwipeTouchSpListener(
    private val context: Context,
    private val gridView: GridView,
    private val viewModel: SinglePlayerGameViewModel
) : View.OnTouchListener {

    private val gestureDetector: GestureDetector

    companion object {

        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {


        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

            var result = false
            val position : Int = gridView.pointToPosition(e1.x.toInt(), e1.y.toInt())

            try {

                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x

                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                        //Horizontal swipe
                        val row : Int = (position / gridView.numColumns) + 1

                        if(row == 1 || row == 3 || row == 5){
                            viewModel.makeMove(row,"row",context)
                            onSwipe()
                        }

                        result = true
                    }
                } else
                    if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {

                        //Vertical swipe
                        val column : Int = (position % gridView.numColumns) + 1

                        if(column == 1 || column == 3 || column == 5){
                            viewModel.makeMove(column,"column",context)
                            onSwipe()
                        }

                        result = true
                    }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }
    }

    open fun onSwipe(){

    }
}

