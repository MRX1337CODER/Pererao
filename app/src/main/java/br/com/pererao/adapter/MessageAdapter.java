package br.com.pererao.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

import br.com.pererao.R;
import br.com.pererao.model.Chat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private long dateMessage;
    //Animation Fade
    long DURATION = 100;
    private boolean on_attach = true;
    //Firebase
    FirebaseUser mFirebaseUser;
    DatabaseReference mDatabaseReference;

    public MessageAdapter(Context mContext, List<Chat> mChat, long dateMessage) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.dateMessage = new Date().getTime();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("VIEWTYPE", "VIEW: " + viewType);
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        final Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        holder.date_message.setText(DateFormat.format("dd/MM/yyyy (HH:mm:ss)", chat.getMessageTime()));
        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.img_delivered.setVisibility(View.GONE);
                holder.img_seen.setVisibility(View.VISIBLE);
            } else {
                holder.img_delivered.setVisibility(View.VISIBLE);
                holder.img_seen.setVisibility(View.GONE);
            }
        } else {
            holder.img_delivered.setVisibility(View.GONE);
            holder.img_seen.setVisibility(View.GONE);
        }

        if (chat.getImgKey().equals("default")) {
            holder.img_sended.setVisibility(View.GONE);
        } else {
            holder.img_sended.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(chat.getImgUrl())
                    .placeholder(R.drawable.infinity)
                    .into(holder.img_sended);
        }

        holder.img_sended.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alerViewImage = new AlertDialog.Builder(mContext);
                alerViewImage.setTitle(chat.getMessage());
                WebView wv = new WebView(mContext);
                wv.getSettings().setJavaScriptEnabled(true);
                wv.getSettings().setSupportZoom(true);
                wv.getSettings().setBuiltInZoomControls(true);
                wv.getSettings().setDisplayZoomControls(false);
                wv.getSettings().setLoadWithOverviewMode(true);
                wv.getSettings().setUseWideViewPort(true);
                wv.loadUrl(chat.getImgUrl());
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }

                    @Override
                    public void onPageStarted(WebView _param1, String _param2, Bitmap _param3) {
                        final String _url = _param2;

                        super.onPageStarted(_param1, _param2, _param3);
                    }

                    @Override
                    public void onPageFinished(WebView _param1, String _param2) {
                        final String _url = _param2;

                        super.onPageFinished(_param1, _param2);
                    }
                });
                alerViewImage.setView(wv);
                alerViewImage.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = alerViewImage.create();
                alertDialog.show();
            }
        });

        if (viewType == MSG_TYPE_RIGHT) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference("Chat").child("Mensagens");
                    AlertDialog.Builder alerDelete = new AlertDialog.Builder(mContext);
                    alerDelete.setTitle("Deletar Mensagem");
                    alerDelete.setMessage("Deseja deletar esta mensagem?");
                    alerDelete.setPositiveButton("Sim, deletar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    //  if (snapshot.exists()){
                                    //    final Chat chatDel = snapshot.getValue(Chat.class);
                                    //assert chatDel != null;
                                    mDatabaseReference.child(chat.getKeyMessage()).removeValue();
                                    notifyDataSetChanged();
                                    //}                 esse for apaga todu o chat, é bom pra dps e.e
                                    //}
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            notifyDataSetChanged();
                            //Toast.makeText(mContext, "Teste Long Delete ;) >>> desse lado kk", Toast.LENGTH_LONG).show();
                        }
                    });
                    alerDelete.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alerDelete.create();
                    alertDialog.show();
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public TextView date_message;
        private ImageView img_delivered;
        private ImageView img_seen;
        private ImageView img_sended;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            date_message = itemView.findViewById(R.id.date_message_chat);
            img_delivered = itemView.findViewById(R.id.img_delivered);
            img_seen = itemView.findViewById(R.id.img_seen);
            img_sended = itemView.findViewById(R.id.img_sended);
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