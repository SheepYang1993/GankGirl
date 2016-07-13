package com.sheepyang.gankgirl.model.school;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/7/13.
 */
public class FriendsCircle extends BmobObject{
    //点赞数
    public Integer totalStar;
    //评论数
    public Integer totalComment;
    public String type;
    public ArrayList<BmobFile> imgList;
}
