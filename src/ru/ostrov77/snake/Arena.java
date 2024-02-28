package ru.ostrov77.snake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.Song;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.enums.Game;
import ru.komiss77.enums.GameState;
import ru.komiss77.enums.Stat;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.scoreboard.SideBar;
import ru.komiss77.utils.DonatEffect;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.TCUtils;
import ru.ostrov77.minigames.IArena;
import ru.ostrov77.minigames.MG;
import ru.ostrov77.minigames.MiniGamesLst;

public class Arena implements IArena {

    public String arenaName;
    public Location boundsLow, boundsHigh, arenaLobby;
    public GameState state = GameState.ОЖИДАНИЕ;
    private List<Location> spawns = new ArrayList();
    
    private RadioSongPlayer songPlayer;
    private Random random = new Random();
    
    private BukkitTask task;

    private HashMap<String, DyeColor> SheepColor = new HashMap();
    public HashMap<String, Snake> players = new HashMap();

    private int minPlayers, maxplayers;
    private int cdCounter = 30;//ожид в лобби арены
    private int prestart = 7;//ожид сидя на овцах
    private int gameTime = 150;
    public int pickupGold = 0;
    private int ending = 20;//салюты,награждения
    //private boolean soloMode;

    
    public Arena(final List spawns, final String name, final Location arenaLobby, final Location boundsLow, final Location boundsHigh, final int minPlayers) {
        if (AM.ArenaExist(name)) {
            return; //не создаём дубль!!
        }
        this.arenaName = name;
        this.spawns = spawns;
        this.maxplayers = spawns.size();
        this.minPlayers = minPlayers;
        this.arenaLobby = arenaLobby;
        this.boundsLow = boundsLow;
        this.boundsHigh = boundsHigh;
    }
    
    public void resetGame() {
        if (state==GameState.ОЖИДАНИЕ) return;
        if (task != null)   task.cancel();
        arenaLobby.getWorld().getEntities().stream().forEach(e -> {
            if (e.getType() == EntityType.PLAYER) {
                ((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 1));
                removePlayer((Player) e);
            } else {
                e.remove();
            }
        });
        players.values().stream().forEach( sn -> {
            if (sn!=null) sn.stop(false);
        });
        players.clear();
        SheepColor.clear();
        cdCounter = 30;
        prestart = 7;
        gameTime = 150;
        ending = 20;
        pickupGold = 0;
        StopMusic();
        state = GameState.ОЖИДАНИЕ;
        Main.sendBsignMysql(arenaName, state.displayColor+state.name(), "", state, 0);
    }
    
    
    public void startCountdown() {                            //ожидание в лобби
        if (state != GameState.ОЖИДАНИЕ)  return;
        state = GameState.СТАРТ;

        SendTitle("§aЗмейка стартует через", "§b" + cdCounter + " сек.!");

        task = (new BukkitRunnable() {
            @Override
            public void run() {

                if (cdCounter == 0) {
                    cdCounter = 30;
                    this.cancel();
                    PrepareToStart();

                }  else if (cdCounter > 0) {
                    --cdCounter;
                    Main.sendBsignChanel(arenaName, 
                            "§6Игроки: §2" + players.size(), state.displayColor + state.name() + " §4" + cdCounter, state, players.size());
                    Oplayer op;
                    for (Player p : getPlayers()) {
                        op = PM.getOplayer(p);
                        op.score.getSideBar().setTitle("§6До старта: §b"+(cdCounter+7));
                        if (cdCounter <= 5 ) {
                            p.playSound(p.getEyeLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5.0F, 5.0F);
                        }
                    }
                }

            }
        }).runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    public void forceStart(Player p) {
        if (state!=GameState.СТАРТ) {
            p.sendMessage("§cYou can't start an arena - arena must have state СТАРТ!");
            return;
        }
        if (task != null && cdCounter > 3) {
            cdCounter = 3;
        }
        p.sendMessage("§bВремя до старта уменьшено");
    }

    public void PrepareToStart() {
        if (state != GameState.СТАРТ)  return;
        state = GameState.ЭКИПИРОВКА;
        if (task != null)  task.cancel();
        
        StartMusic();

        int i = 0;
        for (Player p : getPlayers()) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 1));
            p.teleport(spawns.get(i));
            i++;

            p.getInventory().clear();
            generateColor(p.getName()); //генерируем цвет

            players.put(p.getName(), new Snake(p, getColor(p.getName()), this));

