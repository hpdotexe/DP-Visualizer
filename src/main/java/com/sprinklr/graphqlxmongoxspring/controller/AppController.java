package com.sprinklr.graphqlxmongoxspring.controller;

import com.sprinklr.graphqlxmongoxspring.resolver.DPResolver;
import com.sprinklr.graphqlxmongoxspring.service.AuthService;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLException;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.logging.Logger;

@Controller
@Slf4j
@RequestMapping("/")
public class AppController {
    private final GraphQL graphQL;
    private static final Logger logger = Logger.getLogger("AppController.class");
    @Autowired
    public AppController(DPResolver dpResolver) {
        GraphQLSchema querySchema = new GraphQLSchemaGenerator()
                .withBasePackages("com.sprinklr.graphqlxmongoxspring")
                .withOperationsFromSingleton(dpResolver)
                .generate();
        this.graphQL = new GraphQL.Builder(querySchema).build();
    }

    @PostMapping(value="graphql")
    @ResponseBody
    public Map<String, Object> execute(@RequestBody Map<String, String> request)
            throws GraphQLException {
        if(AuthService.validateToken(request.get("token"))) {
            String query = request.get("query");
            logger.info("Query Received! Query: "+query);
            ExecutionResult result = ExecutionResult.newExecutionResult().build();
            if(query.contains("mutation")) {
                if (!AuthService.hasEditAccess(request.get("token"))) {
                    System.out.println("Ticket raised");
                    String rawQuery = query.replaceAll("\"","\\\\\\\"");
                    String ticketCreationQuery="mutation{createTicket(ticket:{createdBy:\""+request.get("upn")+"\" creationTime: "+System.currentTimeMillis()+" query:\""+rawQuery+"\" details: \""+request.get("details")+"\" resolved: false resolutionTime: 0 accepted: false})}";
                    ExecutionResult ticketResult = graphQL.execute(ticketCreationQuery);
                    return result.getData();
                }
            }
            result = graphQL.execute(query);
            logger.info("Query Executed! Result: " + result.getErrors());
            return result.getData();
        }else System.out.println("Unauthorized access!");
        return null;
    }

    @PostMapping(value="authorization")
    @ResponseBody
    public Map<String,String> authorization(@RequestBody Map<String, String> request) throws Exception {
        return AuthService.getAccessToken(request.get("authCode"),request.get("redirectUri"));
    }
}

