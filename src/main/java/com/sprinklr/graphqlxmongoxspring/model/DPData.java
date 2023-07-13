package com.sprinklr.graphqlxmongoxspring.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Sprinklr")
@Data
public class DPData {
    @Id
    @BsonProperty("_id")
    private String id;
    private String key;
    private String value;
    private boolean deleted;
    private String reason;
    private double modifiedTime;
    private String modifiedBy;
    private String property;
    private String client;
    private String partner;
    private String type;
    private String level;
}

