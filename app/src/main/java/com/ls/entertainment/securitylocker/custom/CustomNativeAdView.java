package com.ls.entertainment.securitylocker.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.Barrier;

import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.ls.entertainment.securitylocker.R;


public class CustomNativeAdView extends FrameLayout {
    FrameLayout rootView;
    com.google.android.gms.ads.nativead.NativeAdView nativeAdView;
    ImageView iconAd, adAppIcon;
    CustomTextView adHeadline, adAdvertiser;
    Barrier barrier, barrier2;
    RatingBar adStars;
    MediaView adMedia;
    AppCompatButton adCallToAction;


    public CustomNativeAdView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public CustomNativeAdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomNativeAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public CustomNativeAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    public void initView(Context context) {
        inflate(context, R.layout.layout_native_ads, this);
        rootView = findViewById(R.id.rootView);
        nativeAdView = findViewById(R.id.nativeAdView);
        iconAd = findViewById(R.id.iconAd);
        adAppIcon = findViewById(R.id.adAppIcon);
        adHeadline = findViewById(R.id.adHeadline);
        adAdvertiser = findViewById(R.id.adAdvertiser);
        barrier = findViewById(R.id.barrier);
        barrier2 = findViewById(R.id.barrier2);
        adStars = findViewById(R.id.adStars);
        adMedia = findViewById(R.id.adMedia);
        adCallToAction = findViewById(R.id.adCallToAction);
    }

    public void binDataNativeAds(NativeAd nativeAd) {
        if (nativeAd == null) return;
        rootView.setVisibility(View.VISIBLE);
        nativeAdView.setMediaView(adMedia);
        nativeAdView.setHeadlineView(adHeadline);
        nativeAdView.setIconView(adAppIcon);
        nativeAdView.setCallToActionView(adCallToAction);
        adHeadline.setText(nativeAd.getHeadline());
        if (nativeAd.getCallToAction() == null) {
            adCallToAction.setVisibility(View.GONE);
        } else {
            adCallToAction.setVisibility(View.VISIBLE);
            adCallToAction.setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            adAppIcon.setVisibility(View.GONE);
        } else {
            adAppIcon.setVisibility(View.VISIBLE);
            adAppIcon.setImageDrawable(nativeAd.getIcon().getDrawable());
        }
        nativeAdView.setStarRatingView(adStars);
        nativeAdView.setAdvertiserView(adAdvertiser);
        if (nativeAd.getStarRating() == null) {
            adStars.setVisibility(View.GONE);
        } else {
            adStars.setVisibility(View.VISIBLE);
            adStars.setRating(Float.parseFloat(String.valueOf(nativeAd.getStarRating())));

        }
        if (nativeAd.getAdvertiser() == null) {
            adAdvertiser.setVisibility(View.INVISIBLE);
        } else {
            adAdvertiser.setVisibility(View.VISIBLE);
            adAdvertiser.setText(nativeAd.getAdvertiser());

        }
        nativeAdView.setNativeAd(nativeAd);

        MediaContent vc = nativeAd.getMediaContent();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc != null) {
            if (vc.hasVideoContent()) {
                adMedia.setMinimumHeight(120);
                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
            } else {
                adMedia.setMinimumHeight(100);
            }
        }
    }
}
