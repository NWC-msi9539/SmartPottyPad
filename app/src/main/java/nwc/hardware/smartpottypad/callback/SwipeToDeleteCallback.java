package nwc.hardware.smartpottypad.callback;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import nwc.hardware.smartpottypad.adapters.HomeRoomAdapter;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private HomeRoomAdapter mAdapter;
    private boolean isEnabled = false;

    public SwipeToDeleteCallback(HomeRoomAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return isEnabled && super.isLongPressDragEnabled();
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return isEnabled && super.isItemViewSwipeEnabled();
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.removeRoom(viewHolder.getAdapterPosition());
    }
}