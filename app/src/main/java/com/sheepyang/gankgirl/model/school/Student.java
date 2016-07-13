package com.sheepyang.gankgirl.model.school;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/7/13.
 */
public class Student extends BmobUser {
    private BmobFile avatar;
    public ClassInfo classInfo ;
    public String realName;
    public String nickName;
}
