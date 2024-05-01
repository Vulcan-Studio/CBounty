package coma112.cbounty.update;

import coma112.cbounty.CBounty;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final int resourceId;

    public UpdateChecker(int resourceId) {
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(CBounty.getInstance(), () -> {
            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId + "/~").openStream(); Scanner scanner = new Scanner(is)) {
                if (scanner.hasNext()) { consumer.accept(scanner.next()); }
            } catch (IOException exception) {
                CBounty.getInstance().getLogger().info("Unable to check for updates: " + exception.getMessage());
            }
        });
    }
}
