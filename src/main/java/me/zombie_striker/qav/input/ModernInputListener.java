package me.zombie_striker.qav.input;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class ModernInputListener extends BaseInputHandler implements Listener, Runnable {
    private static WrappedTask task;
    private final Map<Player, InternalStructure> lastInput = new HashMap<>();

    @Override
    public void onInputReceived(Player player, PacketContainer packet) {
        InternalStructure input = packet.getStructures().read(0);
        lastInput.put(player, input);
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
        Bukkit.getPluginManager().registerEvents(this, QualityArmoryVehicles.getPlugin());
        task = Main.foliaLib.getScheduler().runTimer(this, 0, 1);
    }

    @Override
    public void run() {
        new HashMap<>(lastInput).forEach((player, input) -> {
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
        });
    }

    public static void unregister() {
        if (task == null || task.isCancelled()) return;
        task.cancel();
    }
}
