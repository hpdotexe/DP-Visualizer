package com.sprinklr.graphqlxmongoxspring.service;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;
import com.sprinklr.graphqlxmongoxspring.model.DPData;
import com.sprinklr.graphqlxmongoxspring.model.Property;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.sprinklr.graphqlxmongoxspring.model.Constants.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Service
public class MongoService implements IMongoService {
    private static final MongoCollection<DPData> collection = initializeData();

    @Override
    public DPData getDPWithId(String id) {
        return collection.find(eq("_id", id)).first();
    }

    @Override
    public List<DPData> getDPWithPropertyAndPartner(String property, String partner) {
        return FindIterableToList(collection.find(and(eq("property", property), eq("partner", partner))));
    }

    @Override
    public List<DPData> getDPWithPartner(String partner) {
        return FindIterableToList(collection.find(eq("partner", partner)).sort(Sorts.ascending("partner")));
    }

    @Override
    public List<DPData> getDPWithProperty(String property) {
        return FindIterableToList(collection.find(eq("property", property)).sort(Sorts.descending("partner")));
    }

    @Override
    public List<Property> getAllProperties() {
        MongoClient mongoClient = getMongoClient();
        MongoDatabase db = mongoClient.getDatabase(MONGO_DATABASE);
        MongoCollection<Property> propCollection= db.getCollection("Properties", Property.class);
        FindIterable<Property> props = propCollection.find();
        List<Property> propList = new ArrayList<>();
        for(Property prop:props){
            propList.add(prop);
        }
        return propList;
    }

    @Override
    public List<Property> getPropWithTags(String tags){
        MongoClient mongoClient = getMongoClient();
        MongoDatabase db = mongoClient.getDatabase(MONGO_DATABASE);
        MongoCollection<Property> propCollection= db.getCollection("Properties", Property.class);
        String[] tagArray = tags.replaceAll(" ","").split(",");
        FindIterable<Property> props = propCollection.find(all("tags",tagArray));
        List<Property> list = new ArrayList<>();
        for(Property prop:props) list.add(prop);
        mongoClient.close();
        return list;
    }

    @Override
    public DPData upsertDPForPartner(DPData dp, String mode) {
        if (dp.getId() != null && !Objects.equals(dp.getId(), "null")) {
            if(Objects.equals(mode, "Append")) appendUtil(dp);
            else if(Objects.equals(mode, "Delete")) deleteUtil(dp);
            collection.deleteOne(eq("_id", dp.getId()));
        } else {
            dp.setId(new ObjectId().toString());
            extractKeyFromProperty(dp,false);
        }
        collection.insertOne(dp);
        return dp;
    }

    public void deleteUtil(DPData dp){
        DPData oldDP = collection.find(eq("_id", dp.getId())).first();
        assert oldDP != null;
        dp.setValue(dp.getValue().replaceAll("\\s","")); //remove spaces from value
        String newValue =dp.getValue(), oldValue = oldDP.getValue();
        if(Objects.equals(dp.getType(), "MAP")) {
            newValue = newValue.substring(1,newValue.length()-1);
            oldValue = oldValue.substring(1,oldValue.length()-1);
        }
        List<String> updateValueList = new ArrayList<>(Arrays.asList(newValue.split(",")));
        List<String> oldValueList = new ArrayList<>(Arrays.asList(oldValue.split(",")));
        for(String value: updateValueList) oldValueList.remove(value);
        String finalVal = String.join(",",oldValueList);
        if(Objects.equals(dp.getType(), "MAP")) finalVal = '{' + finalVal + '}';
        dp.setValue(finalVal);
    }
    public void appendUtil(DPData dp){
        DPData oldDP = collection.find(eq("_id", dp.getId())).first();
        assert oldDP != null;
        dp.setValue(dp.getValue().replaceAll("\\s","")); //remove spaces from value
        if(Objects.equals(dp.getType(), "MAP")) {
            String oldMap = oldDP.getValue();
            if(Objects.equals(oldMap, "")) return;
            oldMap=oldMap.substring( 0,oldMap.length() - 1);
            dp.setValue(oldMap+","+dp.getValue().substring(1));
        }
        else dp.setValue(oldDP.getValue() + ',' + dp.getValue());
    }

