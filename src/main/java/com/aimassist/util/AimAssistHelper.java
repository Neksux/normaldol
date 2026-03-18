package com.aimassist.util;

import com.aimassist.config.AimAssistConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AimAssistHelper {
    
    public static void applyAimAssist(MinecraftClient client, AimAssistConfig config) {
        if (client.player == null || client.world == null) return;
        
        // Check if holding weapon
        if (config.isAimWhileHoldingWeapon() && !isHoldingWeapon(client.player)) {
            return;
        }
        
        // Find best target
        LivingEntity target = findBestTarget(client, config);
        if (target == null) return;
        
        // Calculate aim angles
        Vec3d targetPos = getAimPosition(target, config);
        Vec3d playerPos = client.player.getCameraPosVec(1.0f);
        
        // Predict movement
        if (config.isPredictMovement()) {
            targetPos = predictTargetPosition(target, playerPos, config);
        }
        
        Vec3d direction = targetPos.subtract(playerPos).normalize();
        
        double deltaX = direction.x;
        double deltaY = direction.y;
        double deltaZ = direction.z;
        
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        
        float targetYaw = (float) (MathHelper.atan2(deltaZ, deltaX) * (180.0 / Math.PI)) - 90.0f;
        float targetPitch = (float) (-(MathHelper.atan2(deltaY, horizontalDistance) * (180.0 / Math.PI)));
        
        // Apply offsets
        targetYaw += (float) config.getHorizontalOffset();
        targetPitch += (float) config.getVerticalOffset();
        
        float currentYaw = client.player.getYaw();
        float currentPitch = client.player.getPitch();
        
        // Smooth aiming
        if (config.isSmoothAiming()) {
            float aimSpeed = (float) config.getAimSpeed();
            
            float yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);
            float pitchDiff = targetPitch - currentPitch;
            
            float newYaw = currentYaw + yawDiff * aimSpeed;
            float newPitch = currentPitch + pitchDiff * aimSpeed;
            
            client.player.setYaw(newYaw);
            client.player.setPitch(MathHelper.clamp(newPitch, -90.0f, 90.0f));
        } else {
            client.player.setYaw(targetYaw);
            client.player.setPitch(MathHelper.clamp(targetPitch, -90.0f, 90.0f));
        }
    }
    
    private static LivingEntity findBestTarget(MinecraftClient client, AimAssistConfig config) {
        if (client.player == null || client.world == null) return null;
        
        Vec3d playerPos = client.player.getCameraPosVec(1.0f);
        double maxDistance = config.getAimDistance();
        
        Box searchBox = new Box(
            playerPos.x - maxDistance,
            playerPos.y - maxDistance,
            playerPos.z - maxDistance,
            playerPos.x + maxDistance,
            playerPos.y + maxDistance,
            playerPos.z + maxDistance
        );
        
        List<LivingEntity> entities = client.world.getEntitiesByClass(
            LivingEntity.class,
            searchBox,
            entity -> isValidTarget(entity, client.player, config)
        );
        
        return entities.stream()
            .filter(entity -> {
                double distance = entity.distanceTo(client.player);
                if (distance > maxDistance) return false;
                
                // Check FOV
                Vec3d toEntity = entity.getPos().subtract(playerPos).normalize();
                Vec3d lookVec = client.player.getRotationVec(1.0f);
                double angle = Math.acos(toEntity.dotProduct(lookVec)) * (180.0 / Math.PI);
                
                if (angle > config.getAimFOV()) return false;
                
                // Check line of sight
                if (config.isRequireLineOfSight() && !hasLineOfSight(client, entity)) {
                    return false;
                }
                
                return true;
            })
            .min(Comparator.comparingDouble(entity -> entity.distanceTo(client.player)))
            .orElse(null);
    }
    
    private static boolean isValidTarget(LivingEntity entity, PlayerEntity player, AimAssistConfig config) {
        if (entity == player) return false;
        if (!entity.isAlive()) return false;
        if (entity.isInvisible() && config.isIgnoreInvisible()) return false;
        
        // Check entity type
        if (entity instanceof PlayerEntity) {
            if (!config.isTargetPlayers()) return false;
            
            // Check teammates
            if (config.isIgnoreTeammates() && entity.isTeammate(player)) {
                return false;
            }
        } else if (entity instanceof HostileEntity) {
            if (!config.isTargetHostile()) return false;
        } else if (entity instanceof PassiveEntity) {
            if (!config.isTargetPassive()) return false;
        } else {
            if (!config.isTargetMobs()) return false;
        }
        
        return true;
    }
    
    private static Vec3d getAimPosition(LivingEntity target, AimAssistConfig config) {
        Vec3d pos = target.getPos();
        
        if (config.isAimAtHead()) {
            // Aim at head/eye level
            return pos.add(0, target.getStandingEyeHeight(), 0);
        } else {
            // Aim at center of mass
            return pos.add(0, target.getHeight() / 2.0, 0);
        }
    }
    
    private static Vec3d predictTargetPosition(LivingEntity target, Vec3d playerPos, AimAssistConfig config) {
        Vec3d targetPos = getAimPosition(target, config);
        Vec3d velocity = target.getVelocity();
        
        // Simple prediction based on current velocity
        double distance = targetPos.distanceTo(playerPos);
        double timeToReach = distance / 20.0; // Assume projectile speed
        
        return targetPos.add(velocity.multiply(timeToReach));
    }
    
    private static boolean hasLineOfSight(MinecraftClient client, Entity target) {
        if (client.player == null || client.world == null) return false;
        
        Vec3d start = client.player.getCameraPosVec(1.0f);
        Vec3d end = target.getPos().add(0, target.getStandingEyeHeight() / 2.0, 0);
        
        RaycastContext context = new RaycastContext(
            start,
            end,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            client.player
        );
        
        HitResult result = client.world.raycast(context);
        return result.getType() == HitResult.Type.MISS;
    }
    
    private static boolean isHoldingWeapon(PlayerEntity player) {
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();
        
        return isWeapon(mainHand) || isWeapon(offHand);
    }
    
    private static boolean isWeapon(ItemStack stack) {
        return stack.getItem() instanceof SwordItem ||
               stack.getItem() instanceof RangedWeaponItem;
    }
}
