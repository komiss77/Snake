package ru.ostrov77.snake;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Messages {

    static Plugin plugin = Main.getPlugin(Main.class);
    
    static File customYml = new File(Messages.plugin.getDataFolder() + "/messages.yml");
    static FileConfiguration customConfig = YamlConfiguration.loadConfiguration(Messages.customYml);
    
    public static String gameStartingIn;
    public static String arenaStartingMessage;
    public static String titleMessageLose;
    public static String titleMessageWin;
    public static String playerJoinArena;
    public static String errorJoinMoreThenOneArena;
    public static String leaveArenaGuiItem;
    //public static String guiShopItemName;
    public static String errorInsufficiantFunds;
    public static String fastSnakeKitName;
    public static String ferrariSnakeKitName;
    //public static String itemAlreadyOwned;
    //public static String colorChooserName;
    public static String purchaseColorChooser;
    public static String selectedKitMessage;
    public static String selectedColorMessage;
    public static String purchasedAndSelectedMessage;
    public static String playerLeaveGameMessage;
    public static String joinInvalidArenaMessage;
    public static String joinArenaBadArguments;
    public static String playerLeaveArenaMessage;
    public static String playerLeaveNotInArena;
    public static String goMessage;
    public static String gameStartingPleaseWaitMessage;
    public static String playerLeaveBoundsMessage;
    public static String snakeHitMessage;
    public static String gameAlreadyStarted;
    public static String scoreboardTitle;
    public static String firstLineOfSign;
    public static String commandBlockerMsg;
    public static String creditForParticipation;
    public static String creditForWin;
    public static String creditForHitSomeoneOut;

    public static void loadAll() {
        Messages.customConfig.addDefault("gameStartingIn", "&6&lGame Starting In [X] Seconds!");
        Messages.customConfig.addDefault("arenaStartingMessage", "&3&lArena Starting In [X] Seconds!");
        Messages.customConfig.addDefault("titleMessageLose", "YOU LOSE!");
        Messages.customConfig.addDefault("titleMessageWin", "YOU WIN!");
        Messages.customConfig.addDefault("playerJoinArena", "You have joined an arena!");
        Messages.customConfig.addDefault("errorJoinMoreThenOneArena", "You cannot join more then one arena!");
        Messages.customConfig.addDefault("leaveArenaGuiItem", "Leave Arena");
        Messages.customConfig.addDefault("guiShopItemName", "&5Shop");
        Messages.customConfig.addDefault("errorInsufficiantFunds", "&cYou can\'t afford that!");
        Messages.customConfig.addDefault("fastSnakeKitName", "Fast Snake");
        Messages.customConfig.addDefault("ferrariSnakeKitName", "Ferrari Snake");
        Messages.customConfig.addDefault("itemAlreadyOwned", "&cYou already own that!");
        Messages.customConfig.addDefault("colorChooserName", "Color Chooser");
        Messages.customConfig.addDefault("purchaseColorChooser", "&aYou have purchased the color chooser!");
        Messages.customConfig.addDefault("selectedKitMessage", "&3You have selected kit [X]!");
        Messages.customConfig.addDefault("selectedColorMessage", "&3&lYou have selected [X] as your snake color!");
        Messages.customConfig.addDefault("purchasedAndSelectedMessage", "&3&lYou have purchased and selected kit [X]!");
        Messages.customConfig.addDefault("playerLeaveGameMessage", "&c[X] has left the game!");
        Messages.customConfig.addDefault("joinInvalidArenaMessage", "&cInvalid Arena!");
        Messages.customConfig.addDefault("joinArenaBadArguments", "&cInvalid Arguments! To join an arena you must do /snake join <arena name>");
        Messages.customConfig.addDefault("playerLeaveArenaMessage", "You have left the arena!");
        Messages.customConfig.addDefault("playerLeaveNotInArena", "&cYou are not in an arena!");
        Messages.customConfig.addDefault("goMessage", "&l&6Go!!");
        Messages.customConfig.addDefault("gameStartingPleaseWaitMessage", "&6Game Starting - Please Wait");
        Messages.customConfig.addDefault("playerLeaveBoundsMessage", "&cYou are not allowed to leave the arena!");
        Messages.customConfig.addDefault("snakeHitMessage", "&3[X] was hit by [Y]\'s snake. [Z] Players remain.");
        Messages.customConfig.addDefault("gameAlreadyStarted", "&cThis arena is already in-game. Please try another.");
        Messages.customConfig.addDefault("scoreboardTitle", "&6Super Snake");
        Messages.customConfig.addDefault("firstLineOfSign", "&6[Super Snake]");
        Messages.customConfig.addDefault("commandBlockerMsg", "&cYou were kicked from the arena!");
        Messages.customConfig.addDefault("creditForParticipation", "&6+[X] Gems for participation!");
        Messages.customConfig.addDefault("creditForWin", "&6+[X] Gems for winning!");
        Messages.customConfig.addDefault("creditForHitSomeoneOut", "&6+[X] Gems for [Y] kills!");
        Messages.customConfig.options().copyDefaults(true);
        saveCustomYml(Messages.customConfig, Messages.customYml);

        try {
            Iterator iterator = Messages.customConfig.getKeys(false).iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                Field field = Messages.class.getField(s);

                field.setAccessible(true);
                field.set((Object) null, Messages.customConfig.getString(s));
            }
        } catch (Exception exception) {
            ((Main) Main.getPlugin(Main.class)).getLogger().severe(ChatColor.RED + "Super Snake is having some issues loading the messages.yml. Please be sure you did not add any custom values or break anything. If this message persists, please delete messages.yml and try again.");
        }

    }

    private static void saveCustomYml(FileConfiguration fileconfiguration, File file) {
        try {
            fileconfiguration.save(file);
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

    }
}
