package com.goat.futscout.Service;

import com.goat.futscout.DTO.PlayerDTO;
import com.goat.futscout.Model.Player;
import com.goat.futscout.Repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final io.micrometer.core.instrument.MeterRegistry meterRegistry;

    @Override
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        if (playerDTO.getAge() < 14 || playerDTO.getAge() > 50) {
            throw new IllegalArgumentException("Age must be between 14 and 50.");
        }

        Player player = mapToEntity(playerDTO);
        Player savedPlayer = playerRepository.save(player);
        return mapToDTO(savedPlayer);
    }

    @Override
    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlayerDTO getPlayerById(String id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
        return mapToDTO(player);
    }

    @Override
    public PlayerDTO updatePlayer(String id, PlayerDTO playerDTO) {
        Player existingPlayer = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));

        existingPlayer.setName(playerDTO.getName());
        existingPlayer.setClub(playerDTO.getClub());
        existingPlayer.setPosition(playerDTO.getPosition());
        existingPlayer.setNationality(playerDTO.getNationality());
        existingPlayer.setAge(playerDTO.getAge());
        existingPlayer.setGoals(playerDTO.getGoals());
        existingPlayer.setAssists(playerDTO.getAssists());
        existingPlayer.setRating(playerDTO.getRating());
        existingPlayer.setMarketValue(playerDTO.getMarketValue());

        Player updatedPlayer = playerRepository.save(existingPlayer);
        meterRegistry.counter("service.players.updated.count").increment();
        return mapToDTO(updatedPlayer);
    }

    @Override
    public void deletePlayer(String id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
        playerRepository.delete(player);
    }

    @Override
    public List<PlayerDTO> searchPlayers(String keyword) {
        return playerRepository.findByNameContainingIgnoreCaseOrClubContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlayerDTO> filterAndSort(String search, String position, String club, String sortBy, String order) {
        List<Player> players = playerRepository.findAll();

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            players = players.stream()
                    .filter(p -> p.getName().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        if (position != null && !position.isBlank() && !position.equalsIgnoreCase("all")) {
            players = players.stream()
                    .filter(p -> p.getPosition().equalsIgnoreCase(position))
                    .collect(Collectors.toList());
        }

        if (club != null && !club.isBlank() && !club.equalsIgnoreCase("all")) {
            players = players.stream()
                    .filter(p -> p.getClub().equalsIgnoreCase(club))
                    .collect(Collectors.toList());
        }

        Comparator<Player> comparator = switch (sortBy == null ? "" : sortBy) {
            case "goals" -> Comparator.comparingInt(Player::getGoals);
            case "assists" -> Comparator.comparingInt(Player::getAssists);
            case "age" -> Comparator.comparingInt(Player::getAge);
            case "marketValue" -> Comparator.comparingDouble(Player::getMarketValue);
            case "name" -> Comparator.comparing(p -> p.getName().toLowerCase());
            default -> Comparator.comparingDouble(Player::getRating);
        };

        players = "asc".equalsIgnoreCase(order)
                ? players.stream().sorted(comparator).collect(Collectors.toList())
                : players.stream().sorted(comparator.reversed()).collect(Collectors.toList());

        return players.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<String> getDistinctPositions() {
        return playerRepository.findAll().stream()
                .map(Player::getPosition)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getDistinctClubs() {
        return playerRepository.findAll().stream()
                .map(Player::getClub)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private Player mapToEntity(PlayerDTO dto) {
        Player player = new Player();
        player.setId(dto.getId());
        player.setName(dto.getName());
        player.setClub(dto.getClub());
        player.setPosition(dto.getPosition());
        player.setNationality(dto.getNationality());
        player.setAge(dto.getAge());
        player.setGoals(dto.getGoals());
        player.setAssists(dto.getAssists());
        player.setRating(dto.getRating());
        player.setMarketValue(dto.getMarketValue());
        return player;
    }

    private PlayerDTO mapToDTO(Player entity) {
        return new PlayerDTO(
                entity.getId(),
                entity.getName(),
                entity.getClub(),
                entity.getPosition(),
                entity.getNationality(),
                entity.getAge(),
                entity.getGoals(),
                entity.getAssists(),
                entity.getRating(),
                entity.getMarketValue()
        );
    }
}
