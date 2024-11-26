package com.voste.snap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.voste.snap.R;
import com.voste.snap.model.Message;

import java.util.List;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private final List<Message> messages;

    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.messageTv.setText(message.getText());
        holder.dateTv.setText(message.getDate());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getOwnerId().equals(
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
            return R.layout.message_me;
        } else {
            return R.layout.message_you;
        }
    }

    // Метод для добавления нового сообщения
    public void addMessage(Message message) {
        messages.add(message); // Добавляем сообщение в конец списка
        notifyItemInserted(messages.size() - 1); // Уведомляем RecyclerView об изменении
    }

    // ViewHolder для отображения сообщений
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTv, dateTv;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTv = itemView.findViewById(R.id.message_tv);
            dateTv = itemView.findViewById(R.id.message_date_tv);
        }
    }
}