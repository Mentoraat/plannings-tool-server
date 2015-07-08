package com.hanabi.backend.game;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import com.hanabi.api.models.Color;
import com.hanabi.api.models.Game;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by tim on 8-7-15.
 */
@Singleton
public class GameBackend {

    public static final int NUMBER_OF_CARDS_PER_PLAYER = 4;
    private static final int NUMBER_OF_PLAYERS_PER_GAME = 4;
    private final Map<Integer, GameInstance> games;

    private int lastGameId;

    public GameBackend() {
        this.games = Maps.newHashMap();
        this.lastGameId = 0;
    }

    public GameInstance createGame() {
        final GameInstance instance = new GameInstance();
        final int id = ++this.lastGameId;

        final Game game = new Game();
        game.setId(id);

        instance.setEncapsulatable(game);

        for (int i = 0; i < NUMBER_OF_PLAYERS_PER_GAME; i++) {
            this.addPlayerToInstance(instance);
        }

        games.put(id, instance);

        return instance;
    }

    public GameInstance getInstance(int id) {
        GameInstance instance = games.get(id);

        if (instance == null) {
            throw new IllegalArgumentException("Game does not exist");
        }

        return instance;
    }

    public GameInstance addPlayer(int id) {
        GameInstance instance = this.getInstance(id);

        addPlayerToInstance(instance);

        return instance;
    }

    private void addPlayerToInstance(GameInstance instance) {
        PlayerInstance player = new PlayerInstance();

        for (int i = 0; i < NUMBER_OF_CARDS_PER_PLAYER; i++) {
            player.addCard(Color.BLUE, new Random().nextInt(NUMBER_OF_CARDS_PER_PLAYER));
        }

        player.setId(instance.getNextPlayerId());

        instance.addPlayer(player);
    }
}
