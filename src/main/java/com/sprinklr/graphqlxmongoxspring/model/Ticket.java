package com.sprinklr.graphqlxmongoxspring.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;

@Data
public class Ticket {
    @Id
    @BsonProperty("_id")
    private String id;
    private String createdBy;
    private double creationTime;
    private String query;
    private String details;
    private boolean resolved;
    private String resolvedBy;
    private double resolutionTime;
    private boolean accepted;
}
