package pl.chathistory;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryStore {
    public record Entry(long time, boolean command, String text) {
        public String display() {
            String t = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(time));
            return "[" + t + "] " + (command ? "/" : "") + text;
        }
    }

    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("chathistory.txt");
    public static final List<Entry> ENTRIES = new ArrayList<>();

    public static void load() {
        ENTRIES.clear();
        if (!Files.exists(FILE)) return;
        try {
            for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                String[] p = line.split("\t", 3);
                if (p.length < 3) continue;
                ENTRIES.add(new Entry(Long.parseLong(p[0]), p[1].equals("C"), p[2]));
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void add(boolean command, String text) {
        Entry e = new Entry(System.currentTimeMillis(), command, text.replace("\n", " "));
        ENTRIES.add(e);
        try {
            Files.writeString(FILE, e.time() + "\t" + (command ? "C" : "M") + "\t" + e.text() + "\n",
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
