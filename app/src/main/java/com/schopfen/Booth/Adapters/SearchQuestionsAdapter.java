package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schopfen.Booth.Activities.QuestionsDetailsActivity;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.DataClasses.Question_Search_Images_Data;
import com.schopfen.Booth.DataClasses.SearchProductsArrayData;
import com.schopfen.Booth.DataClasses.SearchQuestionsArrayData;
import com.schopfen.Booth.Models.SearchQuestionsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchQuestionsAdapter extends RecyclerView.Adapter<SearchQuestionsAdapter.MyViewHolder> {

    Context context;
    ArrayList<SearchQuestionsData> arrayList;
    ArrayList<Question_Search_Images_Data> question_search_images_data;
    private ItemClick itemClick;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public SearchQuestionsAdapter(Context context, ArrayList<SearchQuestionsData> arrayList,ItemClick itemClick) {
        this.context = context;
        this.arrayList = arrayList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_questions_adapter_items, parent, false);

        final SearchQuestionsAdapter.MyViewHolder viewHolder = new SearchQuestionsAdapter.MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onItemClick(view, viewHolder.getPosition());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        question_search_images_data = new ArrayList<>();

        SpannableString span1 = new SpannableString(context.getResources().getString(R.string.comments));
        span1.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.orange)), 0, span1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        CharSequence finalText = TextUtils.concat(span1);

        Picasso.get().load(Constants.URL.IMG_URL + arrayList.get(position).getUserImage()).into(holder.profile_image_view);

        holder.username.setText("@"+arrayList.get(position).getUserName());
        holder.place.setText(arrayList.get(position).getUserCityName());
        holder.time.setText(BaseClass.TimeStampToDate(arrayList.get(position).getQuestionAskedAt()));
        holder.detail.setText(arrayList.get(position).getQuestionDescription());
        holder.answer_count.setText("(" + arrayList.get(position).getCommentCount() + ") "+finalText);
        holder.categories.setText(arrayList.get(position).getCategoryName() + " / " +arrayList.get(position).getSubCategoryName());

        for (int i=0; i<arrayList.get(position).getQuestionImages().size(); i++){
            question_search_images_data.add(new Question_Search_Images_Data(arrayList.get(position).getQuestionImages().get(i).getProductCompressedImage()));
        }

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        Question_Search_Recycler_Adapter adapter = new Question_Search_Recycler_Adapter(context,question_search_images_data);
        holder.recyclerView.setAdapter(adapter);

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QuestionsDetailsActivity.class);
                mEditor.putString("QuestionID", arrayList.get(position).getQuestionID()).apply();
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username, place, time, detail, answer_count,categories;
        RecyclerView recyclerView;
        CircleImageView profile_image_view;
        LinearLayout main_layout;

        MyViewHolder(View itemView) {
            super(itemView);

            categories = itemView.findViewById(R.id.categories);
            profile_image_view = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.user_name);
            place = itemView.findViewById(R.id.place);
            time = itemView.findViewById(R.id.time_text);
            detail = itemView.findViewById(R.id.question_detail);
            answer_count = itemView.findViewById(R.id.answers_count);
            recyclerView = itemView.findViewById(R.id.questionsearchrecycler);
            main_layout = itemView.findViewById(R.id.main_layout);
        }
    }
    public interface ItemClick {
        void onItemClick(View v, int position);
    }
}