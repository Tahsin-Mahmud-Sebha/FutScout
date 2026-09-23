package com.goat.futscout.Repository;

import com.goat.futscout.Model.Player;
import io.micrometer.core.annotation.Timed;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlayerRepository extends MongoRepository<Player, String> {

    @Timed(value = "repository.player.search.time", description = "Time taken to execute search")
    List<Player> findByNameContainingIgnoreCaseOrClubContainingIgnoreCase(String name, String club);

    List<Player> findByPositionIgnoreCase(String position);

    List<Player> findByClubIgnoreCase(String club);
}
