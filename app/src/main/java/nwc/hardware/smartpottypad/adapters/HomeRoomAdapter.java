package nwc.hardware.smartpottypad.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.datas.Bed;
import nwc.hardware.smartpottypad.datas.Room;

public class HomeRoomAdapter extends RecyclerView.Adapter<HomeRoomAdapter.MyViewHolder>{
    private final String TAG = "HomeRoomAdapter";
    private List<Room> items;
    private Context mContext;
    private int departFloor = 0;


    public HomeRoomAdapter(List<Room> items, Context mContext, int departFloor) {
        this.items = items;
        this.mContext = mContext;
        this.departFloor = departFloor;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.roomitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(linearLayout);
        return myViewHolder;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Room room = items.get(position);
        HomeBedAdapter adapter = new HomeBedAdapter(room.getBeds(), mContext);
        holder.roomNoTXT.setText("" + ((100 * departFloor) + room.getIndex()));
        holder.bedRCC.setAdapter(adapter);
        holder.addBedBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "On Click addBedBTN Size --> " + room.getBeds().size());
                room.getBeds().add(new Bed(room.getBeds().size() + 1));
                adapter.setBeds(room.getBeds());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addRoom(Room room){
        if(!items.contains(room)) {
            items.add(room);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void setRooms(List<Room> rooms){
        items = rooms;
        notifyDataSetChanged();
    }

    public void removeRoom(Room room){
        if(items.contains(room)){
            int index = items.indexOf(room);
            items.remove(room);
            notifyItemRemoved(index);
        }
    }

    public void resetRoom(){
        items.clear();
        notifyDataSetChanged();
    }

    public Room getItem(int position){
        return items.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView roomNoTXT;
        public RecyclerView bedRCC;
        public ImageButton addBedBTN;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNoTXT = itemView.findViewById(R.id.roomNoTXT);
            addBedBTN = itemView.findViewById(R.id.AddBedBTN);

            bedRCC = itemView.findViewById(R.id.bedRCC);
            bedRCC.setHasFixedSize(true);
            bedRCC.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

}
