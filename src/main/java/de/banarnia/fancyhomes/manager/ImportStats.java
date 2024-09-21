package de.banarnia.fancyhomes.manager;

import java.util.HashMap;
import java.util.UUID;

public class ImportStats {

    private int overallSuccessfulImports, overallFailedImports;

    private final HashMap<UUID, Integer> successfulImports = new HashMap<>();
    private final HashMap<UUID, Integer> failedImports = new HashMap<>();

    private boolean error;
    private String errorMessage;

    public void addSuccessfulImport(UUID playerId) {
        successfulImports.put(playerId, successfulImports.getOrDefault(playerId, 0) + 1);
        overallSuccessfulImports++;
    }

    public void addFailedImport(UUID playerId) {
        failedImports.put(playerId, failedImports.getOrDefault(playerId, 0) + 1);
        overallFailedImports++;
    }

    public int getOverallFailedImports() {
        return overallFailedImports;
    }

    public int getOverallSuccessfulImports() {
        return overallSuccessfulImports;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
