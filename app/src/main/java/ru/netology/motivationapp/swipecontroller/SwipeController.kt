package ru.netology.motivationapp.swipecontroller

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView


class SwipeController : ItemTouchHelper.Callback() {
    private var swipeBack = false
    private var buttonShowedState = ButtonsState.GONE
    private val buttonWidth = 300f
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {

        return makeMovementFlags(0, LEFT)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonShowedState != ButtonsState.GONE;
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                    val dX = dX.coerceAtMost(-buttonWidth)
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            } else {
                setTouchListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
        if (buttonShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
        drawButtonsEdit(c, viewHolder)
        drawButtonsDel(c, viewHolder)

        currentItemViewHolder = viewHolder
    }

    private fun setTouchListener (
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            swipeBack = (event.action == MotionEvent.ACTION_CANCEL ||
                    event.action == MotionEvent.ACTION_UP)
            if (swipeBack) {
               if (dX < -buttonWidth) {
                    buttonShowedState = ButtonsState.RIGHT_VISIBLE

                }
                if (buttonShowedState != ButtonsState.GONE) {
                    setTouchDownListener(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    setItemsClickable(recyclerView, false)
                }
            }
            false
        }
    }

    private fun setTouchDownListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
        ) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
            false
        }
    }

    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    0f,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                recyclerView.setOnTouchListener { v, event ->
                    false
                }
                setItemsClickable(recyclerView, true)
                swipeBack = false
                buttonShowedState = ButtonsState.GONE
            }
            false
        }
    }

    private fun setItemsClickable(
        recyclerView: RecyclerView,
        valueClickable: Boolean
   ) {
        for (i in 0 until recyclerView.childCount) {
            valueClickable.also {
                recyclerView.getChildAt(i).isClickable = it
            }
        }
    }

    private fun drawButtonsDel(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder
    ) {
        val buttonWidthWithoutPadding = buttonWidth - 150 // отступ от ячейки
        val corners = 0f
        val itemView = viewHolder.itemView
        val p = Paint()
        val rightButton: RectF by lazy {
            RectF(
                itemView.right - buttonWidthWithoutPadding,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
        }
        p.color = Color.RED
        c.drawRoundRect(rightButton, corners, corners, p)
        drawText("Del", c, rightButton, p)

    }

    private fun drawButtonsEdit(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder
    ) {
        val buttonWidthWithoutPadding = buttonWidth - 150 // отступ от ячейки
        val corners = 0f
        val itemView = viewHolder.itemView
        val p = Paint()
        val rightButton = RectF(
                itemView.right.toFloat() - buttonWidth,
                itemView.top.toFloat(),
                itemView.right.toFloat() - buttonWidthWithoutPadding,
                itemView.bottom.toFloat()
            )

        p.color = Color.GRAY
        c.drawRoundRect(rightButton, corners, corners, p)
        drawText("Edit", c, rightButton, p)

    }





    private fun drawText(text: String, c: Canvas, button: RectF, p: Paint) {
        val textSize = 60f
        p.color = Color.WHITE
            p.isAntiAlias = true
            p.textSize = textSize
       val textWidth = p.measureText(text)
        c.drawText(text, button.centerX() - (textWidth/2), button.centerY() + (textSize/2), p)
    }


    fun onDraw(c: Canvas) {
        currentItemViewHolder?.let {
            drawButtonsEdit(c, it)
            drawButtonsDel(c, it)
        }
    }

}

enum class ButtonsState {
    GONE,
    RIGHT_VISIBLE
}