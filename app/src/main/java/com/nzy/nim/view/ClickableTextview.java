package com.nzy.nim.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.widget.TextView;

import com.nzy.nim.tool.common.QYClickableSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mac on 16/1/13.
 */

public class ClickableTextview extends TextView {
    private boolean isInit = false;
    private Context context;

    public ClickableTextview(Context context) {
        super(context);
        this.context = context;
    }

    public ClickableTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ClickableTextview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Deprecated
    public void linkifyCustorm() {
        Pattern pattern = Pattern.compile("((http://|https://){1}[\\w\\.\\-/:]+)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)");
        Linkify.addLinks(this, pattern, QYUriMatcher.getUriScheme(""));
        this.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (isInit) return;
        isInit = true;
        /* 引入一个变量防止递归调用 */
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) this.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();// should clear old spans
//            style.clear();
            for (URLSpan url : urls) {
//                if (Check.isMobileNO(text.toString())||Check.isTelephone(text.toString())) {
//                QYClickableSpan myURLSpan = new QYClickableSpan(getContext(), text.toString(), QYClickableSpan.Type.PHONE);
//                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                }else{
                    QYClickableSpan myURLSpan = new QYClickableSpan(getContext(), url.getURL(), QYClickableSpan.Type.URL);
                    style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                }
            }
            this.setText(style);
        }
        isInit = false;
    }

    public static class Check {
        // 判断电话
        public static boolean isTelephone(String phonenumber) {

//            String phone = "0\\d{2,3}-\\d{7,8}";
            String phone = "^(0[0-9]{2,3}/-)?([2-9][0-9]{6,7})+(/-[0-9]{1,4})?$";


            Pattern p = Pattern.compile(phone);

            Matcher m = p.matcher(phonenumber);

            return m.matches();

        }

        // 判断手机号
        public static boolean isMobileNO(String mobiles) {

            Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

            Matcher m = p.matcher(mobiles);

            return m.matches();

        }
    }
}
