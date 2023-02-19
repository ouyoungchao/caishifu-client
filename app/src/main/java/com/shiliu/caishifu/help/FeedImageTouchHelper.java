package com.shiliu.caishifu.help;


import androidx.recyclerview.widget.ItemTouchHelper;

public class FeedImageTouchHelper extends ItemTouchHelper {

    /**
     * Creates an ItemTouchHelper that will work with the given Callback.
     * <p>
     * You can attach ItemTouchHelper to a RecyclerView via
     * an onItemTouchListener and a Child attach / detach listener to the RecyclerView.
     *
     * @param callback The Callback which controls the behavior of this touch helper.
     */
    public FeedImageTouchHelper(Callback callback) {
        super(callback);
    }

}
