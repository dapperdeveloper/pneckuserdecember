
package com.callpneck.model.dashboard;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("subcategory_list")
    @Expose
    private List<SubcategoryList> subcategoryList = null;
    @SerializedName("banner_slider_images")
    @Expose
    private List<BannerSliderImage> bannerSliderImages = null;

    public List<SubcategoryList> getSubcategoryList() {
        return subcategoryList;
    }

    public void setSubcategoryList(List<SubcategoryList> subcategoryList) {
        this.subcategoryList = subcategoryList;
    }

    public List<BannerSliderImage> getBannerSliderImages() {
        return bannerSliderImages;
    }

    public void setBannerSliderImages(List<BannerSliderImage> bannerSliderImages) {
        this.bannerSliderImages = bannerSliderImages;
    }

}
