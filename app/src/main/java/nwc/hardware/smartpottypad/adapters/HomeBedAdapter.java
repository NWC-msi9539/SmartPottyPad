package nwc.hardware.smartpottypad.adapters;

import static android.content.ContentValues.TAG;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nwc.hardware.smartpottypad.R;
import nwc.hardware.smartpottypad.activities.HomeActivity;
import nwc.hardware.smartpottypad.datas.Bed;
import nwc.hardware.smartpottypad.tasks.SetPreferences;

public class HomeBedAdapter extends RecyclerView.Adapter<HomeBedAdapter.MyViewHolder>{
    private List<Bed> items;
    private Context mContext;
    private FrameLayout HomeDetailFrame;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd, HH:mm");

    private HomeActivity parent;
    private RecyclerView parentView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Map<DatabaseReference, ValueEventListener> references = new HashMap<>();

    private ValueEventListener attachListener = new ValueEventListener() {
        @Override
        public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
            if(snapshot != null){
                boolean attach = snapshot.getValue(Boolean.class);

                Log.d(TAG, "Trailing Attach : " + attach);
            }
        }

        @Override
        public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

        }
    };

    private ValueEventListener bedInfoListener = new ValueEventListener() {
        @Override
        public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

        }
    };

    public HomeBedAdapter(List<Bed> items, Context mContext, HomeActivity parent, RecyclerView parentView) {
        this.items = items;
        this.mContext = mContext;
        this.parent = parent;
        this.parentView = parentView;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "FINDING --> " + parent.getRootView());
        HomeDetailFrame = parent.getRootView().findViewById(R.id.HomeDetailFrame);

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.beditem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(linearLayout);
        return myViewHolder;
    }

    @Override
    public void onViewRecycled(@androidx.annotation.NonNull MyViewHolder holder) {
        int position = holder.getAdapterPosition();
        if ((position != RecyclerView.NO_POSITION && parentView.findViewHolderForAdapterPosition(position) != null) || position == -1) {
            // 아이템이 여전히 화면에 표시되고 있으므로 참조를 제거하지 않습니다.
            return;
        }

        Log.w(TAG,"DETACHED : " + holder.BedIndexTXT.getText());
        Bed bed = items.get(position);

        synchronized (references) {
            DatabaseReference bedInfoRef = database.getReference("users/" + SetPreferences.databaseKey + "/rooms/" + bed.getDepartRoom() + "/beds/" + (bed.getIndex() - 1));
            if(references.containsKey(bedInfoRef)){
                bedInfoRef.removeEventListener(references.get(bedInfoRef));
                references.remove(bedInfoRef);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Bed bed = items.get(position);
        DatabaseReference bedInfoRef = database.getReference("users/" + SetPreferences.databaseKey + "/rooms/" + bed.getDepartRoom() + "/beds/" + (bed.getIndex() - 1));

        synchronized (references) {
            if (references.containsKey(bedInfoRef)) {
                bedInfoRef.removeEventListener(references.get(bedInfoRef));
                references.remove(bedInfoRef);
            }

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    Bed changedBed = snapshot.getValue(Bed.class);
                    if (changedBed != null) {
                        Bed oldBed = items.get((bed.getIndex() - 1));

                        // 만약 변경 전 후의 Bed가 다르다면, 교체하고 UI를 업데이트합니다.
                        if (!oldBed.equals(changedBed)) {
                            Log.d(TAG, "Changed : [" + changedBed.getDepartRoom() + "번방 " + changedBed.getName() + "]");
                            changeBed((bed.getIndex() - 1), changedBed);
                            notifyItemChanged(bed.getIndex() - 1);
                        }
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            };

            bedInfoRef.addValueEventListener(valueEventListener);
            references.put(bedInfoRef, valueEventListener);
        }

        holder.BedIndexTXT.setText(bed.getName());
        holder.BedFirstTXT.setText(dateFormat.format(new Date(bed.getFirstUseTime())));
        if(bed.getLastChangeTime() == -1){
            holder.BedChangeTXT.setText("교체 없음");
        }else{
            holder.BedChangeTXT.setText(dateFormat.format(new Date(bed.getLastChangeTime())));
        }
        if(bed.getEnCountTime() == -1){
            holder.BedChangeCountTXT.setText("발생 안함");
        }else{
            holder.BedChangeCountTXT.setText(dateFormat.format(new Date(bed.getEnCountTime())));
        }

        holder.BedstatusBTN.setImageDrawable(mContext.getDrawable(bed.getTypeToDrawbleID(bed.getAlertType())));
        if(bed.getAlertType() == Bed.TYPE_NEED){
            int alertColor = Color.parseColor("#FFFEB300");
            holder.bed_container.setCardBackgroundColor(alertColor);
        }else if(bed.getAlertType() == Bed.TYPE_MUST){
            int alertColor = Color.parseColor("#E1574C");
            holder.bed_container.setCardBackgroundColor(alertColor);
            ((View)parentView.getParent()).findViewById(R.id.roomNoTXT).setBackgroundColor(alertColor);
        }else{
            int alertColor = Color.parseColor("#FAFFFFFF");
            holder.bed_container.setCardBackgroundColor(alertColor);
        }

        if(bed.getAlertType() == Bed.TYPE_NEED || bed.getAlertType() == Bed.TYPE_MUST) {
            holder.BedstatusBTN.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Bed newBed = new Bed();
                    newBed.setDepartRoom(bed.getDepartRoom());
                    newBed.setEnCountTime(-1);
                    newBed.setAttach(bed.isAttach());
                    if(bed.isAttach()){
                        newBed.setAlertType(Bed.TYPE_NORMAL);
                    }else{
                        newBed.setAlertType(Bed.TYPE_DISCONNECTION);
                    }
                    newBed.setPooCount(bed.getPooCount());
                    newBed.setChangeCount(bed.getChangeCount() + 1);
                    newBed.setLastChangeTime(Calendar.getInstance().getTimeInMillis());
                    newBed.setFirstUseTime(bed.getFirstUseTime());
                    newBed.setIndex(bed.getIndex());
                    newBed.setDegree(bed.getDegree());
                    newBed.setName(bed.getName());

                    bedInfoRef.setValue(newBed);

                    return true;
                }
            });
        }

        holder.BedstatusBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bed.getAlertType() != Bed.TYPE_DISCONNECTION) {
                    if(holder.DetailInfo.getWidth() != 0){
                        animateViewToZero(holder.DetailInfo);
                    }else{
                        animateViewToWrapContent(holder.DetailInfo, holder.getAdapterPosition());
                    }
                }
                /*
                if(bed.getAlertType() == Bed.TYPE_DISCONNECTION) {
                    if (HomeDetailFrame.getVisibility() == View.GONE) {
                        HomeDetailFrame.setVisibility(View.VISIBLE);
                        HomeDetailFrame.animate()
                                .setDuration(1000)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .alpha(1f)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        parent.getProfile().setRoomindex(String.valueOf(bed.getDepartRoom()));
                                        parent.getProfile().setBedindex(String.valueOf(bed.getIndex() - 1));
                                        parent.changeFragment(HomeActivity.FRAGMENT_DETAIL_WIFI);
                                    }
                                })
                                .start();
                    } else {
                        HomeDetailFrame.animate()
                                .setDuration(1000)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .alpha(0f)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        HomeDetailFrame.setVisibility(View.GONE);
                                    }
                                })
                                .start();
                    }
                }else{

                }

                 */
            }
        });
    }

    private void animateViewToWrapContent(final View view, int position) {
        view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetWidth = view.getMeasuredWidth();
        final int targetHeight = view.getMeasuredHeight();

        ValueAnimator animWidth = ValueAnimator.ofInt(view.getWidth(), targetWidth);
        animWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = value;
                view.setLayoutParams(layoutParams);

                parentView.scrollToPosition(position);
            }
        });
        animWidth.setInterpolator(new DecelerateInterpolator()); // DecelerateInterpolator를 설정합니다.

        ValueAnimator animHeight = ValueAnimator.ofInt(view.getHeight(), targetHeight);
        animHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        animHeight.setInterpolator(new DecelerateInterpolator()); // DecelerateInterpolator를 설정합니다.

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500); // 애니메이션 지속 시간을 밀리초 단위로 설정합니다.
        animatorSet.playSequentially(animWidth, animHeight); // width 애니메이션 다음에 height 애니메이션을 실행합니다.
        animatorSet.start();
    }

    private void animateViewToZero(final View view) {
        final int startWidth = view.getWidth();
        final int startHeight = view.getHeight();

        ValueAnimator animWidth = ValueAnimator.ofInt(startWidth, 0);
        animWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = value;
                view.setLayoutParams(layoutParams);
            }
        });
        animWidth.setInterpolator(new DecelerateInterpolator()); // ReverseInterpolator를 설정합니다.

        ValueAnimator animHeight = ValueAnimator.ofInt(startHeight, 0);
        animHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        animHeight.setInterpolator(new DecelerateInterpolator()); // ReverseInterpolator를 설정합니다.

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500); // 애니메이션 지속 시간을 밀리초 단위로 설정합니다.
        animatorSet.playSequentially(animHeight, animWidth); // height 애니메이션 다음에 width 애니메이션을 실행합니다.
        animatorSet.start();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setBeds(List<Bed> beds){
        items = beds;
        notifyDataSetChanged();
    }

    public void addBed(Bed bed){
        if(!items.contains(bed)) {
            items.add(bed);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void removeBed(Bed bed){
        DatabaseReference bedInfoRef = database.getReference("users/" + SetPreferences.databaseKey + "/rooms/" + bed.getDepartRoom() + "/beds/" + (bed.getIndex() - 1));
        if(references.containsKey(bedInfoRef)){
            bedInfoRef.removeEventListener(references.get(bedInfoRef));
            references.remove(bedInfoRef);
        }
    }

    public void changeBed(int position, Bed bed){
        items.set(position, bed);
    }


    public void resetBed(){
        items.clear();
        notifyDataSetChanged();
    }

    public Bed getItem(int position){
        return items.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView BedIndexTXT;
        public TextView BedFirstTXT;
        public TextView BedChangeTXT;
        public TextView BedChangeCountTXT;
        public ImageButton BedstatusBTN;
        public CardView DetailInfo;
        public CardView bed_container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            BedIndexTXT = itemView.findViewById(R.id.BedIndexTXT);
            BedFirstTXT = itemView.findViewById(R.id.bedFirstTXT);
            BedChangeTXT = itemView.findViewById(R.id.bedChangeTXT);
            BedstatusBTN = itemView.findViewById(R.id.bedstatusBTN);
            DetailInfo = itemView.findViewById(R.id.bed_detailInfo);
            BedChangeCountTXT = itemView.findViewById(R.id.bedChangeCountTXT);
            bed_container = itemView.findViewById(R.id.bed_container);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@androidx.annotation.NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Set<DatabaseReference> keys = new HashSet<>(references.keySet());
        List<DatabaseReference> keysToRemove = new ArrayList<>();

        for(DatabaseReference key : keys){
            DatabaseReference bedInfoRef = key;
            bedInfoRef.removeEventListener(references.get(bedInfoRef));
            keysToRemove.add(bedInfoRef);
        }

        for(DatabaseReference keyToRemove : keysToRemove){
            Log.w(TAG, "REMOVED : " + keyToRemove);
            references.remove(keyToRemove);
        }
    }
}
