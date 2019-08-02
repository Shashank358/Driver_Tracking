package cf.poorcoder.driverapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cf.poorcoder.driverapplication.Models.Notification;
import cf.poorcoder.driverapplication.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{

    Context mCtx;
    List<Notification> notificationList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    HashMap<String,String> hashMap;
    int ProductPrice;
    public NotificationAdapter(Context mCtx, List<Notification> notificationList)
    {
        this.mCtx = mCtx;
        this.notificationList = notificationList;
    }
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.single_notification,
                parent, false);
        NotificationViewHolder NotificationViewHolder = new NotificationViewHolder(view);
        return NotificationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationViewHolder holder, final int position) {
        final Notification notification = notificationList.get(position);

        holder.category.setText(notification.getCategory());
        holder.detail.setText(notification.getContent());

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder
    {

        TextView category,detail;
        public NotificationViewHolder(View itemView) {
            super(itemView);

            category = (TextView) itemView.findViewById(R.id.category);
            detail = (TextView) itemView.findViewById(R.id.details);
            //ratingBar = (AppCompatRatingBar) itemView.findViewById(R.id.ratingBarMain);
        }
    }
}
