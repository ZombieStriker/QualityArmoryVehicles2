package me.zombie_striker.qav.command.subcommands;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.command.QAVCommand;
import me.zombie_striker.qav.command.SubCommand;
import me.zombie_striker.qav.menu.tram.TrackEditMenu;
import me.zombie_striker.qav.menu.tram.TracksMenu;
import me.zombie_striker.qav.tracks.data.Track;
import me.zombie_striker.qav.vehicles.AbstractTrain;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TracksCommand extends SubCommand {

    public static final String PERM = "qualityarmoryvehicles.tracks.admin";

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "menu", "list", "create", "delete", "edit", "assigntrain", "start", "stop", "cancel"
    );
    private static final List<String> TRACK_ID_SUBCOMMANDS = Arrays.asList(
            "delete", "edit", "start", "stop", "assigntrain"
    );

    public TracksCommand(QAVCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "tracks";
    }

    @Override
    public @Nullable String getDescription(@NotNull CommandSender sender) {
        return sender.hasPermission(PERM) ? MessagesConfig.COMMAND_TRAM_DESCRIPTION : null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PERM)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_NO_PERM);
            return;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("menu")) {
            if (sender instanceof Player) {
                new TracksMenu((Player) sender).open();
            } else {
                sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_CONSOLE_HINT);
            }
            return;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                handleCreate(sender, args);
                break;
            case "delete":
                handleDelete(sender, args);
                break;
            case "list":
                handleList(sender);
                break;
            case "start":
                handleSetRunning(sender, args, true);
                break;
            case "stop":
                handleSetRunning(sender, args, false);
                break;
            case "edit":
                handleEdit(sender, args);
                break;
            case "assigntrain":
                handleAssignTrain(sender, args);
                break;
            case "cancel":
                handleCancel(sender);
                break;
            default:
                sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_UNKNOWN_SUBCOMMAND);
        }
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_ONLY_PLAYERs);
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_USAGE_CREATE);
            return;
        }
        String id = args[1];
        if (Main.tracksManager.getTrack(id) != null) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_TRACK_ALREADY_EXISTS);
            return;
        }
        Player p = (Player) sender;
        Track track = Main.tracksManager.createTrack(id, p.getWorld().getName());
        try {
            Main.tracksManager.saveAll();
        } catch (IOException e) {
            sender.sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_SAVE_FAILED);
            Main.getPlugin(Main.class).getLogger().log(java.util.logging.Level.SEVERE, "Failed to save tram tracks", e);
            return;
        }
        sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_TRACK_CREATED
                .replace("%track%", track.getId())
                .replace("%world%", track.getWorldName()));
    }

    private void handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_USAGE_DELETE);
            return;
        }
        String id = args[1];
        if (Main.tracksManager.getTrack(id) == null) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_NO_TRACK_WITH_ID.replace("%track%", id));
            return;
        }
        Main.tracksManager.deleteTrack(id);
        try {
            Main.tracksManager.saveAll();
        } catch (IOException e) {
            sender.sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_SAVE_FAILED);
            Main.getPlugin(Main.class).getLogger().log(java.util.logging.Level.SEVERE, "Failed to save tram tracks", e);
            return;
        }
        sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_TRACK_DELETED.replace("%track%", id));
    }

    private void handleList(CommandSender sender) {
        List<String> ids = new ArrayList<>();
        for (Track t : Main.tracksManager.getTracks()) {
            ids.add(t.getId());
        }
        sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_TRACKS_LIST
                .replace("%tracks%", ids.isEmpty() ? MessagesConfig.COMMAND_TRAM_TRACKS_LIST_EMPTY : String.join(", ", ids)));
    }

    private void handleSetRunning(CommandSender sender, String[] args, boolean running) {
        if (args.length < 2) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_USAGE_SET_RUNNING
                    .replace("%action%", args[0].toLowerCase()));
            return;
        }
        Track track = requireTrack(sender, args[1]);
        if (track == null) return;

        Main.tracksManager.setRunning(track.getId(), running);
        if (running) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_TRACK_STARTED.replace("%track%", track.getId()));
        } else {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_TRACK_STOPPED.replace("%track%", track.getId()));
        }
    }

    private void handleEdit(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_ONLY_PLAYERs);
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_USAGE_EDIT);
            return;
        }
        Track track = requireTrack(sender, args[1]);
        if (track == null) return;

        new TrackEditMenu((Player) sender, track.getId(), null).open();
    }

    private void handleAssignTrain(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_ONLY_PLAYERs);
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_USAGE_ASSIGNTRAIN);
            return;
        }
        Track track = requireTrack(sender, args[1]);
        if (track == null) return;

        Player p = (Player) sender;
        VehicleEntity ve = null;
        if (p.getVehicle() != null) {
            ve = QualityArmoryVehicles.getVehicleEntityByEntity(p.getVehicle());
        }
        if (ve == null) {
            ve = QualityArmoryVehicles.getVehiclePlayerLookingAt(p);
        }
        if (ve == null) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_NO_VEHICLE_FOUND);
            return;
        }
        if (!(ve.getType() instanceof AbstractTrain)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_VEHICLE_NOT_TRAIN);
            return;
        }

        List<String> newTrains = new ArrayList<>(track.getTrains());
        String trainTypeName = ve.getType().getName();
        boolean alreadyAssigned = newTrains.stream().anyMatch(t -> t.equalsIgnoreCase(trainTypeName));
        if (!alreadyAssigned) {
            newTrains.add(trainTypeName);
        }
        track.setTrains(newTrains);
        try {
            Main.tracksManager.saveAll();
        } catch (IOException e) {
            sender.sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_SAVE_FAILED);
            Main.getPlugin(Main.class).getLogger().log(java.util.logging.Level.SEVERE, "Failed to save tram tracks", e);
            return;
        }
        sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_TRAIN_ASSIGNED
                .replace("%train%", ve.getType().getName())
                .replace("%track%", track.getId()));
    }

    private void handleCancel(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMANDMESSAGES_ONLY_PLAYERs);
            return;
        }

        Player p = (Player) sender;
        Main.trackEditorController.stopEditing(p);
        sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_CANCEL);
    }

    private @Nullable Track requireTrack(CommandSender sender, String id) {
        Track track = Main.tracksManager.getTrack(id);
        if (track == null) {
            sender.sendMessage(Main.prefix + MessagesConfig.COMMAND_TRAM_NO_TRACK_WITH_ID.replace("%track%", id));
        }
        return track;
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> out = new ArrayList<>();
        if (!sender.hasPermission(PERM)) return out;

        if (args.length == 1) {
            return new ArrayList<>(SUBCOMMANDS);
        }
        if (args.length == 2 && TRACK_ID_SUBCOMMANDS.contains(args[0].toLowerCase())) {
            for (Track t : Main.tracksManager.getTracks()) {
                out.add(t.getId());
            }
        }
        return out;
    }
}
