package me.zombie_striker.qav.tracks.editor;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.tracks.data.Track;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.IntConsumer;
import java.util.logging.Level;

public class TrackEditorController {

    private final @NotNull Map<UUID, String> sessions = new HashMap<>();
    private final @NotNull ConversationFactory conversationFactory;
    private final @NotNull Main plugin;

    public TrackEditorController(@NotNull Main plugin) {
        this.plugin = plugin;
        this.conversationFactory = new ConversationFactory(plugin)
                .withLocalEcho(false)
                .withModality(false)
                .withEscapeSequence(MessagesConfig.TRAM_CANCEL_KEYWORD)
                .thatExcludesNonPlayersWithMessage(MessagesConfig.colorize(MessagesConfig.MESSAGE_TRAM_PLAYERS_ONLY))
                .addConversationAbandonedListener(event -> {
                    if (event.gracefulExit()) return;

                    event.getContext().getForWhom().sendRawMessage(Main.prefix + MessagesConfig.colorize(MessagesConfig.COMMAND_TRAM_CANCEL));
                });
    }

    public void startStopSelection(@NotNull Player player, @NotNull String trackId) {
        Track track = Main.tracksManager.getTrack(trackId);
        if (track == null) {
            player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_TRACK_NOT_FOUND.replace("%track%", trackId));
            return;
        }
        sessions.put(player.getUniqueId(), trackId);
        player.sendMessage(Main.prefix + MessagesConfig.colorize(MessagesConfig.MESSAGE_TRAM_SELECT_STOP));
    }

    public void stopEditing(@NotNull Player player) {
        sessions.remove(player.getUniqueId());
    }

    public @Nullable String getSession(@NotNull Player player) {
        return sessions.get(player.getUniqueId());
    }

    public void handleRailClickForStop(@NotNull Player player, @NotNull Block clickedRail) {
        String session = getSession(player);
        if (session == null) return;

        Track track = Main.tracksManager.getTrack(session);
        if (track == null) {
            player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_TRACK_NOT_FOUND_GENERIC);
            stopEditing(player);
            return;
        }

        track.setWorldName(clickedRail.getWorld().getName());

        player.spawnParticle(Particle.VILLAGER_HAPPY,
                clickedRail.getLocation().clone().add(0.5, 0.5, 0.5), 8, 0.15, 0.15, 0.15, 0);

        Conversation conv = conversationFactory
                .withFirstPrompt(new StopNamePrompt(track, (stopName) ->
                        new DwellSecondsPrompt((seconds) -> {
                            Main.tracksManager.addStop(track, stopName, clickedRail.getLocation(), seconds);
                            try {
                                Main.tracksManager.saveAll();
                                String secondsWithPlural = seconds + (seconds == 1 ? "" : "s");
                                player.sendMessage(Main.prefix + MessagesConfig.colorize(
                                        MessagesConfig.MESSAGE_TRAM_STOP_ADDED
                                                .replace("%name%", stopName)
                                                .replace("%seconds%s", secondsWithPlural)
                                                .replace("%seconds%", String.valueOf(seconds))));
                            } catch (IOException e) {
                                player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_SAVE_FAILED);
                                plugin.getLogger().log(Level.SEVERE, "Failed to save tram tracks", e);
                            } finally {
                                stopEditing(player);
                            }
                        })))
                .buildConversation(player);

        conv.begin();
    }

    private static class DwellSecondsPrompt extends StringPrompt {
        private final @NotNull IntConsumer onDone;

        private DwellSecondsPrompt(@NotNull IntConsumer onDone) {
            this.onDone = onDone;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return Main.prefix + MessagesConfig.colorize(MessagesConfig.MESSAGE_TRAM_STOP_PROMPT);
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context, String input) {
            String trimmed = input == null ? "" : input.trim();
            try {
                int seconds = Integer.parseInt(trimmed);
                if (seconds < 0 || seconds > 3600) {
                    context.getForWhom().sendRawMessage(MessagesConfig.colorize(MessagesConfig.MESSAGE_TRAM_DWELL_RANGE));
                    return this;
                }
                onDone.accept(seconds);
                return Prompt.END_OF_CONVERSATION;
            } catch (NumberFormatException e) {
                context.getForWhom().sendRawMessage(MessagesConfig.colorize(MessagesConfig.MESSAGE_TRAM_DWELL_INVALID));
                return this;
            }
        }
    }

    private static class StopNamePrompt extends StringPrompt {

        private final @NotNull Track track;
        private final @NotNull java.util.function.Function<String, Prompt> nextPromptFactory;

        private StopNamePrompt(@NotNull Track track,
                               @NotNull java.util.function.Function<String, Prompt> nextPromptFactory) {
            this.track = track;
            this.nextPromptFactory = nextPromptFactory;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return Main.prefix + MessagesConfig.colorize(MessagesConfig.MESSAGE_TRAM_STOP_NAME_PROMPT);
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context, String input) {
            String trimmed = input == null ? "" : input.trim();
            String stopName;
            if (trimmed.isEmpty()) {
                stopName = MessagesConfig.MESSAGE_TRAM_DEFAULT_STOP_NAME
                        .replace("%index%", String.valueOf(track.getStops().size() + 1));
            } else {
                stopName = trimmed;
            }
            return nextPromptFactory.apply(stopName);
        }
    }
}
