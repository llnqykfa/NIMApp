package com.nzy.nim.activity.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Window;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.fragment.ImageDetailFragment;
import com.nzy.nim.view.HackyViewPager;

import java.util.ArrayList;

/**
 * @TODO 图片浏览
 */
public class ImagePagerActivity extends FragmentActivity {
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    private static final String STATE_POSITION = "STATE_POSITION";
    private HackyViewPager mPager;
    private int pagerPosition;
    private TextView indicator;

    public static void actionIntent(Context context, ArrayList<String> imgUrl) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        intent.putStringArrayListExtra(EXTRA_IMAGE_URLS, imgUrl);
        context.startActivity(intent);
    }

    public static void actionIntent(Context context, ArrayList<String> imgUrl, int currentItem) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        intent.putStringArrayListExtra(EXTRA_IMAGE_URLS, imgUrl);
        intent.putExtra(EXTRA_IMAGE_INDEX, currentItem);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager);

        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        ArrayList<String> urls = getIntent().getStringArrayListExtra(
                EXTRA_IMAGE_URLS);

        mPager = (HackyViewPager) findViewById(R.id.image_detail_pager_pager);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(
                getSupportFragmentManager(), urls);
        mPager.setAdapter(mAdapter);
        indicator = (TextView) findViewById(R.id.image_detail_pager_indicator);
        CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
                .getAdapter().getCount());
        indicator.setText(text);
        // 更新下标
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                CharSequence text = getString(R.string.viewpager_indicator,
                        arg0 + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ArrayList<String> fileList;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList.get(position);
            return ImageDetailFragment.newInstance(url);
        }
    }
}