package com.github.pupnewfster.minema_resurrection;

import com.github.pupnewfster.minema_resurrection.cam.CameraRoll;
import com.github.pupnewfster.minema_resurrection.cam.DynamicFOV;
import com.github.pupnewfster.minema_resurrection.cam.interpolation.CubicInterpolator;
import com.github.pupnewfster.minema_resurrection.cam.interpolation.IAdditionalAngleInterpolator;
import com.github.pupnewfster.minema_resurrection.cam.interpolation.IPolarCoordinatesInterpolator;
import com.github.pupnewfster.minema_resurrection.cam.interpolation.Interpolator;
import com.github.pupnewfster.minema_resurrection.cam.path.IPathChangeListener;
import com.github.pupnewfster.minema_resurrection.cam.path.PathHandler;
import com.github.pupnewfster.minema_resurrection.cam.path.Position;
import com.github.pupnewfster.minema_resurrection.command.CommandMinema;
import com.github.pupnewfster.minema_resurrection.util.CamUtils;
import com.github.pupnewfster.minema_resurrection.util.Translations;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

public final class EventListener implements IPathChangeListener {

    public static final EventListener instance = new EventListener();
    /**
     * Describes how many lines per block intersection should be drawn
     */
    private static final double previewFineness = 2.5;

    private Position[] previewPoints;

    /**
     * Use {@link EventListener#instance}
     */
    private EventListener() {
        this.previewPoints = null;
        PathHandler.addPathChangeListener(this);
    }

    @Override
    public void onPathChange() {
        if (PathHandler.getWaypointSize() > 1) {
            Position[] path = PathHandler.getWaypoints();
            Interpolator interpolater = new Interpolator(path, CubicInterpolator.instance, IPolarCoordinatesInterpolator.dummy, IAdditionalAngleInterpolator.dummy);

            double distances = 0;

            Position prev = path[0];

            // The use of direct distances instead of the actual interpolated
            // slope means that there will always be less drawn lines per block,
            // however this is a good enough approximation

            for (int i = 1; i < path.length; i++) {
                Position next = path[i];
                distances += prev.distanceTo(next);
                prev = next;
            }

            int iterations = (int) (distances * previewFineness + 0.5);
            // Snap to next mod 2
            iterations += iterations & 1;

            this.previewPoints = new Position[iterations];
            for (int i = 0; i < iterations; i++) {
                this.previewPoints[i] = interpolater.getPoint((double) i / (iterations - 1));
            }
        } else {
            this.previewPoints = null;
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterClientCommandsEvent e) {
        e.getDispatcher().register(CommandMinema.register());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        if (consumeClick(event, MinemaKeyBindings.KEY_CAPTURE)) {
            if (!CaptureSession.singleton.startCapture()) {
                CaptureSession.singleton.stopCapture();
            }
        }
        if (!PathHandler.isTravelling()) {
            if (consumeClick(event, MinemaKeyBindings.KEY_POINT)) {
                Player player = Minecraft.getInstance().player;
                Position playerPos = CamUtils.getPosition(player);
                PathHandler.addWaypoint(playerPos);
                player.sendSystemMessage(Translations.COMMAND_PATH_ADD.translateColored(ChatFormatting.WHITE, PathHandler.getWaypointSize()));
            }
            if (consumeClick(event, MinemaKeyBindings.KEY_RESET_CAMERA)) {
                CameraRoll.reset();
            }
            if (consumeClick(event, MinemaKeyBindings.KEY_RESET_FOV)) {
                DynamicFOV.reset();
            }
        }
    }

    private boolean consumeClick(InputEvent.Key event, KeyMapping keyBinding) {
        return keyBinding.consumeClick() || keyBinding.isDown() && event.getKey() == keyBinding.getKey().getValue() && event.getAction() == InputConstants.PRESS;
    }

    @SubscribeEvent
    public void onRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || this.previewPoints == null || !PathHandler.showPreview()) {
            return;
        }
        Camera camera = event.getCamera();
        Vec3 viewPosition = camera.getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-viewPosition.x, -viewPosition.y, -viewPosition.z);
        Matrix4f pose = poseStack.last().pose();
        ShaderInstance previousShader = RenderSystem.getShader();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < (this.previewPoints.length - 1); i++) {
            Position prev = this.previewPoints[i];
            Position next = this.previewPoints[i + 1];
            bufferBuilder.vertex(pose, (float) prev.x, (float) prev.y, (float) prev.z).color(1F, 0.2F, 0.2F, 1F).endVertex();
            bufferBuilder.vertex(pose, (float) next.x, (float) next.y, (float) next.z).color(1F, 0.2F, 0.2F, 1F).endVertex();
        }
        tesselator.end();
        poseStack.popPose();
        //Set shader back to the one it was
        RenderSystem.setShader(() -> previousShader);
    }

    @SubscribeEvent
    public void onRender(RenderTickEvent e) {
        PathHandler.tick();
        if (PathHandler.isTravelling()) {
            return;
        }
        if (MinemaKeyBindings.KEY_CLOCKWISE_CAMERA.isDown()) {
            CameraRoll.rotateClockWise();
        }
        if (MinemaKeyBindings.KEY_COUNTER_CLOCKWISE_CAMERA.isDown()) {
            CameraRoll.rotateCounterClockWise();
        }
        if (MinemaKeyBindings.KEY_INCREASE_FOV.isDown()) {
            DynamicFOV.increase();
        }
        if (MinemaKeyBindings.KEY_DECREASE_FOV.isDown()) {
            DynamicFOV.decrease();
        }
    }

    @SubscribeEvent
    public void fov(ViewportEvent.ComputeFov event) {
        //Only adjust the Fov if the event used the fov setting from the options (when it is false
        // it expects to be a static number such as for rendering the hand)
        if (event.usedConfiguredFov()) {
            float fov = DynamicFOV.get();
            //Apply fov modifiers if enabled in config, and we aren't travelling a path, or we are meant to apply them even when travelling a path
            if (MinemaResurrection.instance.getConfig().applyFOVModifiers.get() &&
                (!PathHandler.isTravelling() || MinemaResurrection.instance.getConfig().applyFOVModifiersPath.get())) {
                if (fov == DynamicFOV.getRaw()) {
                    //Skip any changes if we are at the same value as vanilla would be
                    return;
                }
                //Copy based off of GameRenderer#getFov
                double partialTick = event.getPartialTick();
                Minecraft minecraft = Minecraft.getInstance();
                //Apply entity's Fov Modifier
                fov *= (double) Mth.lerp(partialTick, minecraft.gameRenderer.oldFov, minecraft.gameRenderer.fov);
                //Apply the Fov Modifier for if the entity is dead or if they are in a specific type of fluid
                Camera camera = event.getCamera();
                if (camera.getEntity() instanceof LivingEntity entity && entity.isDeadOrDying()) {
                    float f = Math.min((float) entity.deathTime + (float) partialTick, 20.0F);
                    fov /= (double) ((1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F);
                }
                FogType fogtype = camera.getFluidInCamera();
                if (fogtype == FogType.LAVA || fogtype == FogType.WATER) {
                    fov *= Mth.lerp(minecraft.options.fovEffectScale().get(), 1.0D, 0.85714287F);
                }
            }
            event.setFOV(fov);
        }
    }

    @SubscribeEvent
    public void onOrientCamera(ViewportEvent.ComputeCameraAngles e) {
        // Do not explicitly set roll to 0 (when the player is hurt for example
        // minecraft uses roll)
        if (CameraRoll.roll != 0) {
            e.setRoll(CameraRoll.roll);
        }
    }
}