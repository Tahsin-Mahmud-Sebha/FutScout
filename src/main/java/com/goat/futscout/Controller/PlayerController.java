package com.goat.futscout.Controller;

import com.goat.futscout.DTO.PlayerDTO;
import com.goat.futscout.Service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // Crucial: Allows your frontend to talk to this backend
@RestController
@RequestMapping("/api/players") // Base request mapping
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private final io.micrometer.core.instrument.MeterRegistry meterRegistry;

    // POST endpoint to create a record
    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) {
        PlayerDTO created = playerService.createPlayer(playerDTO);
        meterRegistry.counter("api.players.created.count").increment();
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // GET endpoint to retrieve all records, with optional search / filter / sort
    @GetMapping
    public ResponseEntity<List<PlayerDTO>> getAllPlayers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String club,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order) {
        return ResponseEntity.ok(playerService.filterAndSort(search, position, club, sortBy, order));
    }

    // GET endpoint to retrieve a record by ID
    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable String id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

    // PUT endpoint to update a record
    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable String id, @Valid @RequestBody PlayerDTO playerDTO) {
        return ResponseEntity.ok(playerService.updatePlayer(id, playerDTO));
    }

    // DELETE endpoint to delete a record
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable String id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }

    // Helper endpoints for populating filter dropdowns in the UI
    @GetMapping("/meta/positions")
    public ResponseEntity<List<String>> getPositions() {
        return ResponseEntity.ok(playerService.getDistinctPositions());
    }

    @GetMapping("/meta/clubs")
    public ResponseEntity<List<String>> getClubs() {
        return ResponseEntity.ok(playerService.getDistinctClubs());
    }
}
