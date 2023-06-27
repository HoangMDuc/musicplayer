package com.example.musicplayer.adapter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Comment.Comment;
import com.example.musicplayer.model.Comment.CommentImp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private ArrayList<Comment> commentList;
    private CommentImp commentImp;
    String id_music;
//    private static OnItemClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private static int currentPosition;

//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//    }

//    public interface OnItemClickListener {
//        void onCommentItemClick(int position);
//
//    }
//    public void setOnItemClickListener(PlaylistSongAdapter.OnItemClickListener listener) {
//        this.listener = (OnItemClickListener) listener;
//    }
    public Comment getItem(int position) {
        if (position >= 0 && position < commentList.size()) {
            return commentList.get(position);
        }
        return null;
    }


    public CommentAdapter(ArrayList<Comment> commentList, CommentImp commentImp,String id_music) {
        this.commentList = commentList;
        this.commentImp =commentImp;
        this.id_music = id_music;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View lItem;
        lItem = layoutInflater.inflate(R.layout.comment_item, parent, false);
        CommentViewHolder viewHolder = new CommentViewHolder(lItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        if (comment == null) {
            return;
        }
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentImp.delete(comment.getId_comment());
                updateData(commentImp.getComment(id_music));
            }
        });

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.popup_form_cmt);

                Window window = dialog.getWindow();

                if(window == null){
                    return;
                }
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams windowActributes = window.getAttributes();
                windowActributes.gravity = Gravity.CENTER;
                window.setAttributes(windowActributes);

                if(Gravity.BOTTOM == Gravity.CENTER){
                    dialog.setCancelable(true);
                }else {
                    dialog.setCancelable(false);
                }

                ImageButton btnExit = dialog.findViewById(R.id.imageExit1);

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

                EditText cmtUP= (EditText) dialog.findViewById(R.id.editCmt);
                cmtUP.setText(comment.getContent());

                Button up_btn =(Button) dialog.findViewById(R.id.buttonSubmitUp);

                up_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentImp.update(comment.getId_comment(), cmtUP.getText().toString());
                        updateData(commentImp.getComment(id_music));
                    }
                });

            }
        });

        holder.textViewUserName.setText(comment.getUser_name());
        holder.textViewContent.setText(comment.getContent());

        // Đặt ảnh từ URL sử dụng thư viện Picasso hoặc Glide
        Picasso.get().load(comment.getImage()).into(holder.imageViewAvatar);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAvatar;
        TextView textViewUserName;
        TextView textViewContent;

        ImageButton btn_delete;
        ImageButton btn_edit;
        ConstraintLayout constraintLayout;

        public ImageView getImageViewAvatar() {
            return imageViewAvatar;
        }

        public void setImageViewAvatar(ImageView imageViewAvatar) {
            this.imageViewAvatar = imageViewAvatar;
        }

        public TextView getTextViewUserName() {
            return textViewUserName;
        }

        public void setTextViewUserName(TextView textViewUserName) {
            this.textViewUserName = textViewUserName;
        }

        public TextView getTextViewContent() {
            return textViewContent;
        }

        public void setTextViewContent(TextView textViewContent) {
            this.textViewContent = textViewContent;
        }

        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }

        public void setConstraintLayout(ConstraintLayout constraintLayout) {
            this.constraintLayout = constraintLayout;
        }

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAvatar = (ImageView) itemView.findViewById(R.id.image);
            textViewUserName = (TextView) itemView.findViewById(R.id.user_name);
            textViewContent =(TextView) itemView.findViewById(R.id.content);
            btn_delete = (ImageButton) itemView.findViewById(R.id.imageDelete);
            btn_edit =(ImageButton) itemView.findViewById(R.id.imageUpdate);
        }


        public int getCurrentComment() {
            return currentPosition;
        }
    }
    public void updateData(ArrayList<Comment> newData) {
        commentList.clear();
        commentList.addAll(newData);
        notifyDataSetChanged();
    }

}




