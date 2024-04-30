package coma112.cbounty.config;

import coma112.cbounty.CBounty;
import coma112.cbounty.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigKeys {
    public static String BOUNTY_ITEM_MATERIAL = getString("bounty-item.material");
    public static String BOUNTY_ITEM_NAME = getString("bounty-item.name");
    public static int MAXIMUM_TOP = getInt("max-top");
    public static List<String> CODE_ITEM_LORE = getLoreList();

    private static String getString(@NotNull String path) {
        return MessageProcessor.process(CBounty.getInstance().getConfiguration().getString(path));
    }

    private static int getInt(@NotNull String path) {
        return CBounty.getInstance().getConfiguration().getInt(path);
    }

    private static List<String> getLoreList() {
        List<String> originalList = CBounty.getInstance().getConfiguration().getLoreList("bounty-item.lore");
        List<String> processedList = new ArrayList<>();

        originalList.forEach(line -> processedList.add(MessageProcessor.process(line)));

        return processedList;
    }
}
