package com.example.vietnamhistoryapplication.stageDetail;

import java.util.ArrayList;
import java.util.List;

public class StageDetailItem {
     public String slug;
     public String title;
     public String stageRange;
     public String overview;
     public String image;
     public List<String> details;
     public List<String> result;
     public String impactOnPresent;


     public StageDetailItem(String slug, String title, String stageRange, String overview, String image, List<String> details, List<String> result, String impactOnPresent) {
         this.slug = slug;
         this.title = title;
         this.stageRange = stageRange;
         this.overview = overview;
         this.image = image;
         this.details = details;
         this.result = result;
         this.impactOnPresent = impactOnPresent;
     }

}
