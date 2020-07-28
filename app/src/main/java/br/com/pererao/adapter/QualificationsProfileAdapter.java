package br.com.pererao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import br.com.pererao.R;
import br.com.pererao.model.Qualifications;

public class QualificationsProfileAdapter extends RecyclerView.Adapter<QualificationsProfileAdapter.ViewHolder> {

    private Context mContext;
    private List<Qualifications> mQualifications;

    public QualificationsProfileAdapter(Context mContext, List<Qualifications> mQualifications) {
        this.mContext = mContext;
        this.mQualifications = mQualifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_qualification_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Qualifications qualifications = mQualifications.get(position);

        holder.tv_qualifi_profile.setText(qualifications.getDesc());

    }

    @Override
    public int getItemCount() {
        return mQualifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_qualifi_profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_qualifi_profile = itemView.findViewById(R.id.tv_desc);

        }
    }

}