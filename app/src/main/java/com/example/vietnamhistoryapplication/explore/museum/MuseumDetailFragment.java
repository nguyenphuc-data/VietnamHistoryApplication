package com.example.vietnamhistoryapplication.explore.museum;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vietnamhistoryapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MuseumDetailFragment extends Fragment {

    private static final String MUSEUM_360_URL = "https://platform.starglobal3d.com/smart-tourism-360/vietnam/34-tinh-thanh/?startscene=scene_viewhome_mienbac_ha_noi";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.article_museum, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WebView webView = view.findViewById(R.id.webView360); // ← Có khoảng trắng, sửa thành webView360
        FloatingActionButton btnBack = view.findViewById(R.id.btnBack);
        TextView tvTitle = view.findViewById(R.id.tvTitle);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Cấu hình WebView
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl(MUSEUM_360_URL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}