package com.github.pupnewfster.minema_resurrection.cam.path;

import com.github.pupnewfster.minema_resurrection.CaptureSession;
import com.github.pupnewfster.minema_resurrection.util.CamUtils;
import com.github.pupnewfster.minema_resurrection.cam.interpolation.CubicInterpolator;
import com.github.pupnewfster.minema_resurrection.cam.interpolation.IAdditionalAngleInterpolator;
import com.github.pupnewfster.minema_resurrection.cam.interpolation.IPolarCoordinatesInterpolator;
import com.github.pupnewfster.minema_resurrection.cam.interpolation.IPositionInterpolator;
import com.github.pupnewfster.minema_resurrection.cam.interpolation.Interpolator;
import com.github.pupnewfster.minema_resurrection.cam.interpolation.LinearInterpolator;
import com.github.pupnewfster.minema_resurrection.cam.interpolation.TargetInterpolator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PathHandler {

    private PathHandler() {
    }

    private static final List<Position> points = new ArrayList<>();
    private static final List<IPathChangeListener> listeners = new ArrayList<>(1);
    private static Vec3 target;
    @Nullable
    private static ActivePath activePath = null;
    private static boolean recording;
    private static boolean preview = true;

    // Additional path properties

    public static void setTarget(Vec3 target) {
        PathHandler.target = target;
    }

    public static void removeTarget() {
        PathHandler.target = null;
    }

    public static boolean hasTarget() {
        return target != null;
    }

    public static void switchPreview() {
        preview = !preview;
    }

    public static boolean showPreview() {
        return preview && activePath == null;
    }

    // End of path properties

    // Travel control

    public static void startTravelling(Player player, long frames, boolean record) {
        Position[] pathCopy = getWaypoints();
        boolean cmovLinear = pathCopy.length == 2;
        long iterations = frames * CamUtils.renderPhases;

        IPositionInterpolator a = cmovLinear ? LinearInterpolator.instance : CubicInterpolator.instance;

        IPolarCoordinatesInterpolator b = target == null ? cmovLinear ? LinearInterpolator.instance : CubicInterpolator.instance : new TargetInterpolator(target);

        IAdditionalAngleInterpolator c = cmovLinear ? LinearInterpolator.instance : CubicInterpolator.instance;

        setActivePath(new ActiveInterpolatorPath(player, new Interpolator(pathCopy, a, b, c), iterations), record);
    }

    private static void setActivePath(ActivePath path, boolean record) {
        if (recording) {
            //Stop the current recording if there was one
            recording = false;
            CaptureSession.singleton.stopCapture();
        }
        activePath = path;
        if (record) {
            //Start a recording if we are meant to record a given path
            recording = true;
            CaptureSession.singleton.startCapture();
        }
    }

    public static void stopTravelling() {
        setActivePath(null, false);
    }

    public static boolean isTravelling() {
        return activePath != null;
    }

    // End of travel control

    // Auxiliary methods

    public static void tick() {
        if (isTravelling()) {
            activePath.tick();
        }
    }

    public static void addPathChangeListener(IPathChangeListener listener) {
        listeners.add(listener);
    }

    private static void pushChange() {
        for (IPathChangeListener o : listeners) {
            o.onPathChange();
        }
    }

    private static boolean isInBounds(int index) {
        return index >= 0 && index < points.size();
    }

    // End of auxiliary methods

    // Waypoints

    public static void setWaypoints(List<Position> points) {
        PathHandler.points.clear();
        PathHandler.points.addAll(points);
        pushChange();
    }

    public static Position[] getWaypoints() {
        return points.toArray(new Position[0]);
    }

    public static int clearWaypoints() {
        int size = points.size();
        points.clear();
        pushChange();
        return size;
    }

    public static void addWaypoint(Position pos) {
        points.add(pos);
        pushChange();
    }

    public static Position getWaypoint(int index) {
        return isInBounds(index) ? points.get(index) : null;
    }

    public static boolean removeLastWaypoint() {
        return remove(points.size() - 1);
    }

    public static boolean remove(int index) {
        return modify(index, points::remove);
    }

    public static boolean insert(Position position, int index) {
        return modify(index, i -> points.add(i, position));
    }

    public static boolean replace(Position position, int index) {
        return modify(index, i -> points.set(i, position));
    }

    private static boolean modify(int index, IntConsumer modifier) {
        if (isInBounds(index)) {
            modifier.accept(index);
            pushChange();
            return true;
        }
        return false;
    }

    public static int getWaypointSize() {
        return points.size();
    }

    // End of waypoints
}