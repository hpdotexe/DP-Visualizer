package com.sprinklr.graphqlxmongoxspring.model;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;

public class Constants {
    public static final Dotenv dotenv = Dotenv.load();

    //-------------------AZURE AD CONSTANTS--------------------------
    public static final String TENANT_ID = "bb2dbde6-a22c-41cb-b77d-0ebb00cd16fc";
    public static final String CLIENT_ID = "7c9fe36e-b6a3-4af7-a5dd-688b714eea40";
    public static final String CLIENT_SECRET= dotenv.get("CLIENT.SECRET");
    public static final String SCOPE = "api://7c9fe36e-b6a3-4af7-a5dd-688b714eea40/Users.Info openid";
    public static final String READ_GROUP_OID_AAD = "2357e3db-e0b7-4896-bc4f-d15cefca403b";
    public static final String READWRITE_GROUP_OID_AAD = "487d809f-e90a-4609-828c-d0ee6641bcdd";

    //-------------------MONGO CONSTANTS--------------------------
    public static final String MONGO_URI = dotenv.get("MONGO.URI");
    public static final String MONGO_DATABASE = "Sprinklr";
    public static final String MONGO_COLLECTION_DP = "DP";
    public static final String MONGO_COLLECTION_TICKET = "Tickets";
}
