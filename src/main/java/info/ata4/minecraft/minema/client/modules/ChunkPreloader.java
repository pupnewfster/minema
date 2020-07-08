package info.ata4.minecraft.minema.client.modules;

import info.ata4.minecraft.minema.Minema;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.ChunkRender;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.CompiledChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkPreloader extends CaptureModule {

	@SubscribeEvent
	public void onTick(RenderTickEvent evt) {
		if (evt.phase != Phase.START) {
			return;
		}

		ChunkRenderDispatcher renderDispatcher = MC.worldRenderer.renderDispatcher;
         // 250ms timeout
        // TODO: verify no timeout
        renderDispatcher.runChunkUploads();
		MC.worldRenderer.renderInfos.forEach(info -> {
		    if (info.renderChunk.needsUpdate()) {
                renderDispatcher.rebuildChunk(info.renderChunk);
                info.renderChunk.clearNeedsUpdate();
            }
		});
	}

	@Override
	protected void doEnable() throws Exception {
		if (Minema.instance.getConfig().forcePreloadChunks.get()) {
			ChunkRenderDispatcher chunks = MC.worldRenderer.renderDispatcher;
			ViewFrustum frustum = MC.worldRenderer.viewFrustum;

			for (ChunkRender chunk : frustum.renderChunks) {
				if (chunk.getCompiledChunk() == CompiledChunk.DUMMY) {
					chunks.rebuildChunk(chunk);
				}
			}
		}

		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void doDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@Override
	protected boolean checkEnable() {
		return Minema.instance.getConfig().preloadChunks.get();
	}

}