package br.com.pererao.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.pererao.R;
import br.com.pererao.model.ItemPreOrcamento;
import br.com.pererao.model.PreOrcamento;
import br.com.pererao.model.Qualifications;

public class ItemPreOrcamentoAdapter extends RecyclerView.Adapter<ItemPreOrcamentoAdapter.ViewHolder> {

    private Context mContext;
    private List<ItemPreOrcamento> mItemPreOrcamento;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mFirebaseUser;
    double dtt = 0;

    public ItemPreOrcamentoAdapter(Context mContext, List<ItemPreOrcamento> mItemPreOrcamento) {
        this.mContext = mContext;
        this.mItemPreOrcamento = mItemPreOrcamento;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_preorcamento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ItemPreOrcamento itemPreOrcamento = mItemPreOrcamento.get(position);

        holder.tv_desc_item.setText(itemPreOrcamento.getItem());
        String qtde = String.valueOf(itemPreOrcamento.getQtdeItem());
        holder.tv_item_qtde.setText(qtde);
        String val = String.valueOf(itemPreOrcamento.getValorItem());
        holder.tv_item_value.setText(val);
        dtt += itemPreOrcamento.getQtdeItem() * itemPreOrcamento.getValorItem();
        String tt = String.valueOf(dtt);
        holder.tv_tt_itens.setText(tt);

        if (position == mItemPreOrcamento.size() - 1) {
            holder.ll_total.setVisibility(View.VISIBLE);
        } else {
            holder.ll_total.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!(position == mItemPreOrcamento.size())) {
                    mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference("Chat").child("PreOrcamento").child("Itens");
                    AlertDialog.Builder alerDelete = new AlertDialog.Builder(mContext);
                    alerDelete.setTitle("Deletar Mensagem");
                    alerDelete.setMessage("Deseja deletar esta mensagem?");
                    alerDelete.setPositiveButton("Sim, deletar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dtt -= itemPreOrcamento.getQtdeItem() * itemPreOrcamento.getValorItem();
                                    mDatabaseReference.child(itemPreOrcamento.getKeyPreOrc()).removeValue();
                                    notifyDataSetChanged();
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
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemPreOrcamento.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_desc_item;
        public TextView tv_item_qtde;
        public TextView tv_item_value;
        public TextView tv_tt_itens;
        public LinearLayout ll_total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_desc_item = itemView.findViewById(R.id.tv_desc_item_preo);
            tv_item_qtde = itemView.findViewById(R.id.tv_quant_item_preo);
            tv_item_value = itemView.findViewById(R.id.tv_valor_item_preo);
            tv_tt_itens = itemView.findViewById(R.id.tv_total_val_itens);
            ll_total = itemView.findViewById(R.id.llTotal);
        }
    }

}