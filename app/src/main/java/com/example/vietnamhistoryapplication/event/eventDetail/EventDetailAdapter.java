package com.example.vietnamhistoryapplication.event.eventDetail;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vietnamhistoryapplication.R;
import com.example.vietnamhistoryapplication.common.ImageLoader;
import com.example.vietnamhistoryapplication.common.YouTubeUtils;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.EventDetailItem;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.HeaderItem;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.IntroItem;
//import com.example.vietnamhistoryapplication.event.eventDetail.ui.MapItem;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.SectionListItem;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.SectionListItem2;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.SlidesItem;
import com.example.vietnamhistoryapplication.event.eventDetail.ui.VideoItem;
//import com.google.android.gms.maps.MapView;
import com.google.android.material.tabs.TabLayout;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

public class EventDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<EventDetailItem> data;
    private final LifecycleOwner lifecycleOwner;

    public EventDetailAdapter(List<EventDetailItem> data, LifecycleOwner lifecycleOwner) {
        this.data = data;
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override public int getItemCount() { return data == null ? 0 : data.size(); }

    @Override public int getItemViewType(int position) { return data.get(position).getType(); }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == EventDetailItem.TYPE_HEADER) {
            return new VHHeader(inf.inflate(R.layout.event_detail_item_header, parent, false));
        } else if (viewType == EventDetailItem.TYPE_INTRO) {
            return new VHIntro(inf.inflate(R.layout.event_detail_item_intro, parent, false));
        } else if (viewType == EventDetailItem.TYPE_SECTION_LIST) {
            return new VHSection(inf.inflate(R.layout.event_detail_item_section_list, parent, false));
        }else if (viewType == EventDetailItem.TYPE_SECTION_LIST2){
            return new VHSection2(inf.inflate(R.layout.event_detail_item_section_list2, parent, false));
        }else if (viewType == EventDetailItem.TYPE_SLIDES) {
            return new VHSlides(inf.inflate(R.layout.event_detail_item_slides, parent, false));
        } else if (viewType == EventDetailItem.TYPE_VIDEO) {
            return new VHVideo(inf.inflate(R.layout.event_detail_item_video, parent, false));
        }
        throw new IllegalArgumentException("Unknown type: " + viewType);
    }
//     else if (viewType == EventDetailItem.TYPE_MAP) {
//        return new VHMap(inf.inflate(R.layout.event_detail_item_map, parent, false));
    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
        EventDetailItem it = data.get(pos);

        if (h instanceof VHHeader) {
            HeaderItem m = (HeaderItem) it;
            VHHeader v = (VHHeader) h;
            v.tvTitle.setText(m.getTitle());
            v.tvSubtitle.setText(m.getSubtitle());
            ImageLoader.loadImage(v.imgCover, m.getCoverUrl());
        } else if (h instanceof VHIntro) {
            IntroItem m = (IntroItem) it;
            VHIntro v = (VHIntro) h;
            v.tvContent.setText(m.getContent());
            ImageLoader.loadImage(v.imgDocument, m.getImgUrl());
        } else if (h instanceof VHSection) {
            SectionListItem m = (SectionListItem) it;
            VHSection v = (VHSection) h;
            v.tvTitle.setText(m.getTitle());
            v.tvBullets.setText(m.asBulletedString());
        } else if (h instanceof VHSection2){
            SectionListItem2 m = (SectionListItem2) it;
            VHSection2 v = (VHSection2) h;
            v.tvSectionTitle.setText(m.getTitle());
            v.tvUSLabel.setText("Phía Mỹ:");
            v.tvVNLabel.setText("Phía Việt Nam:");
            v.tvUSBullets.setText(m.asBulletedStringUs());
            v.tvVNBullets.setText(m.asBulletedStringVn());
        } else if (h instanceof VHSlides) {
            SlidesItem m = (SlidesItem) it;
            VHSlides v = (VHSlides) h;
            v.tvStatic.setText(TextUtils.isEmpty(m.getTitle()) ? "" : m.getTitle());
            v.vp.setAdapter(new SlidePageAdapter(m.getSlides(), v.itemView.getContext()));
        } else if (h instanceof VHVideo) {
            VideoItem m = (VideoItem) it;
            VHVideo v = (VHVideo) h;
            v.tvTitle.setText(TextUtils.isEmpty(m.getTitle()) ? "" : m.getTitle());
            YouTubeUtils.loadVideo(v.yt, m.getVideoUrlOrId(), lifecycleOwner);
        }
    }
//    else if (h instanceof VHMap) {
//        MapItem m = (MapItem) it;
//        VHMap v = (VHMap) h;
//
//        v.mapView.onCreate(null);
//        v.mapView.getMapAsync(googleMap -> {
//            com.google.android.gms.maps.model.LatLng location =
//                    new com.google.android.gms.maps.model.LatLng(m.getLatitude(), m.getLongitude());
//            googleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions()
//                    .position(location)
//                    .title(m.getLocationName()));
//            googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(location, 10f));
//        });
    static class VHHeader extends RecyclerView.ViewHolder {
        ImageView imgCover; TextView tvTitle, tvSubtitle, tvPeriod;
        VHHeader(@NonNull View v) {
            super(v);
            imgCover = v.findViewById(R.id.imgCover);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvSubtitle = v.findViewById(R.id.tvSubtitle);
            tvPeriod = v.findViewById(R.id.tvPeriod);
        }
    }
    static class VHIntro extends RecyclerView.ViewHolder {
        TextView tvContent; ImageView imgDocument;
        VHIntro(@NonNull View v) {
            super(v);
            tvContent = v.findViewById(R.id.tvContent);
            imgDocument = v.findViewById(R.id.imgDocument);
        }
    }
//    static class VHMap extends RecyclerView.ViewHolder {
//        MapView mapView;
//        VHMap(View v) {
//            super(v);
//            mapView = v.findViewById(R.id.mapView);
//        }
//    }
    static class VHSection extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBullets;
        VHSection(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvSectionTitle);
            tvBullets = v.findViewById(R.id.tvBullets);
        }
    }
    public class VHSection2 extends RecyclerView.ViewHolder {
        TextView tvSectionTitle, tvUSLabel, tvUSBullets, tvVNLabel, tvVNBullets;

        public VHSection2(@NonNull View itemView) {
            super(itemView);
            tvSectionTitle = itemView.findViewById(R.id.tvSectionTitle);
            tvUSLabel      = itemView.findViewById(R.id.tvUSLabel);
            tvUSBullets    = itemView.findViewById(R.id.tvUSBullets);
            tvVNLabel      = itemView.findViewById(R.id.tvVNLabel);
            tvVNBullets    = itemView.findViewById(R.id.tvVNBullets);
        }
    }
    static class VHSlides extends RecyclerView.ViewHolder {
        TextView tvStatic; ViewPager2 vp; TabLayout dots;
        VHSlides(@NonNull View v) {
            super(v);
            tvStatic = v.findViewById(R.id.tvSlidesTitleStatic);
            vp = v.findViewById(R.id.vpSlides);
            dots = v.findViewById(R.id.tabDots);
        }
    }
    static class VHVideo extends RecyclerView.ViewHolder {
        TextView tvTitle; YouTubePlayerView yt;
        VHVideo(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvVideoTitle);
            yt = v.findViewById(R.id.ytPlayer);
        }
    }
}