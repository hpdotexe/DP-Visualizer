package com.sprinklr.graphqlxmongoxspring.service;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.sprinklr.graphqlxmongoxspring.model.Ticket;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.sprinklr.graphqlxmongoxspring.model.Constants.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Service
public class TicketService implements ITicketService{
    private static final MongoCollection<Ticket> collection = initializeData();

    @Override
    public void createTicket(Ticket ticket){
        ticket.setId(new ObjectId().toString());
        collection.insertOne(ticket);
    }

    @Override
    public List<Ticket> getAllTickets() {return FindIterableToList(collection.find());}

    @Override
    public List<Ticket> getUserTickets(String upn){
        return FindIterableToList(collection.find(eq("createdBy",upn)));
    }

    @Override
    public void resolveTicket(Ticket ticket){
        Ticket oldTicket = collection.find(eq("_id",ticket.getId())).first();
        ticket.setCreationTime(oldTicket.getCreationTime());
        ticket.setQuery(oldTicket.getQuery());
        collection.deleteOne(eq("_id",oldTicket.getId()));
        collection.insertOne(ticket);
    }

    private static MongoCollection<Ticket> initializeData() {
        ConnectionString connectionString = new ConnectionString(MONGO_URI);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
        MongoClient mongoClient = MongoClients.create(clientSettings);
        MongoDatabase db = mongoClient.getDatabase(MONGO_DATABASE);
        return db.getCollection(MONGO_COLLECTION_TICKET, Ticket.class);
    }

    List<Ticket> FindIterableToList(FindIterable<Ticket> tickets) {
        List<Ticket> list = new ArrayList<>();
        for (Ticket ticket : tickets) list.add(ticket);
        return list;
    }
}
