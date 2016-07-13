package com.sheepyang.gankgirl.model.school.schoolmodel;

import com.google.gson.annotations.SerializedName;


public class SchoolFriends
{

    @SerializedName("showapi_res_body")
    public ShowapiResBody showapiResBody;

    @SerializedName("showapi_res_error")
    public String showapiResError;

    @SerializedName("showapi_res_code")
    public int showapiResCode;
}
