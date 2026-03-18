package com.aimassist.config;

import com.aimassist.AimAssistMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final AimAssistConfig config;
    
    public ConfigScreen(Screen parent) {
        super(Text.literal("Aim Assist Configuration"));
        this.parent = parent;
        this.config = AimAssistMod.getConfig();
    }
    
    @Override
    protected void init() {
        int y = 20;
        int spacing = 25;
        
        // Title
        y += spacing;
        
        // General settings
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Enabled: " + (config.isEnabled() ? "§aON" : "§cOFF")),
            button -> {
                config.setEnabled(!config.isEnabled());
                button.setMessage(Text.literal("Enabled: " + (config.isEnabled() ? "§aON" : "§cOFF")));
            }
        ).dimensions(this.width / 2 - 100, y, 200, 20).build());
        y += spacing;
        
        // Aim Speed Slider
        addDrawableChild(new SliderWidget(this.width / 2 - 100, y, 200, 20, 
            Text.literal("Aim Speed: " + String.format("%.2f", config.getAimSpeed())), 
            config.getAimSpeed()) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Aim Speed: " + String.format("%.2f", value)));
            }
            
            @Override
            protected void applyValue() {
                config.setAimSpeed(value);
            }
        });
        y += spacing;
        
        // Aim Distance Slider
        addDrawableChild(new SliderWidget(this.width / 2 - 100, y, 200, 20, 
            Text.literal("Distance: " + String.format("%.1f", config.getAimDistance())), 
            config.getAimDistance() / 100.0) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Distance: " + String.format("%.1f", value * 100.0)));
            }
            
            @Override
            protected void applyValue() {
                config.setAimDistance(value * 100.0);
            }
        });
        y += spacing;
        
        // FOV Slider
        addDrawableChild(new SliderWidget(this.width / 2 - 100, y, 200, 20, 
            Text.literal("FOV: " + String.format("%.0f°", config.getAimFOV())), 
            config.getAimFOV() / 180.0) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("FOV: " + String.format("%.0f°", value * 180.0)));
            }
            
            @Override
            protected void applyValue() {
                config.setAimFOV(value * 180.0);
            }
        });
        y += spacing;
        
        // Smooth Aiming Toggle
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Smooth Aiming: " + (config.isSmoothAiming() ? "§aON" : "§cOFF")),
            button -> {
                config.setSmoothAiming(!config.isSmoothAiming());
                button.setMessage(Text.literal("Smooth Aiming: " + (config.isSmoothAiming() ? "§aON" : "§cOFF")));
            }
        ).dimensions(this.width / 2 - 100, y, 200, 20).build());
        y += spacing;
        
        // Predict Movement Toggle
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Predict Movement: " + (config.isPredictMovement() ? "§aON" : "§cOFF")),
            button -> {
                config.setPredictMovement(!config.isPredictMovement());
                button.setMessage(Text.literal("Predict Movement: " + (config.isPredictMovement() ? "§aON" : "§cOFF")));
            }
        ).dimensions(this.width / 2 - 100, y, 200, 20).build());
        y += spacing;
        
        // Target Players Toggle
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Target Players: " + (config.isTargetPlayers() ? "§aON" : "§cOFF")),
            button -> {
                config.setTargetPlayers(!config.isTargetPlayers());
                button.setMessage(Text.literal("Target Players: " + (config.isTargetPlayers() ? "§aON" : "§cOFF")));
            }
        ).dimensions(this.width / 2 - 100, y, 200, 20).build());
        y += spacing;
        
        // Target Hostile Toggle
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Target Hostile: " + (config.isTargetHostile() ? "§aON" : "§cOFF")),
            button -> {
                config.setTargetHostile(!config.isTargetHostile());
                button.setMessage(Text.literal("Target Hostile: " + (config.isTargetHostile() ? "§aON" : "§cOFF")));
            }
        ).dimensions(this.width / 2 - 100, y, 200, 20).build());
        y += spacing;
        
        // Aim at Head Toggle
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Aim at Head: " + (config.isAimAtHead() ? "§aON" : "§cOFF")),
            button -> {
                config.setAimAtHead(!config.isAimAtHead());
                button.setMessage(Text.literal("Aim at Head: " + (config.isAimAtHead() ? "§aON" : "§cOFF")));
            }
        ).dimensions(this.width / 2 - 100, y, 200, 20).build());
        y += spacing;
        
        // Done button
        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            config.save();
            this.close();
        }).dimensions(this.width / 2 - 100, this.height - 30, 200, 20).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
