package com.hybrid.customlogin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class RecyclerAdapter_Comment extends RecyclerView.Adapter<RecyclerAdapter_Comment.RecyclerViewHolder_Comment> {

    ArrayList<Comment_Object> comments;
    Context ctx;

    public RecyclerAdapter_Comment(ArrayList<Comment_Object> comments, Context ctx) {
        Log.e("BINDER_COMMENT", "Onviewholder");
        this.comments = comments;
        this.ctx = ctx;
    }

    @Override
    public RecyclerViewHolder_Comment onCreateViewHolder(ViewGroup parent, int viewType) {
        //row_layout_comment

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.focus_comment_list_item, parent, false);
        RecyclerViewHolder_Comment recyclerViewHolder_comment = new RecyclerViewHolder_Comment(view);
        return recyclerViewHolder_comment;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder_Comment holder, int position) {

        holder.text2.setText(comments.get(position).getComment());
        holder.text1.setText(comments.get(position).getUser_name());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    public static class RecyclerViewHolder_Comment extends RecyclerView.ViewHolder {


        TextView text2, text1;

        public RecyclerViewHolder_Comment(View v) {
            super(v);
            Log.e("BINDER_COMMENT", "Onviewholder");
            text2 = (TextView) v.findViewById(R.id.focus_comment_text2);
            text1 = (TextView) v.findViewById(R.id.focus_comment_text1);
        }
    }
}

//change in ids
