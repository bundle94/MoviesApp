package com.example.movies.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movies.R;
import com.example.movies.model.Notification;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private Context context;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context , List<Notification> notifications){
        this.context = context;
        notificationList = notifications;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification , parent , false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {

        Notification notification = notificationList.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationHolder extends RecyclerView.ViewHolder{

        TextView title , message;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_tv);
            message = itemView.findViewById(R.id.message_tv);
        }
    }
}
