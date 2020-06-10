package br.com.pererao.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.pererao.R;
import br.com.pererao.activity.MessageActivity;
import br.com.pererao.model.Chat;
import br.com.pererao.model.User;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 1;//0
    public static final int MSG_TYPE_RIGHT = 0;//1

    private Context mContext;
    private List<Chat> mChat;
    private String imageUrl;
    private long dateMessage;

    FirebaseUser mFirebaseUser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageUrl, long dateMessage) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageUrl = imageUrl;
        this.dateMessage = new Date().getTime();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("VIEWTYPE", "VIEW: " + viewType);
        if (viewType == MSG_TYPE_RIGHT) {
            //View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right, parent, false);
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            //View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left, parent, false);
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        holder.date_message.setText(DateFormat.format("dd/MM/yyyy (HH:mm:ss)", chat.getMessageTime()));

        if (imageUrl.equals("default")) {
            Glide.with(mContext)
                    .load("https://firebasestorage.googleapis.com/v0/b/pererao2k20.appspot.com/o/user_photo%2Fman_user.png?alt=media")
                    .transform(new CircleCrop())
                    .into(holder.profile_image);
        } else {
            Glide.with(mContext)
                    .load(imageUrl)
                    .transform(new CircleCrop())
                    .into(holder.profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public TextView date_message;
        //public TextView txt_seen;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            date_message = itemView.findViewById(R.id.date_message_chat);
            profile_image = itemView.findViewById(R.id.profile_image_chat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(mFirebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

}