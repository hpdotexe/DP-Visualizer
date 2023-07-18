package com.sprinklr.graphqlxmongoxspring.api;

import com.sprinklr.graphqlxmongoxspring.model.Property;
import com.sprinklr.graphqlxmongoxspring.model.RequiresAdminAccess;
import com.sprinklr.graphqlxmongoxspring.model.Ticket;
import com.sprinklr.graphqlxmongoxspring.service.IMongoService;
import com.sprinklr.graphqlxmongoxspring.model.DPData;
import com.sprinklr.graphqlxmongoxspring.service.ITicketService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
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

    public GraphqlAPI(){}

    //=========================VIEW QUERIES===========================
    //------------------------MONGO QUERIES--------------------------
    @GraphQLQuery(name = "getDPWithId")
    public DPData getDPWithId(@GraphQLArgument(name = "id") String id) {
        return mongoService.getDPWithId(id);
    }

    @GraphQLQuery(name = "getAllProperties", description = "Get all properties")
    public List<Property> getAllProperties() { return mongoService.getAllProperties(); }

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
    public List<Property> getPropWithTags(@GraphQLArgument(name="tags")String tags){return mongoService.getPropWithTags(tags);}
    //------------------------TICKET QUERIES--------------------------
    @GraphQLMutation(name = "createTicket")
    public void createTicket(@GraphQLArgument(name="ticket") Ticket ticket){ ticketService.createTicket(ticket);}

    @GraphQLQuery(name= "getUserTickets")
    public List<Ticket> getUserTickets(@GraphQLArgument(name="upn") String upn){return ticketService.getUserTickets(upn);}



    //=========================EDIT QUERIES===========================
    //------------------------MONGO QUERIES--------------------------
    @GraphQLMutation(name = "upsertDPForPartner")
    @RequiresAdminAccess
    public DPData upsertDPForPartner(@GraphQLArgument(name = "prop") DPData dp,@GraphQLArgument(name="mode") String mode) {return mongoService.upsertDPForPartner(dp,mode); }

    @GraphQLMutation(name = "deleteDP")
    @RequiresAdminAccess
    public boolean deleteDP(@GraphQLArgument(name = "id") String id) { return mongoService.deleteDP(id); }

    @GraphQLMutation(name= "batchUpdateDP")
    @RequiresAdminAccess
    public DPData batchUpdateDP(@GraphQLArgument(name="prop") DPData dp,@GraphQLArgument(name="mode")String mode){return mongoService.batchUpdateDP(dp,mode);}

    //------------------------TICKET QUERIES--------------------------

    @GraphQLQuery(name= "getAllTickets")
    @RequiresAdminAccess
    public List<Ticket> getAllTickets(){return ticketService.getAllTickets();}

    @GraphQLMutation(name= "resolveTicket")
    @RequiresAdminAccess
    public void resolveTicket(@GraphQLArgument(name="ticket") Ticket ticket){ticketService.resolveTicket(ticket);}
}
