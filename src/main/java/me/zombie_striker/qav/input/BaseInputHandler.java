package me.zombie_striker.qav.input;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.attachments.Attachment;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

        // Passengers only control the attachments of their seat (e.g. a turret)
        Integer seat = ve.getSeatIndex(player.getVehicle());
        if (seat == null || (seat != Attachment.DRIVER_SEAT && !ve.getType().hasAttachmentControlledBy(seat))) return;

        PacketContainer packet = event.getPacket();
        onInputReceived(player, packet);
    }

    public abstract void onInputReceived(Player player, PacketContainer packet);

    public void handleInput(VehicleEntity ve, Player player, boolean forward, boolean backward, boolean left, boolean right, boolean space, boolean shift) {
        if (ve == null) return;

        List<VehicleInput> inputs = new ArrayList<>();
        if (forward) inputs.add(VehicleInput.FORWARD);
        if (backward) inputs.add(VehicleInput.BACKWARD);
        if (left) inputs.add(VehicleInput.LEFT);
        if (right) inputs.add(VehicleInput.RIGHT);
        if (space) inputs.add(VehicleInput.SPACE);
        if (shift) inputs.add(VehicleInput.SHIFT);

        if (inputs.isEmpty()) return;

        ve.handleInput(inputs, player);
    }
}