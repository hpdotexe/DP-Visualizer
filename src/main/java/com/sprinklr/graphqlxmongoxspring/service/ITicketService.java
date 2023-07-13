package com.sprinklr.graphqlxmongoxspring.service;

import com.sprinklr.graphqlxmongoxspring.model.Ticket;

import java.util.List;

public interface ITicketService {

    List<Ticket> getAllTickets();

    List<Ticket> getUserTickets(String upn);

    void resolveTicket(Ticket ticket);

    void createTicket(Ticket ticket);
}