            if (Shop.selected.containsKey(p.getUniqueId())) {
                if (((String) Shop.selected.get(p.getUniqueId())).contains("fastsnake")) {
                    p.getInventory().setItem(0, new ItemStack(Material.FEATHER, Files.fastKitBoosts));
                }
                if (((String) Shop.selected.get(p.getUniqueId())).contains("ferrarisnake")) {
                    p.getInventory().setItem(0, new ItemStack(Material.FEATHER, Files.ferrariKitBoosts));
                }
            }
            p.playSound(p.getEyeLocation(), Sound.ENTITY_SHEEP_AMBIENT, 2, 2);
        }
        //soloMode = players.size()==1;

        task = (new BukkitRunnable() {
            @Override
            public void run() {
                if (players.isEmpty()) {
                    resetGame();
                }
                
                if (prestart == 0) {
                    prestart = 7;
                    this.cancel();
                    GameProgress();

                } else {
                    Oplayer op;
                    for (Player p : getPlayers()) {
                        op = PM.getOplayer(p);
                        op.score.getSideBar().setTitle("§6До старта: §b"+prestart);
                        ApiOstrov.sendActionBarDirect(p, "§aОвцы готовятся к забегу : §b" + prestart + " §aсек.!");
                    }
                    --prestart;
                }
                
            }
        }).runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    
    public void GameProgress() {
        if (state != GameState.ЭКИПИРОВКА)  return;
        state = GameState.ИГРА;
        if (task != null)  task.cancel();

        task = (new BukkitRunnable() {
            @Override
            public void run() {
                if (gameTime==0) {
                    this.cancel();
                    SendTitle("", "Игра окончена!");
                    endGame(true); 
                    return;
                } else if (players.isEmpty()) {
                    this.cancel();
                    resetGame();
                    return;
                }

                gameTime--;
                if (gameTime%5==0) {
                    spawnSugar();
                }
                
                final String time = "§b§l" + getTime(gameTime);
                
                Main.sendBsignChanel(arenaName, time, "§6Игроки: §2" + players.size(), state, players.size());
                
                Oplayer op;
                Snake sn;
                for (Player p : getPlayers()) {
                    op = PM.getOplayer(p);
                    sn = players.get(p.getName());
                    SideBar sb = op.score.getSideBar().setTitle(time);
                    for (String name : players.keySet()) {
                        if (sn!=null) {
                            sb.update(name, getChatColor(name) + name + " §f"+sn.playerSheep.size());
                        }
                    }
                    ApiOstrov.sendActionBarDirect(p, "§6ПОЕХАЛИ! §aПОЕХАЛИ! §bПОЕХАЛИ!");
                }
            }
        }).runTaskTimer(Main.getInstance(), 0L, 20L);

    }



    private void spawnSugar() {
        int ammount = 0;
        for (Entity e : arenaLobby.getWorld().getEntities()) {
            if (e.getType() != EntityType.PLAYER && e.getTicksLived() > 300) {
                e.remove();
            }
            if (e.getType() == EntityType.DROPPED_ITEM) {
                ammount++;
            }
        }
        for (int i = ammount; i < 10; i++) {
            final ItemStack is = new ItemStack(Material.SUGAR, 1);// AM.bonus.clone();
            final ItemMeta im = is.getItemMeta();
            im.displayName(Component.text(String.valueOf(random.nextInt(999))));
            is.setItemMeta(im);
            Item item = arenaLobby.getWorld().dropItem(randomFielldLoc(), is);
            item.setVelocity(new Vector(0, 0, 0));
            item.setPickupDelay(1);
            item.setGravity(false);
        }
    }

    private Location randomFielldLoc() {
        int x, y, z;
        if (boundsLow.getBlockX() > boundsHigh.getBlockX()) {
            x = ApiOstrov.randInt(boundsHigh.getBlockX(), boundsLow.getBlockX());
        } else {
            x = ApiOstrov.randInt(boundsLow.getBlockX(), boundsHigh.getBlockX());
        }
        y = ((Location) spawns.get(0)).getBlockY() + 1;
        if (boundsLow.getBlockZ() > boundsHigh.getBlockZ()) {
            z = ApiOstrov.randInt(boundsHigh.getBlockZ(), boundsLow.getBlockZ());
        } else {
            z = ApiOstrov.randInt(boundsLow.getBlockZ(), boundsHigh.getBlockZ());
        }
        return new Location(arenaLobby.getWorld(), (double) x, (double) y, (double) z);
    }

    
    //либо остался один на поле после столкновений, либо вышло время и все победили
    public void endGame(final boolean timeOut) {
        if (state != GameState.ИГРА)  return;
        state = GameState.ФИНИШ;
        if (task != null) task.cancel();

        Main.sendBsignChanel(arenaName, "§1 - / -", state.displayColor+state.name(), state, players.size());

        final boolean drop = timeOut || (!timeOut && players.size()==1);
        for (Player winner : getPlayers()) {
            final Snake sn = players.get(winner.getName());
            if (sn!=null) {
                sn.stop(drop);
            }
            if (drop) {
                ApiOstrov.sendTitle(winner, "§aВы победили!", "§fСобирайте монеты, это Ваша награда!", 5, 20, 5);
                winner.playSound(winner.getEyeLocation(), Sound.BLOCK_ANVIL_FALL, 1.0F, 1.0F);
            }
        }

        task = (new BukkitRunnable() {
            @Override
            public void run() {

                if (ending > 5) {
                    SendAB("§5Собрано: §4" + pickupGold + "  §6Времени осталось: §b" + (ending - 5));
                }

                if (ending > 5 && ending <= 10) {
                    SendSound(Sound.BLOCK_COMPARATOR_CLICK);
                }


                if (ending<=6) {
                    for (Player winner : getPlayers()) {
                        if (ending == 6) {
                            winner.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 130, 0));
                        }
                        if (ending <= 5) {
                            DonatEffect.spawnRandomFirework(winner.getEyeLocation());
                        }
                        if (ending == 5) {
                            winner.sendMessage("§fМонет собрано: §b" + winner.getLevel() + " §f!");
                            ApiOstrov.addStat(winner, Stat.SN_game);
                            ApiOstrov.addStat(winner, Stat.SN_win);
                            ApiOstrov.addStat(winner, Stat.SN_gold, winner.getLevel());
                            winner.setLevel(0);
                        }
                    }
                }

                if (ending <= 0) {
                    this.cancel();
                    resetGame();
                }

                --ending;
            }
        }).runTaskTimer(Main.getInstance(), 0L, 20L);

        //final String winner_name = players.keySet().stream().findFirst().get();
     //   if (!timeOut && winner_name != null) {

            //Player winner = Bukkit.getPlayerExact(winner_name);
            //if (winner != null) {

                //playerTracker.get(winner_name).cancel();
               // ApiOstrov.sendTitle(winner, "§aВы победили!", "§fСобирайте золото, это Ваша награда!", 5, 20, 5);
                //winner.playSound(winner.getEyeLocation(), Sound.BLOCK_ANVIL_FALL, 1.0F, 1.0F);
                //arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
                //    p.sendMessage("§f§oПобедитель: " + TCUtils.toChat(getColor(winner_name)) + winner_name + " §f§o Выбил игроков: §b" + playerTracker.get(winner_name).kills + " §f§o!");
                //});

       //     } else {
         //       resetGame();
          //  }

       // } else {
       //     resetGame();
       // }

    }




    public void collide(final Player who, final String snakeOwner) {

        if (who.getName().equals(snakeOwner)) {
            SendAB(getChatColor(snakeOwner)+snakeOwner + " §6стлкнулся со своей змейкой!");
        } else {
            SendAB(getChatColor(who.getName())+who.getName()+" §a6врезался в змейку "+getChatColor(snakeOwner)+snakeOwner+"§6!");
        }

        //final Snake sn = players.remove(who.getName());
        //if (sn != null) {
       //     sn.stop();
        //}
        removePlayer(who);
        
        if (players.size()==1) { //остался последний победитель
            endGame(false);
        } else {
            MiniGamesLst.spectatorPrepare(who);
            ApiOstrov.sendTitle(who, "", "§4Вы проиграли!");
            who.teleport(who.getLocation().add(0, 3, 0));
            ApiOstrov.addStat(who, Stat.SN_game);
            ApiOstrov.addStat(who, Stat.SN_loose);
            who.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            who.getWorld().playSound(who.getEyeLocation(), Sound.ENTITY_DONKEY_ANGRY, 0.8f, 2.0f);
        }

    }

    
    
    
    public void addPlayers(Player p) { //всё проверено
        if (!players.containsKey(p.getName())) {
            players.put(p.getName(), null);
            p.teleport(getArenaLobby());
            
            if (players.size()==1) {
                startCountdown();
            } else {
                int cd = cdCounter/players.size();
                if (cd < cdCounter) {
                   cdCounter = cd;
                   SendAB("§2§lВремя до старта игры уменьшено!");
                }
            }

            Main.colorChoice.giveForce(p);
            p.getInventory().setItem(7, ItemUtils.air);
            MG.leaveArena.giveForce(p);//p.getInventory().setItem(8, UniversalListener.leaveArena.clone());
            PM.getOplayer(p).tabSuffix(" §5"+arenaName, p);
            Main.sendBsignChanel(arenaName, "§2" + players.size(), state.displayColor + state.name(), state, players.size());
        }
    }

    // 1-команда leave 2-дисконнект 3-столкнулся с собой или другим
    public void removePlayer(final Player p) {
        if (players.containsKey(p.getName())) {
            final Snake sn = players.remove(p.getName());
            if (sn!=null) {
                sn.stop(false);
            }
            if (players.isEmpty()) {
                if (task!=null) {
                    resetGame();
                } else {
                    Main.sendBsignChanel(arenaName, "§2"+players.size(), state.displayColor + state.name(), state, players.size());
                }
            } else {
                Oplayer op;
                for (Player pl : getPlayers()) {
                    op = PM.getOplayer(pl);
                    op.score.getSideBar().update(p.getName(), "§4§o✖ §m"+getChatColor(p.getName()) + p.getName());
                }
                Main.sendBsignChanel(arenaName, "§2"+players.size(), state.displayColor + state.name(), state, players.size());
            }
        }
    }

    public void SetSheepColor(String nik, DyeColor c) {
        SheepColor.put(nik, c);
    }

    public void generateColor(String nik) {
        if (SheepColor.containsKey(nik)) {
            return;
        }
        DyeColor color;
        for (short i = 0; i < 100; i++) {
            color = TCUtils.randomDyeColor();
            if (!SheepColor.containsValue(color)) {
                SheepColor.put(nik, color);
                break;
            }
        }
    }

    public DyeColor getColor(String nik) {
        return SheepColor.getOrDefault(nik, DyeColor.WHITE);
    }
    
    public String getChatColor(String nik) {
        return TCUtils.toChat(SheepColor.getOrDefault(nik, DyeColor.WHITE));
    }
    
    public List<Player> getPlayers() {
        final List<Player> list = new ArrayList<>();
        for (String nik : players.keySet()) {
            final Player p = Bukkit.getPlayerExact(nik);
            if (p != null) {
                list.add(p);
            }
        }
        return list;
    }



    public List<Location> getSpawns() {
        return this.spawns;
    }

    public Location getArenaLobby() {
        return arenaLobby;
    }

    public void setArenaLobby(Location location) {
        this.arenaLobby = location;
    }

    public Location getBoundsLow() {
        return this.boundsLow;
    }

    public Location getBoundsHigh() {
        return this.boundsHigh;
    }

    public void setBoundsLow(Location location) {
        this.boundsLow = location;
    }

    public void setBoundsHigh(Location location) {
        this.boundsHigh = location;
    }

    public int getMinPlayers() {
        return this.minPlayers;
    }

    public void setMinPlayers(int i) {
        if (i <= this.maxplayers) {
            this.minPlayers = i;
        } else {
            this.minPlayers = this.maxplayers;
        }
    }

    public int getMaxPlayers() {
        return this.maxplayers;
    }

    public void addSpawn(Location location) {
        this.spawns.add(location);
        this.maxplayers = this.spawns.size();
    }

    public void SendAB(final String text) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            ApiOstrov.sendActionBarDirect(p, text);
        });
    }

    public void SendSound(final Sound s) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            p.playSound(p.getLocation(), s, 5.0F, 5.0F);
        });
    }

    public void SendTitle(final String t, final String st) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            ApiOstrov.sendTitle(p, t, st, 5, 20, 5);
        });
    }


    private static String getTime(final long sec) {
        final long s = TimeUnit.SECONDS.toSeconds(sec) - TimeUnit.SECONDS.toMinutes(sec) * 60L;
        final long m = TimeUnit.SECONDS.toMinutes(sec) - TimeUnit.SECONDS.toHours(sec) * 60L;
        return String.format("%02d", m) + ":" + String.format("%02d", s);
    }

    private void StartMusic() {

        try {
            if (Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
                
                File song = AM.songs.get(random.nextInt(AM.songs.size()));
                Song s = NBSDecoder.parse(song);

                songPlayer = new RadioSongPlayer(s);
                songPlayer.setAutoDestroy(true);

                songPlayer.setPlaying(true);

                arenaLobby.getWorld().getPlayers().stream().forEach( p -> {
                    songPlayer.addPlayer(p);
                });

                songPlayer.setVolume((byte) 60);
                songPlayer.setFadeStart((byte) 25);
            }
        } catch (NullPointerException e) {
        }

    }

    private void StopMusic() {
        try {
            if (Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
                songPlayer.setPlaying(false);
                songPlayer.destroy();
            }
        } catch (NullPointerException e) {
        }
    }

    @Override
    public Game game() {
        return Game.SN;
    }

    @Override
    public boolean hasPlayer(final Player p) {
        return players.containsKey(p.getName());
    }

    @Override
    public String joinCmd() {
        return "snake join ";
    }

    @Override
    public String leaveCmd() {
        return "snake leave";
    }

    public void spectate(final Player p) {
        MiniGamesLst.spectatorPrepare(p);
        p.teleport(randomFielldLoc().add(0, 3, 0));
    }

}
