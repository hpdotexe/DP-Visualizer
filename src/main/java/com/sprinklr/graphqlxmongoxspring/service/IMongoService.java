package com.sprinklr.graphqlxmongoxspring.service;

import com.sprinklr.graphqlxmongoxspring.model.DPData;
import com.sprinklr.graphqlxmongoxspring.model.Property;

import java.util.List;

public interface IMongoService {
    List<Property> getAllProperties();

    public List<DPData> getDPWithProperty(String property);

    DPData getDPWithId(String id);

    List<DPData> getDPWithPropertyAndPartner(String property, String partner);

    List<DPData> getDPWithPartner(String partner);

    List<DPData> getDPWithPartnerAndClient(DPData dp);

    DPData upsertDPForPartner(DPData dp, String mode);

    boolean deleteDP(String id);

    DPData batchUpdateDP(DPData dp,String mode);

    DPData getAllPartners(String property);

    List<Property> getPropWithTags(String tags);
}
