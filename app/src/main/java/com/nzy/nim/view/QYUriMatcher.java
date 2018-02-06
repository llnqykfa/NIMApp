package com.nzy.nim.view;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;

import com.nzy.nim.activity.main.CommentInfoActivity;
import com.nzy.nim.activity.main.QYWebviewAvtivity;
import com.nzy.nim.activity.main.RingTeamInfoActivity;

/**
 * Created by Administrator on 2016/5/11.
 */
public class QYUriMatcher {
    private static final int BOOK_ISBN = 1;
    private static final int BOOK_LIST = 2;
    private static final int RING_THEME = 3;
    private static final int BOOK_REVIEW = 4;
    private static final int RING_COMMENT = 5;
    private static final int MEET_COMMENT = 6;
    private static String HOST = "quanyou";
    private static String SCHEME_HEAD = "qy://";
    public static String PATH_BOOK_ISBN = "book/*";
    public static String PATH_BOOK_LIST = "booklist/*";
    public static String PATH_RING_THEME = "ringtheme/*";
    public static String PATH_BOOK_REVIEW = "bookreview/*";
    public static String PATH_RING_COMMENT = "ringcomment/*";
    public static String PATH_MEETING = "meetinglist/*";
    private static Uri uri;

    UriMatcher matcher;


    public QYUriMatcher() {
        initRouter();
    }


    private void initRouter() {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
//        matcher.addURI(HOST, "activity", ACTIVITY);
//        matcher.addURI(HOST, "feed_back", FEED_BACK);
        matcher.addURI(HOST, PATH_BOOK_LIST, BOOK_LIST);
        matcher.addURI(HOST, PATH_BOOK_ISBN, BOOK_ISBN);
        matcher.addURI(HOST, PATH_RING_THEME, RING_THEME);
        matcher.addURI(HOST, PATH_BOOK_REVIEW, BOOK_REVIEW);
        matcher.addURI(HOST, PATH_RING_COMMENT, RING_COMMENT);
        matcher.addURI(HOST, PATH_MEETING, MEET_COMMENT);
    }


    /**
     * @param scheme
     * @param uri
     * @return
     */
    private String parseIds(String scheme, Uri uri) {
        scheme = scheme.replace("*", "");
        return uri.toString().replace(scheme, "");
    }


    public void startRouter(Context ctx, Uri uri) {
        int match = matcher.match(uri);
        switch (match) {
            case RING_THEME:
                String ring = parseIds(getUriScheme(PATH_RING_THEME), uri);
                RingTeamInfoActivity.actionIntent(ctx, ring);
                break;
            case RING_COMMENT:
                String ringDynamicID = parseIds(getUriScheme(PATH_RING_COMMENT), uri);
                CommentInfoActivity.actionIntent(ctx, ringDynamicID);
                break;
            case MEET_COMMENT:
//                String meetingId = parseIds(getUriScheme(PATH_MEETING), uri);
//                Intent intent = new Intent(ctx, MeetingDetailActivity.class);
//                if(meetingId.startsWith("isProject")){
//                    //执行签到方法
//                    String s = meetingId.replace("isProject", "");
//                    intent.putExtra("ISPROJECT",true);
//                    intent.putExtra("meetingId",s);
//                }else{
//                    intent.putExtra("meetingId",meetingId);
//                }
//                ctx.startActivity(intent);
                break;
            default:
                QYWebviewAvtivity.loadUrl(ctx, uri.toString());
        }
    }

    public static String getUriScheme(String path) {
        return QYUriMatcher.SCHEME_HEAD + QYUriMatcher.HOST + "/" + path;
    }

    public static void actionUri(Context context, String sUri) {
        Uri uri = Uri.parse(sUri);
        QYUriMatcher uiMatcher = new QYUriMatcher();
        uiMatcher.startRouter(context, uri);
    }
}
