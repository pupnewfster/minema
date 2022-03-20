/*
 ** 2014 July 29
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.minecraft.minema.client.modules;

import info.ata4.minecraft.minema.Minema;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

/**
 * Tick synchronizer that virtually works like the escapement device of a clock where the "wheel" is the global tick counter and the "anchor" being the local server and
 * client thread.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class TickSynchronizer extends CaptureModule {

    // wait time in nanoseconds before the lock release condition is re-checked
    private static final long WAIT_INTERVAL = TimeUnit.SECONDS.toNanos(1);

    // atomic state objects that are shared between the threads
    private final AtomicBoolean serverReady = new AtomicBoolean();
    private final AtomicBoolean clientReady = new AtomicBoolean();
    private final AtomicInteger serverTick = new AtomicInteger();
    private final AtomicInteger clientTick = new AtomicInteger();

    // concurrency helpers
    private final Lock lock = new ReentrantLock();
    private final Condition serverAhead = this.lock.newCondition();
    private final Condition clientAhead = this.lock.newCondition();

    @SubscribeEvent
    public void onClientTick(ClientTickEvent evt) {
        if (!isEnabled() || evt.phase != Phase.START) {
            return;
        }

        // client is ready now
        if (!clientReady.get()) {
            L.info("Client tick sync ready");
            clientReady.set(true);
            clientTick.set(0);
        }

        // wait for server side
        if (!serverReady.get()) {
            return;
        }

        // don't wait for the server while the game is paused!
        if (MC.isPaused()) {
            return;
        }

        // now sync with the server
        waitFor(evt.side, clientTick, serverTick, clientAhead, serverAhead);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent evt) {
        if (!isEnabled() || evt.phase != Phase.START) {
            return;
        }

        // server is ready now
        if (!serverReady.get()) {
            L.info("Server tick sync ready");
            serverReady.set(true);
            serverTick.set(0);
        }

        // wait for client side
        if (!clientReady.get()) {
            return;
        }

        // now sync with the client
        waitFor(evt.side, serverTick, clientTick, serverAhead, clientAhead);
    }

    private void waitFor(LogicalSide side, AtomicInteger actual, AtomicInteger target, Condition waitCon,
          Condition signalCon) {
        lock.lock();

        try {
            while (target.get() < actual.get()) {
                if (L.isDebugEnabled()) {
                    int behind = actual.get() - target.get();
                    LogicalSide otherSide = side.isClient() ? LogicalSide.SERVER : LogicalSide.CLIENT;
                    L.debug("{} waiting, {} {} ticks behind", side, otherSide, behind);
                }

                waitCon.awaitNanos(WAIT_INTERVAL);

                checkServer();

                // break loop if any side isn't ready or if the sync is disabled
                if (!isEnabled() || !serverReady.get() || !clientReady.get()) {
                    return;
                }
            }

            actual.addAndGet(1);

            if (L.isDebugEnabled()) {
                L.debug("{} tick: {}", side, actual.get());
            }

            signalCon.signal();
        } catch (InterruptedException ex) {
            try {
                disable();
            } catch (Exception ignored) {
            }
        } finally {
            lock.unlock();
        }
    }

    private void checkServer() {
        // reset server status when the server died.
        // server shutdowns may not be noticed by the ServerStopped event while
        // the client is waiting for the server to continue, so do a continuous
        // check instead
        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null || !server.isRunning() || server.isStopped()) {
            serverReady.set(false);
        }
    }

    @Override
    protected void doEnable() throws Exception {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected boolean checkEnable() {
        return Minema.instance.getConfig().syncEngine.get() & MC.hasSingleplayerServer();
    }

    @Override
    protected void doDisable() throws Exception {
        clientReady.set(false);
        serverReady.set(false);
        MinecraftForge.EVENT_BUS.unregister(this);
    }

}
