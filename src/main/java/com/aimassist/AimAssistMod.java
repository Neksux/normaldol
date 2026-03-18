package com.aimassist;

import com.aimassist.config.AimAssistConfig;
import com.aimassist.util.AimAssistHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimAssistMod implements ClientModInitializer {
    public static final String MOD_ID = "aimassist";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static AimAssistConfig config;
    private static boolean enabled = false;
    
    // Key bindings
    private static KeyBinding toggleKey;
    private static KeyBinding configKey;
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Aim Assist Mod");
        
        // Load configuration
        config = AimAssistConfig.load();
        
        // Register key bindings
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aimassist.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.aimassist"
        ));
        
        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aimassist.config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.aimassist"
        ));
        
        // Register tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            
            // Toggle key
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                String status = enabled ? "§aEnabled" : "§cDisabled";
                if (client.player != null) {
                    client.player.sendMessage(
                        net.minecraft.text.Text.literal("§6[Aim Assist] " + status),
                        true
                    );
                }
                LOGGER.info("Aim Assist toggled: {}", enabled);
            }
            
            // Config key
            while (configKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new com.aimassist.config.ConfigScreen(null));
                }
            }
            
            // Apply aim assist
            if (enabled && config.isEnabled()) {
                AimAssistHelper.applyAimAssist(client, config);
            }
        });
        
        LOGGER.info("Aim Assist Mod initialized successfully!");
    }
    
    public static AimAssistConfig getConfig() {
        return config;
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static void setEnabled(boolean value) {
        enabled = value;
    }
}
