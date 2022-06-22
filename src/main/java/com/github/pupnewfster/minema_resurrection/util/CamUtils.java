package com.github.pupnewfster.minema_resurrection.util;

import com.github.pupnewfster.minema_resurrection.cam.CameraRoll;
import com.github.pupnewfster.minema_resurrection.cam.DynamicFOV;
import com.github.pupnewfster.minema_resurrection.EventListener;
import com.github.pupnewfster.minema_resurrection.cam.path.PathHandler;
import com.github.pupnewfster.minema_resurrection.cam.path.Position;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import org.jetbrains.annotations.Nullable;

public class CamUtils {

    private CamUtils() {
    }

    /**
     * Describes how often {@link PathHandler#tick()} is called per frame
     * <p>
     * calls are made from {@link EventListener#onRender(RenderTickEvent)})
     */
    public static final int renderPhases = TickEvent.Phase.values().length;

    /**
     * Do only call this method if a world is loaded!
     */
    public static Position getPosition(Player player) {
        return new Position(player.getX(), player.getY(), player.getZ(), player.getXRot(), player.getYRot(), CameraRoll.roll, DynamicFOV.get());
    }

    /**
     * @param pos   Position to teleport the player to
     * @param force should be true when teleporting over a presumably large distance
     */
    public static void teleport(@Nullable Player player, Position pos, boolean force) {
        if (player == null) {
            PathHandler.stopTravelling();
        } else {
            if (force) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.hasSingleplayerServer()) {
                    ServerPlayer serverPlayer = minecraft.getSingleplayerServer().getPlayerList().getPlayer(player.getUUID());
                    setPositionProperly(serverPlayer, pos);
                } else {
                    player.sendSystemMessage(Translations.WARN_NO_LOCAL_WORLD_TELEPORT.translateColored(ChatFormatting.RED));
                }
            }

            setPositionProperly(player, pos);
            CameraRoll.roll = pos.roll;
            DynamicFOV.set(pos.fov);
        }
    }

    private static void setPositionProperly(Entity entity, Position pos) {
        // This procedure here is crucial! When not done properly (eg.
        // setPositionAndRotation is not properly) it can lead to
        // spinning camera movement (probably yaw angle which may incorrectly be
        // bounded inside -180 and 180 degrees)
        // FUN FACT: PixelCam had the same issue!
        // FUN FACT 2: setLocationAndAngles solves this but instead results in
        // desync when setting the position of entities both in the client world
        // and server world -> not loading chunks anymore on the client side
        // Workaround: Send a teleport command
        entity.moveTo(pos.x, pos.y, pos.z, pos.yaw, pos.pitch);
        // Prevents inaccurate/wobbly/jerky angle movement (setLocationAndAngles
        // only sets previous values for x,y,z -> partial tick interpolation is
        // still also done for angles by the engine)
        entity.yRotO = pos.yaw;
        entity.xRotO = pos.pitch;
    }
}