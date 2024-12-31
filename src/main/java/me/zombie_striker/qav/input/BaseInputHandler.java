package me.zombie_striker.qav.input;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.api.events.PlayerExitQAVehicleEvent;
import me.zombie_striker.qav.util.VehicleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class BaseInputHandler extends PacketAdapter {

    public BaseInputHandler() {
        super(QualityArmoryVehicles.getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE);
    }

    @Override
    public void onPacketReceiving(final PacketEvent event) {
        final Player player = event.getPlayer();
        try {
            event.getPlayer().getVehicle();
        } catch (UnsupportedOperationException e) {
            Main.DEBUG("The method getVehicle is not supported for temporary players.");
            return;
        }

        if (player.getVehicle() == null) return;

        VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(player.getVehicle());
        if (ve == null)
            return;

        if (!ve.getDriverSeat().equals(player.getVehicle())) return;

        PacketContainer packet = event.getPacket();
        onInputReceived(player, packet);
    }

    public abstract void onInputReceived(Player player, PacketContainer packet);

    public void handleInput(VehicleEntity ve, Player player, boolean forward, boolean backward, boolean left, boolean right, boolean space, boolean shift) {
        if (right)
            ve.getType().handleTurnLeft(ve, player);
        if (left)
            ve.getType().handleTurnRight(ve, player);
        if (backward)
            ve.getType().handleSpeedDecrease(ve, player);
        if (forward)
            ve.getType().handleSpeedIncrease(ve, player);
        if (space)
            ve.getType().handleSpace(ve, player);
        if (shift) {
            PlayerExitQAVehicleEvent event = new PlayerExitQAVehicleEvent(ve, player);
            Bukkit.getPluginManager().callEvent(event);

            if (Main.antiCheatHook) {
                Location location = player.getVehicle().getLocation();
                player.teleport(location);
            }

            if (Main.removeVehicleOnDismount) {
                VehicleUtils.callback(ve, player, "Dismount");
            }
        }
    }
}
