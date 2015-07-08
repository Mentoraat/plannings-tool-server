package com.hanabi.backend.game;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import com.hanabi.api.models.Game;

import java.util.List;
import java.util.Map;

/**
 * Created by tim on 8-7-15.
 */
@Singleton
public class GameBackend {

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

        PlayerInstance player = new PlayerInstance();
        player.setId(instance.getNextPlayerId());

        instance.addPlayer(player);

        return instance;
    }
}
