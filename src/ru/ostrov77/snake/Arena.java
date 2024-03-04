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
import java.util.EnumSet;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.enums.Game;
import ru.komiss77.enums.GameState;
import ru.komiss77.enums.Stat;
import ru.komiss77.modules.games.GM;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.scoreboard.SideBar;
import ru.komiss77.utils.DonatEffect;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.LocationUtil;
import ru.komiss77.utils.TCUtils;
import ru.komiss77.version.Nms;
import ru.ostrov77.minigames.IArena;
import ru.ostrov77.minigames.MG;
import ru.ostrov77.minigames.MiniGamesLst;

public class Arena implements IArena {

    public String arenaName;
    public Location boundsLow, boundsHigh, arenaLobby;
    public GameState state = GameState.ВЫКЛЮЧЕНА;
    public List<Location> spawns = new ArrayList();
    
    private RadioSongPlayer songPlayer;
    private Random random = new Random();
    
    private BukkitTask task, sheepTask;

    public List <Sheep> grow = new ArrayList<>();
    public HashMap<String, Snake> players = new HashMap();

    private int cdCounter = 30;//ожид в лобби арены
    private int prestart = 7;//ожид сидя на овцах
    private int gameTime = 150;
    public int pickupGold = 0;
    private int ending = 20;//салюты,награждения

    
    public Arena(final List<Location> spawns, final String name, final Location arenaLobby, final Location boundsLow, final Location boundsHigh) {
        this.arenaName = name;
        this.spawns = spawns;
        this.arenaLobby = arenaLobby;
        this.boundsLow = boundsLow;
        this.boundsHigh = boundsHigh;
    }
    
