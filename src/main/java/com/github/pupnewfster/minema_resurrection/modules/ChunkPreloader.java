package com.github.pupnewfster.minema_resurrection.modules;

import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import com.github.pupnewfster.minema_resurrection.modules.CaptureModule.EventBasedCaptureModule;
import net.minecraft.client.renderer.LevelRenderer.RenderChunkInfo;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.CompiledChunk;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkPreloader extends EventBasedCaptureModule {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTick(RenderTickEvent evt) {
        if (evt.phase == Phase.START) {
            ChunkRenderDispatcher renderDispatcher = minecraft.levelRenderer.chunkRenderDispatcher;
            if (renderDispatcher != null) {
                // 250ms timeout
                // TODO: verify no timeout
                renderDispatcher.uploadAllPendingUploads();
                RenderRegionCache renderRegionCache = new RenderRegionCache();
                boolean rebuilt = false;
                for (RenderChunkInfo info : minecraft.levelRenderer.renderChunksInFrustum) {
                    if (info.chunk.isDirty()) {
                        renderDispatcher.rebuildChunkSync(info.chunk, renderRegionCache);
                        info.chunk.setNotDirty();
                        rebuilt = true;
                    }
                }
                if (rebuilt) {
                    renderDispatcher.uploadAllPendingUploads();
                }
            }
        }
    }

    @Override
    protected void doEnable() throws Exception {
        if (MinemaResurrection.instance.getConfig().forcePreloadChunks.get()) {
            ChunkRenderDispatcher chunks = minecraft.levelRenderer.chunkRenderDispatcher;
            ViewArea frustum = minecraft.levelRenderer.viewArea;
            if (chunks != null && frustum != null) {
                frustum.repositionCamera(minecraft.player.getX(), minecraft.player.getZ());
                RenderRegionCache renderRegionCache = new RenderRegionCache();
                boolean rebuilt = false;
                for (RenderChunk chunk : frustum.chunks) {
                    if (chunk.getCompiledChunk() == CompiledChunk.UNCOMPILED) {
                        chunks.rebuildChunkSync(chunk, renderRegionCache);
                        rebuilt = true;
                    }
                }
                if (rebuilt) {
                    chunks.uploadAllPendingUploads();
                }
            }
        }
        super.doEnable();
    }

    @Override
    protected boolean checkEnable() {
        return MinemaResurrection.instance.getConfig().preloadChunks.get();
    }
}