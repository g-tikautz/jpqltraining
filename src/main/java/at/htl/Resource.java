package at.htl;

import at.htl.model.Penalty;
import at.htl.model.Player;
import at.htl.results.GenderCount;
import at.htl.results.MinMaxAmount;
import at.htl.results.PlayerPenalties;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/api")
public class Resource {

    @Inject
    Repository repo;

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }

    @GET
    @Path("/players")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Player> getPlayers() {
        return repo.getAllPlayers();
    }

    @GET
    @Path("/playersLivingIntown/{town}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Player> getPlayersFromTown(@PathParam("town") String town) {
        return repo.getPlayersLivingInTown(town);
    }

    @GET
    @Path("/getPlayersByGenderAndAge")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlayersByGenderAndAge(@QueryParam("sex") Boolean female, @QueryParam("year") Integer bornBeforeYear) {

        List<Player> playerList = repo.getPlayersByGenderAndAge(female, bornBeforeYear);

        if (bornBeforeYear == null|| female == null) {
            return Response.status(BAD_REQUEST).build();
        } else {
            return Response.ok().entity(playerList).build();
        }
    }

    @GET
    @Path("/getPenaltiesInDateRange")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPenaltiesInDateRange(@QueryParam("start") String start, @QueryParam("end") String end) {

        LocalDate first = LocalDate.parse(start);
        LocalDate last = LocalDate.parse(end);
        List<Penalty> playerList = repo.getPenaltiesInDateRange(first,last);

        if (start == null|| end == null) {
            return Response.status(BAD_REQUEST).build();
        } else {
            return Response.ok().entity(playerList).build();
        }
    }

    @POST
    @Path("/addPenalty/{playerNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPenalty(@PathParam("playerNo") Long playerNo, @QueryParam("date") String penDate, @QueryParam("amount") BigDecimal amount){
        repo.addNewPenaltyForPlayer(playerNo,LocalDate.parse(penDate),amount);

        if (penDate == null|| amount == null) {
            return Response.status(BAD_REQUEST).build();
        }

        return Response.ok().build();
    }

    @PUT
    @Path("changePenaltyAmount/{paymentNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePenaltyAmount(@PathParam("paymentNo") Long paymentNo, @QueryParam("amount") BigDecimal amount){
        if(amount == null){
            Response.status(BAD_REQUEST).build();
        }
        repo.changePenaltyAmount(paymentNo,amount);
        return Response.ok().build();
    }

    @DELETE
    @Path("deletePenalty/{paymentNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePenalty(@PathParam("paymentNo") Long paymentNo){
        Penalty pen = repo.deletePenalty(paymentNo);
        return Response.ok().entity(pen).build();
    }

}