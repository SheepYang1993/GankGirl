package com.sheepyang.gankgirl.model.meizi.taomodel;

import com.google.gson.annotations.SerializedName;


public class TaoFemale
{

    @SerializedName("showapi_res_body")
    public com.sheepyang.gankgirl.model.meizi.taomodel.ShowapiResBody showapiResBody;

    @SerializedName("showapi_res_error")
    public String showapiResError;

    @SerializedName("showapi_res_code")
    public int showapiResCode;
}
