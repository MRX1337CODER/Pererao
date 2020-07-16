package br.com.pererao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.pererao.R;
import br.com.pererao.model.Qualifications;

public class QualificationsAdapter extends RecyclerView.Adapter<QualificationsAdapter.ViewHolder> {

    private Context mContext;
    private List<Qualifications> mQualifications;
    public List<Qualifications> checkedQualifications = new ArrayList<>();

    public QualificationsAdapter(Context mContext, List<Qualifications> mQualifications) {
        this.mContext = mContext;
        this.mQualifications = mQualifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_qualification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Qualifications qualifications = mQualifications.get(position);
        holder.cb_qualifi.setText(qualifications.getDesc());
        holder.cb_qualifi.setChecked(qualifications.isSelected());

        holder.setItemClickListener(new ViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                CheckBox myCheckBox = (CheckBox) v;
                Qualifications currentQualifications = mQualifications.get(pos);
                if (myCheckBox.isChecked()) {
                    currentQualifications.setSelected(true);
                    checkedQualifications.add(currentQualifications);
                } else {
                    currentQualifications.setSelected(false);
                    checkedQualifications.remove(currentQualifications);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mQualifications.size();
    }

    public void setFilter(List<Qualifications> FilteredQualifications) {
        mQualifications = FilteredQualifications;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CheckBox cb_qualifi;
        ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_qualifi = itemView.findViewById(R.id.cb_desc);

            cb_qualifi.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }

        interface ItemClickListener {
            void onItemClick(View v, int pos);
        }
    }

}