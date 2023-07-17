package com.sprinklr.graphqlxmongoxspring.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;

@Data
public class Property {
    @Id
    @BsonProperty("_id")
    private String id;
    private String name;
    private ArrayList<String> tags;
}
