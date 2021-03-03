package ru.netology.motivationapp.swipecontroller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.motivationapp.adapter.PostsAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

abstract class SwipeHelper(
    context: Context,
    private val recyclerView: RecyclerView,
    internal val buttonWidth: Int,
    private val adapter: PostsAdapter,
    private val view: View
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private var buttonsList: MutableList<SwipeButton>? = null
    private var swipedPos = -1
    private var swipeThreshold = 0.5f
    private var buttonBuffer: MutableMap<Int, MutableList<SwipeButton>>
    lateinit var gestureDetector: GestureDetector
    lateinit var recoverQueue: LinkedList<Int>
    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            for (button in buttonsList!!) {
                if (button.onClick(e!!.x, e!!.y))
                    break
            }
            return true
        }
    }
    private val onTouchListener = View.OnTouchListener { v, event ->
        if (swipedPos < 0) {
            return@OnTouchListener false
        }
        val point = Point(event.rawX.toInt(), event.rawY.toInt())
        val swipeViewHolder = recyclerView?.findViewHolderForAdapterPosition(swipedPos)
        val swipedItem = swipeViewHolder?.itemView
        val rect = Rect()
        swipedItem?.getGlobalVisibleRect(rect)
        if (event.action == MotionEvent.ACTION_DOWN ||
            event.action == MotionEvent.ACTION_UP ||
            event.action == MotionEvent.ACTION_MOVE
        ) {
            if (rect.top < point.y && rect.bottom > point.y) {
                gestureDetector.onTouchEvent(event)
            } else {
                v.performClick()
                recoverQueue.add(swipedPos)
                swipedPos = -1
                recoverSwipedItem();
            }
        }
        false
    }

    init {
        this.buttonsList = ArrayList()
        this.gestureDetector = GestureDetector(context, gestureListener)
        this.buttonBuffer = HashMap()
        this.recyclerView.setOnTouchListener(onTouchListener)
        this.recoverQueue = IntLinkedList()
        attachToRecyclerView()
    }

    @Synchronized
    private fun recoverSwipedItem() {
        while (!recoverQueue.isEmpty()) {
            val pos = recoverQueue.poll()!!.toInt()
            if (pos > -1) {
                recyclerView.adapter?.notifyItemChanged(pos)
            }
        }
    }

    private fun attachToRecyclerView() {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    class IntLinkedList : LinkedList<Int>() {
        override fun contains(element: Int): Boolean {
            return false
        }

        override fun add(element: Int): Boolean {
            return if (contains(element)) {
                false
            } else {
                super.add(element)
            }
        }

        override fun lastIndexOf(element: Int): Int {
            return element
        }

        override fun remove(element: Int): Boolean {
            return false
        }

        override fun indexOf(element: Int): Int {
            return element
        }
    }

    abstract fun instantiateSwipeButtons(
        viewHolder: RecyclerView.ViewHolder, buffer: MutableList<SwipeButton>
    )

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (swipedPos != pos) {
            recoverQueue.add(swipedPos)
        }
        swipedPos = pos
        if (buttonBuffer.containsKey(swipedPos)) {
            buttonsList = buttonBuffer[swipedPos]!!
        } else {
            buttonsList?.clear()
        }
        buttonBuffer.clear()
        swipeThreshold = 0.5f * buttonsList!!.size.toFloat() * buttonWidth.toFloat()
        recoverSwipedItem()
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
        val pos = viewHolder.adapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView
        if (pos < 0) {
            swipedPos = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: MutableList<SwipeButton> = ArrayList()
                if (!buttonBuffer.containsKey(pos)) {
                    instantiateSwipeButtons(viewHolder, buffer)
                    buttonBuffer[pos] = buffer
                } else {
                    buffer = buttonBuffer[pos]!!
                }
                translationX = dX * buffer.size.toFloat() * buttonWidth.toFloat() / itemView.width
                drawButton(c, itemView, buffer, pos, translationX)
            }
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            translationX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    private fun drawButton(
        c: Canvas,
        itemView: View,
        buffer: MutableList<SwipeButton>,
        pos: Int,
        translationX: Float
    ) {

        var right = itemView.right.toFloat()
        val dButtonWidth = -1 * translationX / buffer.size
        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(
                c,
                RectF(left, itemView.top.toFloat(), right, itemView.bottom.toFloat()),
                pos
            )
            right = left
        }
    }
}