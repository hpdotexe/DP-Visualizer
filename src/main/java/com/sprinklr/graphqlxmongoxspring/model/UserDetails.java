package com.sprinklr.graphqlxmongoxspring.model;

import lombok.Data;

@Data
public class UserDetails {
    private String name;
    private String upn;
    private String permission;
}
