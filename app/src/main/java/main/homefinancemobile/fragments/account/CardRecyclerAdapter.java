package main.homefinancemobile.fragments.account;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import main.homefinancemobile.R;
import main.homefinancemobile.model.AccountData;
import main.homefinancemobile.utils.ParseDate;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder> {

    private static final Object TAG = R.string.account_card;

    private ArrayList<AccountData> mCards = new ArrayList<>();
    private OnCardListener mOnCardListener;

    public CardRecyclerAdapter(ArrayList<AccountData> cards, OnCardListener onCardListener) {
        this.mCards = cards;
        this.mOnCardListener = onCardListener;
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView accountName, accountBalance, updateDate;
        OnCardListener mOnCardListener;

        public ViewHolder(View itemView, OnCardListener onCardListener) {
            super(itemView);
            accountName = itemView.findViewById(R.id.accountName);
            accountBalance = itemView.findViewById(R.id.accountBalance);
            updateDate = itemView.findViewById(R.id.accountLastUpdate);
            mOnCardListener = onCardListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnCardListener.onCardClick(getAdapterPosition());
        }
    }

    public interface OnCardListener {
        void onCardClick(int position);
    }

    @NonNull
    @Override
    public CardRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_card, parent, false);
        return new ViewHolder(view, mOnCardListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardRecyclerAdapter.ViewHolder holder, int position) {
        try{
            AccountData cardData = mCards.get(position);

            holder.accountName.setText(cardData.getName());
            holder.accountBalance.setText(cardData.getBalance().toString());
            holder.updateDate.setText(ParseDate.parseDateToString(cardData.getUpdateDate()));
        }catch (NullPointerException e){
            Log.e((String) TAG, "onBindViewHolder: Null Pointer: " + e.getMessage() );
        }
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }
}