    public void resetGame() {
        if (state==GameState.ОЖИДАНИЕ) return;
        if (task != null)   task.cancel();
        if (sheepTask != null)   sheepTask.cancel();
        arenaLobby.getWorld().getEntities().stream().forEach( e -> {
            if (e.getType() == EntityType.PLAYER) {
                MG.lobbyJoin((Player) e);
            } else {
                e.remove();
            }
        });
        players.values().stream().forEach( sn -> {
            sn.stop(false);
        });
        players.clear();
        grow.clear();
        cdCounter = 30;
        prestart = 7;
        gameTime = 150;
        ending = 20;
        pickupGold = 0;
        StopMusic();
        state = GameState.ОЖИДАНИЕ;
        sendArenaData();
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
                    sendArenaData();
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
        final EnumSet colors = EnumSet.noneOf(DyeColor.class);
        for (Snake sn : players.values()) {
            if (sn.color !=null) {
                colors.add(sn.color);
            }
        }
        
        DyeColor color;
        Snake sn;
        
        for (Player p : getPlayers()) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 1));
            p.teleport(spawns.get(i));
            i++;

            p.getInventory().clear();
            sn = players.get(p.getName());
            if (sn.color==null) {
                for (int x = 0; x < 100; x++) {
                    color = TCUtils.randomDyeColor();
                    if (!colors.contains(color)) {
                        colors.add(color);
                        sn.color = color;
                        break;
                    }
                }
            }

            sn.start(p);

          /*  if (Shop.selected.containsKey(p.getUniqueId())) {
                if (((String) Shop.selected.get(p.getUniqueId())).contains("fastsnake")) {
                    p.getInventory().setItem(0, new ItemStack(Material.FEATHER, 10));
                }
                if (((String) Shop.selected.get(p.getUniqueId())).contains("ferrarisnake")) {
                    p.getInventory().setItem(0, new ItemStack(Material.FEATHER, 20));
                }
            }*/
            p.playSound(p.getEyeLocation(), Sound.ENTITY_SHEEP_AMBIENT, 2, 2);
        }

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
        
        sheepTask = (new BukkitRunnable() {
            int t;
            @Override
            public void run() {
                if (t%7==0) {
                    Sheep sheep;
                    if (!grow.isEmpty()) {
                        for (int i = grow.size()-1; i>=0; i--) {
                            sheep = grow.get(i);
                            if (sheep.getTicksLived()>300) {
                                sheep.getWorld().playEffect(sheep.getLocation(), Effect.SMOKE, 1);
                                sheep.remove();
                                grow.remove(i);
                            }
                        }
                    }
                    while (grow.size()<10) {
                        final Location loc = randomFielldLoc();
                        final Sheep s = (Sheep) loc.getWorld().spawnEntity(loc, EntityType.SHEEP);
                        s.setInvulnerable(true);
                        s.setNoDamageTicks(Integer.MAX_VALUE);
                        Bukkit.getMobGoals().removeAllGoals(s);
                        s.setAI(false);
                        s.setGlowing(false);
                        s.setGravity(false);
                        s.setBaby();
                        s.setAgeLock(true);
                        s.setTicksLived(1+random.nextInt(200)); //Age value (0) must be greater than 0
                        s.setRotation( random.nextInt(360) - 180, random.nextInt(180) - 90);
                        grow.add(s);
                    }
                }
                
                grow.stream().forEach( s -> {
                    if (t%5==0) s.setColor(DyeColor.values()[random.nextInt(16)]);
                    float yaw = s.getYaw();
                    if (s.getPitch()>0) {
                      yaw+=s.getTicksLived()/10;
                      if (yaw>=360) {
                        yaw = 0;
                      }
                    } else {
                      yaw-=s.getTicksLived()/10;
                      if (yaw<=-360) {
                        yaw = 0;
                      }
                    }
                    s.setRotation(yaw, s.getPitch() );
                });
                t++;
            }
        }).runTaskTimer(Main.getInstance(), 0L, 1);

    }

    
    public void GameProgress() {
        if (state != GameState.ЭКИПИРОВКА)  return;
        state = GameState.ИГРА;
        if (task != null)  task.cancel();

        Oplayer op;
        for (Player p : getPlayers()) {
            op = PM.getOplayer(p);
            SideBar sb = op.score.getSideBar().setTitle("§66ПОЕХАЛИ");
            for (String name : players.keySet()) {
                sb.add(name, players.get(name).getChatColor()+name+" §f0");
            }
            sb.build();
            ApiOstrov.sendActionBarDirect(p, "§6ПОЕХАЛИ! §aПОЕХАЛИ! §bПОЕХАЛИ!");
        }
        
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
                    spawnItems();
                }
                
                final String time = "§b§l" + getTime(gameTime);
                
                sendArenaData();
                
                Oplayer op;
                Snake sn;
                for (Player p : getPlayers()) {
                    op = PM.getOplayer(p);
                    sn = players.get(p.getName());
                    SideBar sb = op.score.getSideBar().setTitle(time);
                    for (String name : players.keySet()) {
                        if (sn!=null) {
                            sb.update(name, sn.getChatColor()+name+" §f"+sn.tail.size());
                        }
                    }
                }
            }
        }).runTaskTimer(Main.getInstance(), 0L, 20L);
  

        
    }


    private void spawnItems() {
        int sugar = 0;
        Item i;
        for (Entity e : arenaLobby.getWorld().getEntities()) {
            if (e.getType()!=EntityType.DROPPED_ITEM) continue;
            if (e.getTicksLived() > 300) {
                e.remove();
            }
            i = (Item) e;
            if (i.getItemStack().getType() == Material.SUGAR) {
                sugar++;
            }
        }
        addItems(Material.SUGAR, 5 - sugar);
    }

    private void addItems(final Material mat, final int ammount) {
        if (ammount<=0) return;
       for (int i = 0; i < ammount; i++) {
            final ItemStack is = new ItemStack(mat, 1);// AM.bonus.clone();
            final Item item = arenaLobby.getWorld().dropItem(randomFielldLoc(), is);
            item.setVelocity(new Vector(0, 0, 0));
            item.setPickupDelay(1);
            item.setGravity(false);
            //item.setGlowing(true);
        }
    }
    
    
    private Location randomFielldLoc() {
        int y = (spawns.get(0)).getBlockY() ;
        int x, z;
        Location loc = null;
        for (int i = 0; i<10; i++) {
            if (boundsLow.getBlockX() > boundsHigh.getBlockX()) {
                x = ApiOstrov.randInt(boundsHigh.getBlockX(), boundsLow.getBlockX());
            } else {
                x = ApiOstrov.randInt(boundsLow.getBlockX(), boundsHigh.getBlockX());
            }
            if (boundsLow.getBlockZ() > boundsHigh.getBlockZ()) {
                z = ApiOstrov.randInt(boundsHigh.getBlockZ(), boundsLow.getBlockZ());
            } else {
                z = ApiOstrov.randInt(boundsLow.getBlockZ(), boundsHigh.getBlockZ());
            }
                   
            if (LocationUtil.isFeetAllow(Nms.getFastMat(arenaLobby.getWorld(), x, y, z))
                    && Nms.getFastMat(arenaLobby.getWorld(), x, y+1, z) == Material.AIR
                    && Nms.getFastMat(arenaLobby.getWorld(), x, y+2, z) == Material.AIR
                    && Nms.getFastMat(arenaLobby.getWorld(), x, y+3, z) == Material.AIR
                    ) {
                loc = new Location(arenaLobby.getWorld(), (double) x, (double) y, (double) z);
            }
        }
        if (loc==null) {
            return spawns.get(random.nextInt(spawns.size()-1));
        } else {
            return loc;
        }
    }

    
    //либо остался один на поле после столкновений, либо вышло время и все победили
    public void endGame(final boolean timeOut) {
        if (state != GameState.ИГРА)  return;
        state = GameState.ФИНИШ;
        if (task != null) task.cancel();
        if (sheepTask != null) sheepTask.cancel();

        sendArenaData();

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
                            final Snake sn = players.get(winner.getName());
                            winner.sendMessage("§fМонет собрано: §b"+sn.coin+" §f!");
                            ApiOstrov.addStat(winner, Stat.SN_game);
                            ApiOstrov.addStat(winner, Stat.SN_win);
                            ApiOstrov.addStat(winner, Stat.SN_gold, sn.coin);
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




    public void collide(final Player who) {

        removePlayer(who);
        if (players.isEmpty()) { //была одиночная игра
            ending = 5;
            endGame(false);
        } else if (players.size()==1) { //остался последний победитель
            endGame(false);
        } else {
            MiniGamesLst.spectatorPrepare(who);
            ApiOstrov.sendTitle(who, "", "§4Вы проиграли!");
            who.teleport(who.getEyeLocation().add(0, 2, 0));
            ApiOstrov.addStat(who, Stat.SN_game);
            ApiOstrov.addStat(who, Stat.SN_loose);
            who.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            who.getWorld().playSound(who.getEyeLocation(), Sound.ENTITY_DONKEY_ANGRY, 0.8f, 2.0f);
            //players.remove(who.getName());
            
        }

    }

    
    
    
    public void addPlayer(Player p) { //всё проверено
        if (!players.containsKey(p.getName())) {
            players.put(p.getName(), new Snake(p, this));
            p.teleport(arenaLobby);
            
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
            if (p.hasPermission("forcestart")) {
                MG.forceStart.giveForce(p);
            }
            MG.leaveArena.giveForce(p);//p.getInventory().setItem(8, UniversalListener.leaveArena.clone());
            p.getInventory().setItem(7, ItemUtils.air);
            p.getInventory().setItem(1, ItemUtils.air);
            PM.getOplayer(p).tabSuffix(" §5"+arenaName, p);
            sendArenaData();
        }
    }

    // 1-команда leave 2-дисконнект 3-столкнулся с собой или другим
    public void removePlayer(final Player p) {
        if (players.containsKey(p.getName())) {
            final Snake sn = players.remove(p.getName());
            sn.stop(!players.isEmpty());
            if (players.isEmpty() && (state==GameState.СТАРТ || state == GameState.ЭКИПИРОВКА)) {
                if (task!=null) {
                    resetGame();
                } else {
                    sendArenaData();
                }
            } else {
                Oplayer op;
                for (Player pl : getPlayers()) {
                    op = PM.getOplayer(pl);
                    op.score.getSideBar().update(p.getName(), "§4§o✖ §m"+sn.getChatColor() + p.getName());
                }
                sendArenaData();
            }
        }
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


/*
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


    public void addSpawn(Location location) {
        this.spawns.add(location);
    }
*/
    public void SendAB(final String text) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            ApiOstrov.sendActionBarDirect(p, text);
        });
    }

    public void SendSound(final Sound s) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            p.playSound(p.getEyeLocation(), s, 5.0F, 5.0F);
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
        return "snake join "+arenaName;
    }

    @Override
    public String leaveCmd() {
        return "snake leave";
    }

    public void spectate(final Player p) {
        MiniGamesLst.spectatorPrepare(p);
        p.teleport(randomFielldLoc().add(0, 3, 0));
    }

    public void sendArenaData() {
        GM.sendArenaData(
                Game.SN, 
                arenaName, 
                state, 
                players.size(), 
                "§2§l§oЗмейка",
                "§5"+arenaName, 
                state.displayColor+state.name(),
                "§6Игроки: §2"+players.size()
        );
    } 

    @Override
    public String forceStartCmd() {
        return "snake start "+arenaName;
    }

   
}
