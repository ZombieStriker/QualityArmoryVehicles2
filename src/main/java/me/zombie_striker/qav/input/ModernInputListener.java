package me.zombie_striker.qav.input;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.Bukkit;
import me.zombie_striker.qav.util.SchedulerUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModernInputListener extends BaseInputHandler implements Listener, Runnable {
    private static WrappedTask task;
    private static ModernInputListener instance;
    private final Map<Player, InternalStructure> lastInput = new ConcurrentHashMap<>();

    @Override
    public void onInputReceived(Player player, PacketContainer packet) {
        InternalStructure input = packet.getStructures().read(0);
        lastInput.put(player, input);
    }

    public void register() {
        instance = this;
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
        Bukkit.getPluginManager().registerEvents(this, QualityArmoryVehicles.getPlugin());
        task = Main.foliaLib.getScheduler().runTimer(this, 1, 1);
    }

    @Override
    public void run() {
        if (SchedulerUtil.isFolia()) {
            // On Folia the input is handled by the region owning the vehicle, see handleDriverInput
            lastInput.keySet().removeIf(player -> player == null || !player.isOnline());
            return;
        }

        for (Player player : new ArrayList<>(lastInput.keySet())) {
            process(player);
        }
    }

    /**
     * Handles the input of the players sitting in the given vehicle. Used on Folia, called by the region ticking the vehicle.
     */
    public static void handleDriverInput(VehicleEntity ve) {
        if (instance == null || ve.getDriverSeat() == null) return;

        handleSeatInput(ve.getDriverSeat());
        for (Entity seat : new ArrayList<>(ve.getPassagerSeats())) {
            handleSeatInput(seat);
        }
    }

    private static void handleSeatInput(Entity seat) {
        Entity passenger = seat.getPassenger();
        if (passenger instanceof Player && instance.lastInput.containsKey(passenger))
            instance.process((Player) passenger);
    }

    private void process(Player player) {
        InternalStructure input = lastInput.get(player);
        if (input == null) return;

        if (player == null || !player.isOnline() || player.getVehicle() == null) {
            lastInput.remove(player);
            return;
        }

        VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(player.getVehicle());
        if (ve == null) {
            lastInput.remove(player);
            return;
        }

        boolean left = input.getBooleans().read(2);
        boolean right = input.getBooleans().read(3);
        boolean forward = input.getBooleans().read(0);
        boolean backward = input.getBooleans().read(1);
        boolean space = input.getBooleans().read(4);
        boolean shift = input.getBooleans().read(5);

        if (!left && !right && !forward && !backward && !space && !shift) {
            lastInput.remove(player);
            return;
        }

        handleInput(ve, player, forward, backward, left, right, space, shift);
    }

    public static void unregister() {
        instance = null;
        if (task == null || task.isCancelled()) return;
        task.cancel();
    }
}
