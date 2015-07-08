package com.hanabi.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.hanabi.api.models.Card;
import com.hanabi.api.models.Color;
import com.hanabi.api.models.Game;
import com.hanabi.api.models.Player;
import com.hanabi.backend.game.GameBackend;
import com.hanabi.backend.game.GameInstance;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
        return this.gameBackend.createGame().transform();
    }

    @GET
    @Path("{id}/{playerId}")
    public Game getGame(@PathParam("id") int id,
                        @PathParam("playerId") int playerId) {
        Preconditions.checkArgument(playerId > 0, "Player Id must be greater than zero.");
        GameInstance instance = this.gameBackend.getInstance(id);

        Game game = instance.transform();

        List<Card> unknownCards = Lists.newArrayList();

        for (int i = 0; i < GameBackend.NUMBER_OF_CARDS_PER_PLAYER; i++) {
            Card card = new Card();
            card.setColor(Color.UNKNOWN);
            card.setValue("?");

            unknownCards.add(card);
        }

        List<Player> players = game.getPlayers();

        if (players.size() < playerId - 1) {
            throw new IllegalArgumentException("Invalid Player Id for this game.");
        }

        players.get(playerId - 1).setCards(unknownCards);

        return game;
    }

    @GET
    @Path("{id}/addplayer")
    public Game addPlayer(@PathParam("id") int id) {
        return this.gameBackend.addPlayer(id).transform();
    }
}
