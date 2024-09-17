package de.banarnia.fancyhomes.manager;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

public class ImportStats {

    @Getter
    private int overallSuccessfulImports, overallFailedImports;

    private final HashMap<UUID, Integer> successfulImports = new HashMap<>();
    private final HashMap<UUID, Integer> failedImports = new HashMap<>();

    @Getter
    @Setter
    private boolean error;
    @Getter
    @Setter
    private String errorMessage;

    public void addSuccessfulImport(UUID playerId) {
        successfulImports.put(playerId, successfulImports.getOrDefault(playerId, 0) + 1);
        overallSuccessfulImports++;
    }

    public void addFailedImport(UUID playerId) {
        failedImports.put(playerId, failedImports.getOrDefault(playerId, 0) + 1);
        overallFailedImports++;
    }

}
