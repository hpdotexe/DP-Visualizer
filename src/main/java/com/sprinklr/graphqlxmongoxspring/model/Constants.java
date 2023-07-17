package com.sprinklr.graphqlxmongoxspring.model;

import io.github.cdimascio.dotenv.Dotenv;

public class Constants {
    public static final Dotenv dotenv = Dotenv.load();

    //-------------------AZURE AD CONSTANTS--------------------------
    public static final String TENANT_ID = dotenv.get("TENANT.ID");
    public static final String CLIENT_ID = dotenv.get("CLIENT.ID");;
    public static final String CLIENT_SECRET= dotenv.get("CLIENT.SECRET");
    public static final String APP_SCOPE = dotenv.get("APP.SCOPE");;
    public static final String READ_GROUP_OID_AAD = dotenv.get("READ.GROUP.OID.AAD");;
    public static final String READWRITE_GROUP_OID_AAD = dotenv.get("READWRITE.GROUP.OID.AAD");;

    //-------------------MONGO CONSTANTS--------------------------
    public static final String MONGO_URI = dotenv.get("MONGO.URI");
    public static final String MONGO_DATABASE = "Sprinklr";
    public static final String MONGO_COLLECTION_DP = "DP";
    public static final String MONGO_COLLECTION_TICKET = "Tickets";
}
