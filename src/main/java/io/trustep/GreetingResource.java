package io.trustep;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
//import org.kie.kogito.incubation.application.AppRoot;
//import org.kie.kogito.incubation.common.DataContext;
//import org.kie.kogito.incubation.common.MapDataContext;
//import org.kie.kogito.incubation.decisions.DecisionIds;
//import org.kie.kogito.incubation.decisions.services.DecisionService;

import java.util.Map;

@Path("/glosa")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

//    @Inject
//    AppRoo tappRoot;

//    @Inject
//    DecisionService svc;

//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public DataContext hello(Map<String, Object> payload) {
//        var id = appRoot
//                .get(DecisionIds.class)
//                .get("https://kie.org/dmn/tiss",
//                        "GuiaRequest");
//        var ctx = MapDataContext.from(payload);
//        return svc.evaluate(id, ctx);
//    }
}