package com.hanabi.backend.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hanabi.api.models.Game;
import com.hanabi.interfaces.Encapsulating;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tim on 8-7-15.
 */
@Data
public class GameInstance extends Encapsulating<Game> {

    private List<PlayerInstance> players;

    public GameInstance() {
        this.players = Lists.newArrayList();
    }

    public void addPlayer(final PlayerInstance player) {
        this.players.add(player);
    }

    public int getNextPlayerId() {
        return players.size() + 1;
    }

    @Override
    public Game transform() {
        Game game = this.getEncapsulatable();

        game.setPlayers(
                ImmutableList.copyOf(
                        this.players.stream()
                        .map(PlayerInstance::transform)
                        .collect(Collectors.toList())
                )
        );

        return game;
    }
}
