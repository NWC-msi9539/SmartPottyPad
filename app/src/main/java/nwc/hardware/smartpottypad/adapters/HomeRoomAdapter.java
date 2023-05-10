package nwc.hardware.smartpottypad.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.datas.Bed;
import nwc.hardware.smartpottypad.datas.Room;
import nwc.hardware.smartpottypad.fragments.home.HomeFragment;
import nwc.hardware.smartpottypad.tasks.SetPreferences;

public class HomeRoomAdapter extends RecyclerView.Adapter<HomeRoomAdapter.MyViewHolder>{
    private final String TAG = "HomeRoomAdapter";
    private List<Room> items;
    private Context mContext;
    private int departFloor = 0;

    private boolean isAnim = false;
    private HomeActivity parent;
    private RecyclerView parentView;

    private Map<DatabaseReference, ValueEventListener> references = new HashMap<>();

    public HomeRoomAdapter(List<Room> items, Context mContext, int departFloor, HomeActivity parent, RecyclerView parentView) {
        this.items = items;
        this.mContext = mContext;
        this.departFloor = departFloor;
        this.parent = parent;
        this.parentView = parentView;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.roomitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(linearLayout);
        return myViewHolder;
    }

    @Override
    public void onViewRecycled(@androidx.annotation.NonNull HomeRoomAdapter.MyViewHolder holder) {
        int position = holder.getAdapterPosition();
        if ((position != RecyclerView.NO_POSITION && parentView.findViewHolderForAdapterPosition(position) != null) || position == -1) {
            // 아이템이 여전히 화면에 표시되고 있으므로 참조를 제거하지 않습니다.
            return;
        }

        Room room = items.get(position);

        synchronized (references) {
            DatabaseReference roomInfoRef = FirebaseDatabase.getInstance().getReference("users/" + SetPreferences.databaseKey + "/rooms/" + (room.getIndex() - 1));
            if(references.containsKey(roomInfoRef)){
                roomInfoRef.removeEventListener(references.get(roomInfoRef));
                references.remove(roomInfoRef);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Room room = items.get(position);

        DatabaseReference dataBaseRooms = FirebaseDatabase.getInstance().getReference("users").child(SetPreferences.databaseKey).child("rooms").child("" + (room.getIndex() - 1));
        synchronized (references) {
            if (references.containsKey(dataBaseRooms)) {
                dataBaseRooms.removeEventListener(references.get(dataBaseRooms));
                references.remove(dataBaseRooms);
            }

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    Room changedRoom = snapshot.getValue(Room.class);
                    if (changedRoom != null) {
                        Log.w(TAG, "Get Changed : [" + changedRoom.getIndex() + "번방]");
                        int max = -1;
                        List<Bed> beds = changedRoom.getBeds();
                        for(Bed b : beds){
                            if(b.getAlertType() > max) {
                                Log.w(TAG, "Check AlertType : [" + b.getAlertType() + "]");
                                max = b.getAlertType();
                            }
                        }

                        if(max == Bed.TYPE_NEED){
                            Log.w(TAG, "Color Changed : [" + changedRoom.getIndex() + "번방] NEED");
                            holder.roomNoTXT.setBackgroundColor(Color.parseColor("#FFFEB300"));
                        }else if(max == Bed.TYPE_MUST){
                            Log.w(TAG, "Color Changed : [" + changedRoom.getIndex() + "번방] MUST");
                            holder.roomNoTXT.setBackgroundColor(Color.parseColor("#FFE1574C"));
                        }else{
                            Log.w(TAG, "Color Changed : [" + changedRoom.getIndex() + "번방] DEFAULT");
                            holder.roomNoTXT.setBackgroundColor(Color.parseColor("#FEF1EB"));
                        }
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            };

            dataBaseRooms.addValueEventListener(valueEventListener);
            references.put(dataBaseRooms, valueEventListener);
        }
        HomeBedAdapter adapter = new HomeBedAdapter(room.getBeds(), mContext, parent, holder.bedRCC);
        holder.roomNoTXT.setText("" + ((100 * departFloor) + (position + 1)));
        holder.bedRCC.setAdapter(adapter);
        holder.addBedBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "On Click addBedBTN Size --> " + room.getBeds().size());
                room.getBeds().add(new Bed(room.getBeds().size() + 1, room.getIndex() - 1));
                adapter.setBeds(room.getBeds());
            }
        });
    }

    public void scrollAlertElement(int roomIndex, int bedIndex) {
        MyViewHolder viewHolder = (MyViewHolder) parentView.findViewHolderForAdapterPosition(roomIndex);
        if (viewHolder == null) {
            parentView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    MyViewHolder viewHolder = (MyViewHolder) parentView.findViewHolderForAdapterPosition(roomIndex);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        RecyclerView childRecyclerView = viewHolder.bedRCC;
                        childRecyclerView.smoothScrollToPosition(bedIndex);

                        parentView.removeOnScrollListener(this);
                    }
                }
            });
            parentView.smoothScrollToPosition(roomIndex);
        }else{
            RecyclerView childRecyclerView = viewHolder.bedRCC;
            childRecyclerView.smoothScrollToPosition(bedIndex);
        }
    }


    private void startShakeAnimation(View view){
        Animation shakeAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.shake_animation);
        view.startAnimation(shakeAnimation);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if(isAnim){
            startShakeAnimation(holder.RoombodyLayout);
        }else{
            if(holder.RoombodyLayout.getAnimation() != null){
                holder.RoombodyLayout.clearAnimation();
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(holder.RoombodyLayout.getAnimation() != null){
            holder.RoombodyLayout.clearAnimation();
        }
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

    public void onAnim(){
        isAnim = true;
        notifyDataSetChanged();
    }

    public void offAnim(){
        isAnim = false;
        notifyDataSetChanged();
    }

    public boolean isAnim(){
        return isAnim;
    }

    public List<Room> getRooms(){
        return items;
    }

    public void removeRoom(Room room){
        if(items.contains(room)){
            int index = items.indexOf(room);
            items.remove(room);
            notifyItemRemoved(index);
        }
    }

    public void removeRoom(int position){
        items.remove(position);
        for(int i=position; i<items.size(); i++){
            Room room = items.get(i);
            room.setIndex(room.getIndex() - 1);
            List<Bed> beds = room.getBeds();
            List<Bed> changedBeds = new ArrayList<>();
            for(Bed b : beds){
                b.setDepartRoom(i);
                changedBeds.add(b);
            }
            room.setBeds(changedBeds);
            items.set(i, room);
        }
        HomeFragment.info.setRoomCount(items.size());
        notifyDataSetChanged();
    }

    public void resetRoom(){
        items.clear();
        notifyDataSetChanged();
    }

    public Room getItem(int position){
        return items.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout RoombodyLayout;
        public TextView roomNoTXT;
        public RecyclerView bedRCC;
        public ImageButton addBedBTN;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNoTXT = itemView.findViewById(R.id.roomNoTXT);
            addBedBTN = itemView.findViewById(R.id.AddBedBTN);
            RoombodyLayout = itemView.findViewById(R.id.RoombodyLayout);
            bedRCC = itemView.findViewById(R.id.bedRCC);
            bedRCC.setHasFixedSize(true);
            bedRCC.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

}
