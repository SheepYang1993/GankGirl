package com.sheepyang.gankgirl.model.school;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/7/13.
 */
public class ClassInfo extends BmobObject{
    private String name;
    private List<Student> studentList;
}
