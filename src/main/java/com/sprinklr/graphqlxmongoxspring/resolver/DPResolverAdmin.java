package com.sprinklr.graphqlxmongoxspring.resolver;

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
public class DPResolverAdmin {
    @Autowired
    IMongoService mongoService;
    @Autowired
    ITicketService ticketService;

    public DPResolverAdmin(IMongoService mongoService, ITicketService ticketService) {
        this.mongoService = mongoService;
        this.ticketService = ticketService;
    }

    //------------------------MONGO QUERIES--------------------------
    @GraphQLMutation(name = "upsertDPForPartner")
    public DPData upsertDPForPartner(@GraphQLArgument(name = "prop") DPData dp,@GraphQLArgument(name="mode") String mode) {return mongoService.upsertDPForPartner(dp,mode); }

    @GraphQLMutation(name = "deleteDP")
    public boolean deleteDP(@GraphQLArgument(name = "id") String id) { return mongoService.deleteDP(id); }

    @GraphQLMutation(name= "batchUpdateDP")
    public DPData batchUpdateDP(@GraphQLArgument(name="prop") DPData dp,@GraphQLArgument(name="mode")String mode){return mongoService.batchUpdateDP(dp,mode);}

    @GraphQLQuery(name = "getDPWithPartnerAndClient")
    public List<DPData> getDPWithPartnerAndClient(@GraphQLArgument(name = "prop") DPData dp){return mongoService.getDPWithPartnerAndClient(dp);}

    //------------------------TICKET QUERIES--------------------------
    @GraphQLMutation(name = "createTicket")
    public void createTicket(@GraphQLArgument(name="ticket") Ticket ticket){ ticketService.createTicket(ticket);}

    @GraphQLQuery(name= "getAllTickets")
    public List<Ticket> getAllTickets(){return ticketService.getAllTickets();}

    @GraphQLQuery(name= "getUserTickets")
    public List<Ticket> getUserTickets(@GraphQLArgument(name="upn") String upn){return ticketService.getUserTickets(upn);}

    @GraphQLMutation(name= "resolveTicket")
    public void resolveTicket(@GraphQLArgument(name="ticket") Ticket ticket){ticketService.resolveTicket(ticket);}
}
