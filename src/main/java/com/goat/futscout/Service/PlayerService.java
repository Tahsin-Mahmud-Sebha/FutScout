package com.goat.futscout.Service;

import com.goat.futscout.DTO.PlayerDTO;

import java.util.List;

public interface PlayerService {

    PlayerDTO createPlayer(PlayerDTO playerDTO);

    List<PlayerDTO> getAllPlayers();

    PlayerDTO getPlayerById(String id);

    PlayerDTO updatePlayer(String id, PlayerDTO playerDTO);

    void deletePlayer(String id);

    List<PlayerDTO> searchPlayers(String keyword);

    List<PlayerDTO> filterAndSort(String search, String position, String club, String sortBy, String order);

    List<String> getDistinctPositions();

    List<String> getDistinctClubs();
}
