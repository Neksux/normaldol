package com.aimassist.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AimAssistConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(
        FabricLoader.getInstance().getConfigDir().toFile(),
        "aimassist.json"
    );
    
    // General settings
    private boolean enabled = true;
    
    // Aim assist settings
    private double aimSpeed = 0.5; // 0.0 - 1.0
    private double aimDistance = 16.0; // Maximum distance in blocks
    private double aimFOV = 45.0; // Field of view for aim assist
    private boolean smoothAiming = true;
    private boolean predictMovement = true;
    
    // Target priorities
    private boolean targetPlayers = false;
    private boolean targetMobs = true;
    private boolean targetAnimals = false;
    private boolean targetHostile = true;
    private boolean targetPassive = false;
    
    // Advanced settings
    private boolean ignoreTeammates = true;
    private boolean ignoreInvisible = false;
    private boolean aimAtHead = true;
    private double verticalOffset = 0.0; // -1.0 to 1.0
    private double horizontalOffset = 0.0; // -1.0 to 1.0
    
    // Trigger settings
    private boolean autoAim = false;
    private boolean aimWhileHoldingWeapon = true;
    private boolean requireLineOfSight = true;
    
    public static AimAssistConfig load() {
        if (!CONFIG_FILE.exists()) {
            AimAssistConfig config = new AimAssistConfig();
            config.save();
            return config;
        }
        
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            return GSON.fromJson(reader, AimAssistConfig.class);
        } catch (IOException e) {
            return new AimAssistConfig();
        }
    }
    
    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Getters and setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public double getAimSpeed() { return aimSpeed; }
    public void setAimSpeed(double aimSpeed) { this.aimSpeed = Math.max(0.0, Math.min(1.0, aimSpeed)); }
    
    public double getAimDistance() { return aimDistance; }
    public void setAimDistance(double aimDistance) { this.aimDistance = Math.max(1.0, Math.min(100.0, aimDistance)); }
    
    public double getAimFOV() { return aimFOV; }
    public void setAimFOV(double aimFOV) { this.aimFOV = Math.max(1.0, Math.min(180.0, aimFOV)); }
    
    public boolean isSmoothAiming() { return smoothAiming; }
    public void setSmoothAiming(boolean smoothAiming) { this.smoothAiming = smoothAiming; }
    
    public boolean isPredictMovement() { return predictMovement; }
    public void setPredictMovement(boolean predictMovement) { this.predictMovement = predictMovement; }
    
    public boolean isTargetPlayers() { return targetPlayers; }
    public void setTargetPlayers(boolean targetPlayers) { this.targetPlayers = targetPlayers; }
    
    public boolean isTargetMobs() { return targetMobs; }
    public void setTargetMobs(boolean targetMobs) { this.targetMobs = targetMobs; }
    
    public boolean isTargetAnimals() { return targetAnimals; }
    public void setTargetAnimals(boolean targetAnimals) { this.targetAnimals = targetAnimals; }
    
    public boolean isTargetHostile() { return targetHostile; }
    public void setTargetHostile(boolean targetHostile) { this.targetHostile = targetHostile; }
    
    public boolean isTargetPassive() { return targetPassive; }
    public void setTargetPassive(boolean targetPassive) { this.targetPassive = targetPassive; }
    
    public boolean isIgnoreTeammates() { return ignoreTeammates; }
    public void setIgnoreTeammates(boolean ignoreTeammates) { this.ignoreTeammates = ignoreTeammates; }
    
    public boolean isIgnoreInvisible() { return ignoreInvisible; }
    public void setIgnoreInvisible(boolean ignoreInvisible) { this.ignoreInvisible = ignoreInvisible; }
    
    public boolean isAimAtHead() { return aimAtHead; }
    public void setAimAtHead(boolean aimAtHead) { this.aimAtHead = aimAtHead; }
    
    public double getVerticalOffset() { return verticalOffset; }
    public void setVerticalOffset(double verticalOffset) { this.verticalOffset = Math.max(-1.0, Math.min(1.0, verticalOffset)); }
    
    public double getHorizontalOffset() { return horizontalOffset; }
    public void setHorizontalOffset(double horizontalOffset) { this.horizontalOffset = Math.max(-1.0, Math.min(1.0, horizontalOffset)); }
    
    public boolean isAutoAim() { return autoAim; }
    public void setAutoAim(boolean autoAim) { this.autoAim = autoAim; }
    
    public boolean isAimWhileHoldingWeapon() { return aimWhileHoldingWeapon; }
    public void setAimWhileHoldingWeapon(boolean aimWhileHoldingWeapon) { this.aimWhileHoldingWeapon = aimWhileHoldingWeapon; }
    
    public boolean isRequireLineOfSight() { return requireLineOfSight; }
    public void setRequireLineOfSight(boolean requireLineOfSight) { this.requireLineOfSight = requireLineOfSight; }
}
