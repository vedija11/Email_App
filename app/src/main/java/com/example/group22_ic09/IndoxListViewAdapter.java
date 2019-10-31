package com.example.group22_ic09;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IndoxListViewAdapter extends RecyclerView.Adapter<IndoxListViewAdapter.ViewHolder> {
    List<InboxData> mList;
    private final OnItemClickListener listener;

    public IndoxListViewAdapter(List<InboxData> mList, OnItemClickListener listener) {
        this.mList = mList;
        this.listener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(InboxData item);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_listview, parent, false);
        ViewHolder viewHolder = new ViewHolder(convertView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mList.get(position), listener);
        InboxData mail = mList.get(position);

        holder.tv_date.setText(mail.created_at);
        holder.tv_subject.setText(mail.subject);
        holder.tv_subject.setFocusable(false);
        holder.tv_date.setFocusable(false);
        holder.btn_delete.setFocusable(false);
        holder.btn_delete.setTag(mail);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_subject;
        TextView tv_date;
        ImageButton btn_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_subject = itemView.findViewById(R.id.tv_subject);
            tv_date = itemView.findViewById(R.id.tv_date);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(final InboxData inboxData, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(inboxData);
                }
            });
        }
    }
}
