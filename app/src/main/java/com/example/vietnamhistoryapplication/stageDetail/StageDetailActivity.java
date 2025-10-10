package com.example.vietnamhistoryapplication.stageDetail;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StageDetailActivity extends AppCompatActivity {
    public StageDetailItem stageDetailItem;
    private List<EventItem> events = new ArrayList<>();
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private StageOverviewAdapter stageOverviewAdapter;

    private MutableLiveData<StageDetailItem> stageDetailLiveData = new MutableLiveData<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage_detail_activity);
         String periodSlug = getIntent().getStringExtra("periodSlug");
         String stageSlug = getIntent().getStringExtra("stageSlug");




         stageDetailLiveData.observe(this,items->{
             TextView tvDetail = findViewById(R.id.tvDetail);
             TextView tvStageRange = findViewById(R.id.tvstageRange);
             ImageView ivImage = findViewById(R.id.ivImage);
             TextView idTitle = findViewById(R.id.idTitle);

             StringBuilder sb = new StringBuilder();
             for (String item : items.details) {
                 sb.append("• ").append(item).append("\n"); // thêm bullet point
             }
             tvDetail.setText(sb.toString());
             ImageLoader.loadImage(ivImage,items.image);
             tvStageRange.setText(items.stageRange);

             ViewPager2 viewPagerOverview = findViewById(R.id.viewPagerOverview);
             stageOverviewAdapter = new StageOverviewAdapter(items);
             viewPagerOverview.setAdapter(stageOverviewAdapter);

             recyclerView = findViewById(R.id.recyclerViewEvents);
             recyclerView.setLayoutManager(new LinearLayoutManager(this));
             eventAdapter = new EventAdapter(events);
             idTitle.setText(items.title);
             recyclerView.setAdapter(eventAdapter);
         });

         loadStageDetailFromFirestore(periodSlug,stageSlug);

        FloatingActionButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->{
            finish();
        });

    }
    private void loadStageDetailFromFirestore(String periodSlug,String stageSlug){

         FirebaseFirestore db = FirebaseFirestore.getInstance();
         db.collection("periods")
                 .document(periodSlug)
                 .collection("stages")
                 .document(stageSlug)
                 .get()
                 .addOnSuccessListener( documentSnapshot->{
                        if(documentSnapshot.exists()){
                            String title = documentSnapshot.getString("title");
                            Timestamp startDate = documentSnapshot.getTimestamp("startDate");
                            Timestamp endDate = documentSnapshot.getTimestamp("endDate");
                            String stageRange = formatDateRange(startDate,endDate);
                            String overview = documentSnapshot.getString("overview");
                            String image = documentSnapshot.getString("coverMediaRef");
                            List<String> details = (List<String>)documentSnapshot.get("details");
                            List<String> result = (List<String>)documentSnapshot.get("result");
                            String impactOnPresent = documentSnapshot.getString("impactOnPresent");
                            stageDetailItem = new StageDetailItem(stageSlug,title,stageRange,overview,image,details,result,impactOnPresent);
                            stageDetailLiveData.setValue(stageDetailItem);
                            Log.d("StageDetailActivity","Loaded stage detail: "+stageDetailItem.title);
                        }else{
                            Log.d("StageDetailActivity","Stage detail not found");
                        }
         });
         db.collection("periods")
                 .document(periodSlug)
                 .collection("stages")
                 .document(stageSlug)
                 .collection("events")
                 .orderBy("sortOrder")
                 .get()
                 .addOnSuccessListener(querySnapshot -> {
                     if(querySnapshot.isEmpty()){
                         Log.d("StageDetailActivity","No events found for stage: "+stageSlug);
                     }else{
                         for(QueryDocumentSnapshot doc : querySnapshot){
                             String slug = doc.getId();
                             String title = doc.getString("title");
                             Timestamp startDate = doc.getTimestamp("startDate");
                             Timestamp endDate = doc.getTimestamp("endDate");
                             String dateRange = formatDateRange(startDate,endDate);
                             String smallTitle = doc.getString("smallTitle");
                             List<Map<String, Object>> images = (List<Map<String, Object>>) doc.get("images");
                             String image = (images != null && !images.isEmpty())
                                     ? (String) images.get(0).get("link")
                                     : null;
                             String type = doc.getString("type");
                             Long sortOrderLong = doc.getLong("sortOrder");
                             int sortOrder = (sortOrderLong != null) ? sortOrderLong.intValue() : 0;
                             events.add(new EventItem(slug,title,dateRange,smallTitle,image,type,sortOrder));
                             Log.d("StageDetailActivity","Loaded event: "+title);
                             eventAdapter.notifyDataSetChanged();
                         }
                     }

         });


    }
    private String formatDateRange(Timestamp start, Timestamp end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
        String startYear = start != null ? sdf.format(start.toDate()) : "N/A";
        String endYear = end != null ? sdf.format(end.toDate()) : "N/A";
        return startYear + "–" + endYear;
    }
}