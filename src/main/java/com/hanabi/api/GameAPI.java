package com.hanabi.api;

import com.google.inject.Inject;
import com.hanabi.api.models.Game;
import com.hanabi.backend.game.GameBackend;
import com.hanabi.backend.game.GameInstance;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by tim on 8-7-15.
 */
@Path("/game/")
@Produces(MediaType.APPLICATION_JSON)
public class GameAPI {

    private final GameBackend gameBackend;

    @Inject
    public GameAPI(final GameBackend gameBackend) {
        this.gameBackend = gameBackend;
    }

    @GET
    @Path("create")
    public Game createGame() {
        return this.gameBackend.createGame().getEncapsulatable();
    }

    @GET
    @Path("{id}")
    public Game getAllGames(@PathParam("id") int id) {
        return this.gameBackend.getInstance(id).getEncapsulatable();
    }

    @GET
    @Path("{id}/addplayer")
    public Game addPlayer(@PathParam("id") int id) {
        return this.gameBackend.addPlayer(id).getEncapsulatable();
    }
}
