package com.nzy.nim.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.nzy.nim.R;
import com.nzy.nim.activity.main.AddNewFriendsActivity;
import com.nzy.nim.activity.main.FriendsInfoActivity;
import com.nzy.nim.zxing.MipcaActivityCapture;

import java.util.ArrayList;
import java.util.List;

public class ContactsContainerFragment extends Fragment implements
        OnClickListener {
    List<Fragment> tabFragment = new ArrayList<Fragment>();
    int tabSize = 0;
    ViewPager mViewPager;
    FragmentPagerAdapter mAdapter;
    /**
     * 顶部的三个TextView
     */
    private Button mContacts;
    private Button mRing;
    private Button mIntersted;
    /**
     * Tab的那个引导线
     */
    private ImageView mTabLine;
    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;
    private ContactsFragment contacts;
    private MyGroupsFragment myGroups;
    private MyInterestFragment interest;
    private PopupMenu popup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_container_contacts,
                container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTopBar(getView());
        initView(getView());
        setAdapter();
        setOnPageListener();
    }

    /**  
     * @author LIUBO
     * @date 2015-3-30下午4:02:50
     * @TODO 设置适配器
     */
    private void setAdapter() {
        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return tabSize;
            }

            @Override
            public Fragment getItem(int position) {
                return tabFragment.get(position);
            }
        };
        mViewPager.setAdapter(mAdapter);
    }

    private void setOnPageListener() {
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // 重置所有TextView的字体颜色
                resetTextView();
                switch (position) {
                    case 0:
                        mContacts.setTextColor(Color.parseColor("#368bda"));
                        break;
                    case 1:
                        mRing.setTextColor(Color.parseColor("#368bda"));
                        break;
                    case 2:
                        mIntersted.setTextColor(Color.parseColor("#368bda"));
                        break;
                }
                currentIndex = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                float a = positionOffset;
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLine
                        .getLayoutParams();
                if (currentIndex == 0 && position == 0 && positionOffset == 0.0)// 0
                {
                    lp.leftMargin = (int) (positionOffset
                            * (screenWidth * 1.0 / tabSize) + currentIndex
                            * (screenWidth / tabSize));
                } else if (currentIndex == 1 && position == 1
                        && positionOffset == 0.0) // 1
                {

                    lp.leftMargin = (int) (positionOffset
                            * (screenWidth * 1.0 / tabSize) + currentIndex
                            * (screenWidth / tabSize));
                } else if (currentIndex == 2 && position == 2
                        && positionOffset == 0.0) // 2
                {
                    lp.leftMargin = (int) ((positionOffset)
                            * (screenWidth * 1.0 / tabSize) + currentIndex
                            * (screenWidth / tabSize));
                }
                mTabLine.setLayoutParams(lp);

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /**
     * @param view
     * @author LIUBO
     * @date 2015-3-30下午3:54:50
     * @TODO 初始化标题栏
     */
    private void initTopBar(View view) {
        ImageView back = (ImageView) view.findViewById(R.id.top_bar_back);
        final Button next = (Button) view.findViewById(R.id.top_bar_next);
        TextView title = (TextView) view.findViewById(R.id.top_bar_content);
        title.setText("我的圈子");
        back.setVisibility(View.INVISIBLE);
        next.setText("添加好友");
        next.setTextSize(14);
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到添加好友界面
                AddNewFriendsActivity.actionIntent(getActivity());
            }
        });

    }


    private MipcaActivityCapture.ScanResultHandlerSerilize scanResultHandler = new MipcaActivityCapture().new ScanResultHandlerSerilize() {
        @Override
        public void onResult(String result) {
            if (!result.startsWith("QYID")) {
                return;
            }
            String s = result.replace("QYID", "");
            FriendsInfoActivity.actionIntent(getActivity(), s);
        }
    };

    /**
     * @param view
     * @author LIUBO
     * @date 2015-3-30下午4:10:08
     * @TODO 初始化布局空间
     */
    private void initView(View view) {
        mViewPager = (ViewPager) view
                .findViewById(R.id.fragment_container_contacts_pager);
        mContacts = (Button) view.findViewById(R.id.top_tabs_tv_contacts);
        mRing = (Button) view.findViewById(R.id.top_tabs_tv_ring);
        mIntersted = (Button) view.findViewById(R.id.top_tabs_tv_intersted);
        contacts = new ContactsFragment();
        myGroups = new MyGroupsFragment();
        interest = new MyInterestFragment();
        tabFragment.add(contacts);
        tabFragment.add(myGroups);
        tabFragment.add(interest);
        tabSize = tabFragment.size();
        mContacts.setOnClickListener(this);
        mRing.setOnClickListener(this);
        mIntersted.setOnClickListener(this);
        initTabLine(view);
    }

    /**
     * 根据屏幕的宽度，初始化引导线的宽度
     */
    private void initTabLine(View view) {
        mTabLine = (ImageView) view.findViewById(R.id.id_tab_line);
        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLine
                .getLayoutParams();
        lp.width = screenWidth / tabSize;
        mTabLine.setLayoutParams(lp);
    }

    /**
     * 重置颜色
     */
    protected void resetTextView() {
        mContacts.setTextColor(Color.parseColor("#656565"));
        mRing.setTextColor(Color.parseColor("#656565"));
        mIntersted.setTextColor(Color.parseColor("#656565"));
    }

    @Override
    public void onClick(View v) {
        resetTextView();
        switch (v.getId()) {
            case R.id.top_tabs_tv_contacts:
                mContacts.setTextColor(Color.parseColor("#368bda"));
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.top_tabs_tv_ring:
                mRing.setTextColor(Color.parseColor("#368bda"));
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.top_tabs_tv_intersted:
                mIntersted.setTextColor(Color.parseColor("#368bda"));
                mViewPager.setCurrentItem(2, false);
                break;
        }
    }
}