    private void extractKeyFromProperty(DPData dp, boolean allPartners) {
        String[] tokens = dp.getProperty().split("_");
        String key = "";
        for (String token : tokens) {
            key += token.toLowerCase() + '.';
        }
        if(allPartners) key+="all-partners";
        else if (Objects.equals(dp.getLevel(), "PARTNER")) key += dp.getPartner();
        else if (Objects.equals(dp.getLevel(), "CLIENT_PARTNER")) key += dp.getPartner() +"."+dp.getClient();
        else key=key.substring( 0,key.length() - 1);
        dp.setKey(key);
    }

    @Override
    public boolean deleteDP(String id) {
        collection.deleteOne(eq("_id", id));
        return true;
    }

    @Override
    public DPData batchUpdateDP(DPData dp, String mode) {
        if (Objects.equals(dp.getKey(), "ALL-PARTNERS")) {
            updateAllPartners(dp,mode);
        } else {
            updateListedPartners(dp,mode);
        }
        return dp;
    }

    private void updateListedPartners(DPData dp, String mode) {
        String[] partnersToUpdate = dp.getKey().replaceAll("\\s","").split(",");
        for (String partner : partnersToUpdate) {
            FindIterable<DPData> DPs = collection.find(and(eq("property", dp.getProperty()), eq("partner", partner)));
            for (DPData temp : DPs) {
                temp.setValue(dp.getValue());
                temp.setModifiedBy(dp.getModifiedBy());
                temp.setModifiedTime(dp.getModifiedTime());
                temp.setReason(dp.getReason());
                if(Objects.equals(mode, "Append"))appendUtil(temp);
                else if(Objects.equals(mode, "Delete")) deleteUtil(dp);
                collection.deleteOne(eq("_id", temp.getId()));
                collection.insertOne(temp);
            }
        }
    }

    private void updateAllPartners(DPData dp,String mode) {
        String globalProperty = dp.getProperty() + "_ALL-PARTNERS";
        DPData dpIfAlreadyExists = collection.find(eq("property", globalProperty)).first();
        if (dpIfAlreadyExists != null) {
            dp.setId((dpIfAlreadyExists.getId()));
            dp.setKey(dpIfAlreadyExists.getKey());
            if(Objects.equals(mode, "Append")) appendUtil(dp);
            collection.deleteOne(eq("_id", dpIfAlreadyExists.getId()));
        } else {
            dp.setId(new ObjectId().toString());
            extractKeyFromProperty(dp,true);
        }
        dp.setProperty(globalProperty);
        collection.insertOne(dp);
    }
    @Override
    public DPData getAllPartners(String property){
        return collection.find(eq("property", property+"_ALL-PARTNERS")).first();
    }

    @Override
    public List<DPData> getDPWithPartnerAndClient(DPData dp){
        List<DPData> list=null;
        if(dp.getPartner()!=null && dp.getPartner()!=""){
            if(dp.getClient()!=null && dp.getClient()!=""){
                list = FindIterableToList(collection.find(and(eq("partner",dp.getPartner()),eq("client",dp.getClient()),eq("property",dp.getProperty()))));
            }else{
                list = FindIterableToList(collection.find(and(eq("partner",dp.getPartner()),eq("property",dp.getProperty()))));
            }
        }else{
            list = FindIterableToList(collection.find(and(eq("client",dp.getClient()),eq("property",dp.getProperty()))));
        }
        return list;
    }
    private static MongoCollection<DPData> initializeData() {
        MongoClient mongoClient = getMongoClient();
        MongoDatabase db = mongoClient.getDatabase(MONGO_DATABASE);
        return db.getCollection(MONGO_COLLECTION_DP, DPData.class);
    }

    private static MongoClient getMongoClient(){
        ConnectionString connectionString = new ConnectionString(MONGO_URI);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
        return  MongoClients.create(clientSettings);
    }

    public static List<DPData> FindIterableToList(FindIterable<DPData> findIterable) {
        List<DPData> list = new ArrayList<>();
        for ( DPData dp : findIterable) list.add(dp);
        return list;
    }
}
