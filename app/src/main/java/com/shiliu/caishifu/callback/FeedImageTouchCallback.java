package com.shiliu.caishifu.callback;

import android.graphics.Canvas;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.shiliu.caishifu.R;
import com.shiliu.caishifu.adapter.FeedImageAdapter;
import com.shiliu.caishifu.listener.OnTouchCallbackListener;


public class FeedImageTouchCallback extends ItemTouchHelper.Callback {

    private OnTouchCallbackListener touchCallbackListener;
    private boolean actionUp;

    public void setTouchCallbackListener(OnTouchCallbackListener touchCallbackListener) {
        this.touchCallbackListener = touchCallbackListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        if (viewHolder.getItemViewType() != FeedImageAdapter.TYPE_ADD) {
            dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.RIGHT;
        }
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (touchCallbackListener != null) {
            return touchCallbackListener.onItemMove(viewHolder, target);
        }
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (touchCallbackListener != null) {
            touchCallbackListener.onSelectedChanged(viewHolder, actionState);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        actionUp = false;
        if (touchCallbackListener != null) {
            touchCallbackListener.onClearView();
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        boolean deleteState = dY >= recyclerView.getHeight() - viewHolder.itemView.getResources().getDimensionPixelSize(R.dimen.dimen_size_63) - viewHolder.itemView.getBottom() ? true : false;
        if (touchCallbackListener != null) {
            touchCallbackListener.onChildDeleteState(deleteState);
            if (deleteState && actionUp) {
                actionUp = false;
                touchCallbackListener.onChildDelete(viewHolder);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        actionUp = true;
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
    }
}
