package com.shiliu.caishifu.listener;


import androidx.recyclerview.widget.RecyclerView;

public interface OnTouchCallbackListener {

    /**
     * move the dragged item from its old position to
     * the new position.
     *
     * @param srcViewHolder
     * @param targetViewHolder
     * @return
     */
    boolean onItemMove(RecyclerView.ViewHolder srcViewHolder, RecyclerView.ViewHolder targetViewHolder);

    /**
     * When the ViewHolder dragged by the ItemTouchHelper is changed.
     *
     * @param viewHolder
     * @param actionState
     */
    void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState);

    /**
     * Called by the ItemTouchHelper when the user interaction with an element is over and it
     * also completed its animation.
     */
    void onClearView();

    /**
     * In deleteing state
     *
     * @param delete true delete,false normal
     */
    void onChildDeleteState(boolean delete);

    /**
     * delete current item
     */
    void onChildDelete(RecyclerView.ViewHolder viewHolder);

}
