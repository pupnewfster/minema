package info.ata4.minecraft.minema.client.modules;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import info.ata4.minecraft.minema.Minema;
import info.ata4.minecraft.minema.Utils;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.ChunkRender;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.CompiledChunk;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkPreloader extends CaptureModule {

	private Field renderInfosField;
	private Field renderDispatcherField;
	private Field renderChunkField;
	private Field renderViewFrustum;

	@SuppressWarnings("rawtypes")
	@SubscribeEvent
	public void onTick(RenderTickEvent evt) {
		if (evt.phase != Phase.START) {
			return;
		}

		try {
			Iterator iterator = ((List) renderInfosField.get(MC.worldRenderer)).iterator();
			ChunkRenderDispatcher renderDispatcher = (ChunkRenderDispatcher) renderDispatcherField.get(MC.worldRenderer);

			// 250ms timeout
			// TODO: verify no timeout
			renderDispatcher.runChunkUploads();

			while (iterator.hasNext()) {
				Object renderInfo = iterator.next();
				ChunkRender rc = (ChunkRender) renderChunkField.get(renderInfo);

				if (rc.needsUpdate()) {
					renderDispatcher.rebuildChunk(rc);
					rc.clearNeedsUpdate();
				}
			}
		} catch (Exception e) {
			Utils.print("Could not do chunk preloading", TextFormatting.RED);
			Utils.printError(e);
		}
	}

	@Override
	protected void doEnable() throws Exception {
		renderInfosField = WorldRenderer.class.getDeclaredField("field_72755_R");
		renderInfosField.setAccessible(true);
		renderDispatcherField = WorldRenderer.class.getDeclaredField("field_174995_M");
		renderDispatcherField.setAccessible(true);
		renderViewFrustum = WorldRenderer.class.getDeclaredField("field_175008_n");
		renderViewFrustum.setAccessible(true);

		for (Class<?> innerClass : WorldRenderer.class.getDeclaredClasses()) {
			if (innerClass.getName().endsWith("ContainerLocalRenderInformation")) {
				renderChunkField = innerClass.getDeclaredField("field_178036_a");
				renderChunkField.setAccessible(true);
				break;
			}
		}

		if (renderInfosField == null || renderDispatcherField == null || renderChunkField == null)
			return;

		if (Minema.instance.getConfig().forcePreloadChunks.get() && renderViewFrustum != null) {
			try {
				ChunkRenderDispatcher chunks = (ChunkRenderDispatcher) renderDispatcherField.get(MC.worldRenderer);
				ViewFrustum frustum = (ViewFrustum) renderViewFrustum.get(MC.worldRenderer);

				for (ChunkRender chunk : frustum.renderChunks) {
					if (chunk.getCompiledChunk() == CompiledChunk.DUMMY) {
						chunks.rebuildChunk(chunk);
					}
				}
			} catch (Exception e) {
				Utils.print("Could not force preload all chunks", TextFormatting.RED);
				Utils.printError(e);
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