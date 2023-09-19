package de.banarnia.fancyhomes;

import de.banarnia.fancyhomes.api.config.Config;

public class HomeConfig {

    private Config config;

    public HomeConfig(Config config) {
        this.config = config;
    }

    public double getWarmupTime() {
        return config.getDouble("warmup-time", 0);
    }

    public void setWarmupTime(double warmupTime) {
        config.set("warmup-time", warmupTime);
        config.save();
    }

    public double getCooldownTime() {
        return config.getDouble("cooldown-time", 0);
    }

    public void setCooldownTime(double cooldownTime) {
        config.set("cooldown-time", cooldownTime);
        config.save();
    }

    public int getMaxHomes() {
        return config.getInt("max-homes", 1);
    }

    public void setMaxHomes(int maxHomes) {
        config.set("max-homes", maxHomes);
        config.save();
    }

}
