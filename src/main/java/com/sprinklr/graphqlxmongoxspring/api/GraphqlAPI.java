package com.sprinklr.graphqlxmongoxspring.api;

import com.sprinklr.graphqlxmongoxspring.model.Property;
import com.sprinklr.graphqlxmongoxspring.model.Ticket;
import com.sprinklr.graphqlxmongoxspring.service.IMongoService;
import com.sprinklr.graphqlxmongoxspring.model.DPData;
import com.sprinklr.graphqlxmongoxspring.service.ITicketService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GraphqlAPI {
    @Autowired
    IMongoService mongoService;
    @Autowired
    ITicketService ticketService;

    public GraphqlAPI(IMongoService mongoService, ITicketService ticketService) {
        this.mongoService = mongoService;
        this.ticketService = ticketService;
    }

    //------------------------MONGO QUERIES--------------------------
    @GraphQLQuery(name = "getDPWithId")
    public DPData getDPWithId(@GraphQLArgument(name = "id") String id) {return mongoService.getDPWithId(id);}

    @GraphQLQuery(name = "getAllDPs", description = "Get all properties")
    public List<DPData> getAllDPs() { return mongoService.getAllDPs(); }

    @GraphQLQuery(name = "getDPWithPartner")
    public List<DPData> getDPWithPartner(@GraphQLArgument(name = "partner") String partner) {return mongoService.getDPWithPartner(partner);}

    @GraphQLQuery(name = "getDPWithPropertyAndPartner")
    public List<DPData> getDPWithPropertyAndPartner(@GraphQLArgument(name = "property") String property,@GraphQLArgument(name = "partner") String partner) {return mongoService.getDPWithPropertyAndPartner(property,partner);}

    @GraphQLQuery(name = "getDPWithProperty")
    public List<DPData> getDPWithProperty(@GraphQLArgument(name = "property") String property) { return mongoService.getDPWithProperty(property);}

    @GraphQLQuery(name ="getAllPartners")
    public DPData getAllPartners(@GraphQLArgument(name="property")String property) {return mongoService.getAllPartners(property);}

    @GraphQLQuery(name = "getDPWithPartnerAndClient")
    public List<DPData> getDPWithPartnerAndClient(@GraphQLArgument(name = "prop") DPData dp){return mongoService.getDPWithPartnerAndClient(dp);}

    @GraphQLQuery(name = "getPropertyWithTags")
    public List<Property> getPropByTags(@GraphQLArgument(name="tags")String tags){return mongoService.getPropWithTags(tags);}
    //------------------------TICKET QUERIES--------------------------

    @GraphQLQuery(name= "getUserTickets")
    public List<Ticket> getUserTickets(@GraphQLArgument(name="upn") String upn){return ticketService.getUserTickets(upn);}
}
