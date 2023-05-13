package ru.ostrov77.snake.Objects;

import com.destroystokyo.paper.entity.ai.Goal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.Song;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import ru.komiss77.ApiOstrov;
import ru.komiss77.enums.Stat;
import ru.komiss77.utils.ColorUtils;
import ru.komiss77.utils.LocationUtil;

import ru.ostrov77.snake.listener.GuiListener;
import ru.ostrov77.snake.Main;
import ru.ostrov77.snake.Manager.AM;
import ru.ostrov77.snake.Manager.Files;
import ru.ostrov77.snake.Manager.FollowGoal;
import ru.ostrov77.snake.Manager.Shop;


public class Arena {

    private String name;
    private Location boundsLow,boundsHigh,arenaLobby;
    private GameState state=GameState.WAITING;;
    private RadioSongPlayer songPlayer;    
    private Random random = new Random();;
    private BukkitTask SugarTask,CoolDown,PreStart,GameTimer,EndGame;
    
    private HashMap <String,DyeColor> SheepColor = new HashMap();
    public HashMap<String,Snake> playerTracker = new HashMap();
    private List<Item> sugars = new ArrayList();
    private List<Location> spawns = new ArrayList();
    private Set<String> players = new HashSet<>();
    
    private int minPlayers,maxplayers;
    private int cdCounter=30;//ожид в лобби арены
    private int prestart=7;//ожид сидя на овцах
    private int playtime,pickupGold=0;
    private int ending=20;//салюты,награждения
    
    private boolean canreset=true;
    
    
    
    public Arena( final List spawns, final String name, final Location arenaLobby, final Location boundsLow, final Location boundsHigh, final int minPlayers ) {
        if (AM.ArenaExist(name)) return; //не создаём дубль!!
        this.name = name;
        this.spawns = spawns;
        this.maxplayers = spawns.size();
        this.minPlayers = minPlayers;
        this.arenaLobby = arenaLobby;
        this.boundsLow = boundsLow;
        this.boundsHigh = boundsHigh;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
 
 
    
    public void startCountdown() {                            //ожидание в лобби
            if (getState() != GameState.WAITING) return;
            setState(GameState.STARTING);

            SendTitle("§aЗмейка стартует через", "§b"+cdCounter+" сек.!");

            this.CoolDown = (new BukkitRunnable() {
                @Override
                public void run() {

                    if (cdCounter == 0) {
                            Arena.this.cdCounter = 30;
                            this.cancel();
                            PrepareToStart();

                    } else if ( players.size() < minPlayers ) {
                        SendAB("§d§lНедостаточно участников, счётчик остановлен.");
                        setState(GameState.WAITING);
                        Arena.this.cdCounter = 30;
                        this.cancel();

                    } else if ( players.size() == maxplayers && cdCounter > 10 ) {
                        SendAB("§2§lВремя до старта игры уменьшено!");
                        cdCounter = 10;

                    } else if (cdCounter > 0) {
                            --cdCounter;
                            Main.sendBsignChanel(name, "§6Игроки: §2"+ arenaLobby.getWorld().getPlayers().size(), getStateAsString()+" §4"+cdCounter, ru.komiss77.enums.GameState.СТАРТ, arenaLobby.getWorld().getPlayers().size());
                            SendAB("§eДо старта: §f"+cdCounter);
                            if (cdCounter <= 5 && cdCounter > 0) {
                                SendTitle("§b"+cdCounter+" !", "");
                                SendSound(Sound.BLOCK_COMPARATOR_CLICK);
                            }
                    } 

                }
            }).runTaskTimer(Main.getInstance(), 0L, 20L);
        }



    public void ForceStart() {
         if (getState() != GameState.STARTING) return;
            if (CoolDown != null && cdCounter>3)  cdCounter=3;
           // PrepareToStart();
        }





    
    public void PrepareToStart() {
        if (getState() != GameState.STARTING) return;
        setState(GameState.STARTED);
        if (this.CoolDown != null)  this.CoolDown.cancel();

        StartMusic();

            //for (int i=0; i<players.size(); i++) {                      //распределение игроков по спавнам
            int i=0;
            for (Player p:getPlayers()) {                      //распределение игроков по спавнам
//Bukkit.broadcastMessage("§ePrepareToStart"+p.getName());
                //Player p = players.get(i);
               // if (p!=null && p.isOnline()) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 1), true);
                    p.teleport(this.spawns.get(i));
                    i++;

                    p.getInventory().clear();

                    GenSheepColor(p.getName());        //генерируем цвет

                    playerTracker.put(p.getName(), new Snake(p, GetSheepColor(p.getName()), this) );

                    if (Shop.selected.containsKey(p.getUniqueId())) {
                        if (((String) Shop.selected.get(p.getUniqueId())).contains("fastsnake"))   p.getInventory().setItem(0, new ItemStack(Material.FEATHER, Files.fastKitBoosts));
                        if (((String) Shop.selected.get(p.getUniqueId())).contains("ferrarisnake"))   p.getInventory().setItem(0, new ItemStack(Material.FEATHER, Files.ferrariKitBoosts));
                    }

               // } else {
               //     PlayerExit(p);
               // }
            }

            SendSound(Sound.ENTITY_SHEEP_AMBIENT);


                this.PreStart = (new BukkitRunnable() {
                @Override
                public void run() {

                        if ( players.isEmpty() && canreset ) resetGame();

                            if ( players.size() < minPlayers ) {
                                Arena.this.SendAB("§d§lСлишкома мало игроков, отмена.");
                                this.cancel();
                                if (canreset) Arena.this.resetGame();

                            } else if (Arena.this.prestart==0) {
                                Arena.this.prestart = 7;
                                this.cancel();
                                GameProgress();

                            } else {
                                SendAB("§aОвцы заправляются... Осталось §b"+prestart+" §aсекунд!");
                                --Arena.this.prestart;
                            }



                }
                }).runTaskTimer(Main.getInstance(), 0L, 20L);
        }
    
   

    public void GameProgress() {
        if (getState() != GameState.STARTED) return;
        setState(GameState.INGAME);
        if (this.PreStart != null)  this.PreStart.cancel();


        startSugarSpawn();

        SendAB("§6ПОЕХАЛИ! §aПОЕХАЛИ! §bПОЕХАЛИ!");


            this.GameTimer = (new BukkitRunnable() {
                @Override
                public void run() {

//for (Snake sn : playerTracker.values()) {
   // Mob mob = (Mob) sn.masterSheep;
    //boolean has = Bukkit.getMobGoals().hasGoal(mob, FollowGoal.key);
    //Bukkit.broadcastMessage("-> snake:"+sn.name+" goal size="+Bukkit.getMobGoals().getAllGoals(mob).size());
    //for( Goal g : Bukkit.getMobGoals().getAllGoals(mob)) {
        //Bukkit.broadcastMessage(""+g.getKey().getNamespacedKey()+" "+g.getTypes().toString());
        //Bukkit.broadcastMessage(mob.getPathfinder().hasPath() ? ("getFinalPoint="+LocationUtil.StringFromLoc(mob.getPathfinder().getCurrentPath().getFinalPoint())) : "!hasPath" );
    //}
    //Bukkit.broadcastMessage("");
//}
                    if ( playerTracker.isEmpty() || playtime > 150 && canreset) {
                        SendTitle("Время вышло!", "Игра окончена!");
                        endGame(true);//resetGame();
                    } else if (playerTracker.size()==1) {
                            this.cancel();
                            endGame(false);
                    }

                    playtime++;
                    //SignsListener.updateSigns( getName(), 1, players.size(), getStateAsString(), playtime );
                    Main.sendBsignChanel(name, getStateAsString(), "§6Игроки: §2"+ players.size(), ru.komiss77.enums.GameState.ИГРА, arenaLobby.getWorld().getPlayers().size()); 
                }
            }).runTaskTimer(Main.getInstance(), 0L, 20L);

    }






    public void endGame(final boolean timeOut) {   
        if (getState() != GameState.INGAME) return;
        state=GameState.ENDING;
        if (GameTimer != null)  GameTimer.cancel();

        //SignsListener.updateSigns( getName(), 1, maxplayers, getStateAsString(), playtime );
        Main.sendBsignChanel(getName(), "§1 - / -", getStateAsString(), ru.komiss77.enums.GameState.ФИНИШ, arenaLobby.getWorld().getPlayers().size());
        
      //  try {
            final String winner_name = playerTracker.keySet().stream().findFirst().get();
            if (!timeOut && winner_name!=null && !winner_name.isEmpty()) {
                
                final Player winner = Bukkit.getPlayerExact(winner_name);
                if (winner!=null) {
                    
                    playerTracker.get(winner_name).cancel();
                    ApiOstrov.sendTitle(winner, "§aВы победили!", "§fСобирайте золото, это Ваша награда!",5,20,5);
                    winner.playSound(winner.getLocation(), Sound.BLOCK_ANVIL_FALL , 1.0F, 1.0F);
                    arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
                        p.sendMessage("§f§oПобедитель: " +  ColorUtils.ChatColorfromDyeColor(GetSheepColor(winner_name))+winner_name+ " §f§o Выбил игроков: §b" + playerTracker.get(winner_name).kills +" §f§o!");
                    });  
                    
                    this.EndGame = (new BukkitRunnable() {
                        @Override
                        public void run() {

                            if (ending > 5) SendAB("§5Собрано: §4"+pickupGold+"  §6Времени осталось: §b"+(ending-5) );

                            if (ending > 5 && ending <= 10 ) {
                                SendSound(Sound.BLOCK_COMPARATOR_CLICK);
                            } 

                            if (ending == 6) winner.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 130, 0));

                            if (ending == 5) {
                                winner.sendMessage("§fСлитков собрано: §b"+pickupGold+" §f!" );
                                //winner.sendMessage("§fВы получаете на счёт §6"+pickupGold*10+" §fр.!" );
                                ApiOstrov.addStat(winner, Stat.SN_game);
                                ApiOstrov.addStat(winner, Stat.SN_win);
                                //for (int g=0; g<pickupGold;g++) {
                                    ApiOstrov.addStat(winner, Stat.SN_gold, pickupGold);
                                //}
                                //ApiOstrov.moneyChange(winner, pickupGold*10, "Змейка, собрано слитков: "+pickupGold);
                                firework(winner);
                            }

                            if (ending <=0) {
                                 this.cancel();
                                 resetGame();
                            }

                            --ending;
                        }
                    }).runTaskTimer(Main.getInstance(), 0L, 20L);    
                    
                } else {
                    resetGame();
                }
                
            } else {
                resetGame();
            }
 



    }





    public void resetGame() {  

        StopMusic();

        canreset=false;
        if (CoolDown != null)  CoolDown.cancel();
        if (EndGame != null)  EndGame.cancel();
        if (PreStart != null)  PreStart.cancel();
        if (GameTimer != null)  GameTimer.cancel();
        if (SugarTask != null)  SugarTask.cancel(); 


        try { sugars.stream().forEach((sugar) -> { sugar.remove();}); } catch (NullPointerException e) {}     //убираем сахар

        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> { p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 1), true); });
        //this.arenaLobby.getWorld().getPlayers().stream().forEach((p) -> { p.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation()); });
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> { PlayerExit(p); });
        arenaLobby.getWorld().getEntities().stream().forEach( (e) -> { 
            if (e.getType()!=EntityType.PLAYER) e.remove();   
        }
        );

        sugars.clear();
        playerTracker.clear();
        players.clear();
        SheepColor.clear();


        cdCounter=30;
        prestart = 7;
        playtime = 0;
        ending=20;
        pickupGold = 0;

        setState(GameState.WAITING);
        canreset=true;
        StopMusic();
        Main.sendBsignMysql(name, getStateAsString(), "", ru.komiss77.enums.GameState.ОЖИДАНИЕ, 0);
    }

    
 












    
    private void startSugarSpawn() {
        SugarTask = (new BukkitRunnable() {
            @Override
            public void run() {
                if ( state == GameState.INGAME) {
                    int i = random.nextInt(Files.chanceOfSugerSpawning);

                    if (i == 0 && sugars.size() < Files.maxSugarOnGround) {
                        int j;

                        if (boundsLow.getBlockX() > boundsHigh.getBlockX()) {
                            j = ApiOstrov.randInt(boundsHigh.getBlockX(), boundsLow.getBlockX());
                        } else {
                            j = ApiOstrov.randInt(boundsLow.getBlockX(), boundsHigh.getBlockX());
                        }

                        int k = ((Location) spawns.get(0)).getBlockY() + 1;
                        int l;

                        if (boundsLow.getBlockZ() > boundsHigh.getBlockZ()) {
                            l = ApiOstrov.randInt(boundsHigh.getBlockZ(), boundsLow.getBlockZ());
                        } else {
                            l = ApiOstrov.randInt(boundsLow.getBlockZ(), boundsHigh.getBlockZ());
                        }

                        Item item = arenaLobby.getWorld().dropItemNaturally(new Location(arenaLobby.getWorld(), (double) j, (double) k, (double) l), new ItemStack(Material.SUGAR, 1));

                        sugars.add(item);
                    }

                } else  this.cancel();
            }
        }).runTaskTimer(Main.getInstance(), 0L, 20L);
    }

  
    public boolean UseSugar (Item sugar) {
        if (sugars.contains(sugar)) {
            sugars.remove(sugar);
            return true;
        } else return false;
    }
    
    
    
    
    

    
 
    
    
    
    
    public void Collide (final Player who, final String snakeOwner) {

        if ( who.getName().equals(snakeOwner) ) {
                SendAB( "§"+SheepColor.get(who.getName())+snakeOwner+" §a6наступил себе на хвост!");
            } else {
                SendAB( "§"+SheepColor.get(snakeOwner)+snakeOwner+" §a6врезался в §"+SheepColor.get(who.getName())+who.getName()+"§a!");
            }
        //LoosePlayer(loose);
            
        if ( playerTracker.containsKey(who.getName()) ) {
            playerTracker.get(who.getName()).cancel();
            playerTracker.remove(who.getName());
            if ( playerTracker.size() ==1 ) endGame(false);  
        }
                
        who.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        who.getWorld().playSound(who.getLocation(), Sound.ENTITY_DONKEY_ANGRY , 0.8f, 2.0f);
        ApiOstrov.sendTitle(who, "", "§4Вы проиграли!");
        who.teleport(arenaLobby);
        ApiOstrov.addStat(who, Stat.SN_game);
        ApiOstrov.addStat(who, Stat.SN_loose);
        who.getInventory().clear();
        who.getInventory().setItem(8, GuiListener.exitGame);
        who.updateInventory();
        
        //if ( playerTracker.size() ==1 ) endGame();    

    }





   
    
    
    
    public void addPlayers(Player p) {

        if (!players.contains(p.getName())) {
            players.add(p.getName());
            p.teleport(getArenaLobby());
            if (minPlayers>players.size()) SendAB ("§6Для старта нужно еще §b" + (minPlayers-players.size())+" §6чел.!" ); 
            p.getInventory().clear();
            p.getInventory().setItem(0, GuiListener.colorChoice);
            p.getInventory().setItem(8, GuiListener.exitGame);
            //GuiListener.giveExitItem(p);
            //GuiListener.givePlayerShop(p);
            //GuiListener.givePlayerNametag(p);
            p.updateInventory();
            //SignsListener.updateSigns(name, players.size(), maxplayers, getStateAsString(), playtime );
            Main.sendBsignChanel(name, "§2"+ arenaLobby.getWorld().getPlayers().size(), getStateAsString(), ru.komiss77.enums.GameState.ОЖИДАНИЕ, arenaLobby.getWorld().getPlayers().size());
            if ( players.size()>=minPlayers ) startCountdown();
           } 

        }

    
    
    public void PlayerExit (final Player p) {
        if (IsJonable()) {              //если waiting, starting
        
            if ( players.contains(p.getName()) ) {
                players.remove(p.getName());
                    if (players.size() < minPlayers && CoolDown != null) {
                        CoolDown.cancel();
                        cdCounter = 30;
                        SendAB("§d§lНедостаточно участников, счётчик остановлен.");
                        setState(GameState.WAITING);
                    }
            }
            //Utils.sendActionBar(p, "§fВы вышли с арены!");
            p.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
            //SignsListener.updateSigns( getName(), players.size(), maxplayers, getStateAsString(), playtime );
            Main.sendBsignChanel(name, "§2"+ arenaLobby.getWorld().getPlayers().size(), getStateAsString(), ru.komiss77.enums.GameState.ОЖИДАНИЕ, arenaLobby.getWorld().getPlayers().size());
        
        } else {                //если игра
        
            if ( playerTracker.containsKey(p.getName()) ) {  //подстраховка
                playerTracker.get(p.getName()).cancel();
                playerTracker.remove(p.getName());
                //SignsListener.updateSigns( getName(), playerTracker.size(), maxplayers, getStateAsString(), playtime );
                Main.sendBsignChanel(name, "§2"+ arenaLobby.getWorld().getPlayers().size(), getStateAsString(), ru.komiss77.enums.GameState.ПЕРЕЗАПУСК, arenaLobby.getWorld().getPlayers().size());
                //if ( playerTracker.size() ==1 ) endGame();   тут нельзя, или определит победиля при таймауте
            } 
            if ( players.contains(p.getName()) ) players.remove(p.getName());
            p.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        }




    }


    

    
    public void AddGold () {
        this.pickupGold++;
    }
    
    

    public void SetSheepColor ( String nik, DyeColor c ) {
        this.SheepColor.put(nik, c);
    }
   
    
    public void GenSheepColor ( String nik ) {
        
        if (this.SheepColor.containsKey(nik)) return;
        DyeColor color;
        
        for ( short i=0; i<100; i++) {
            
            color = ColorUtils.randomDyeColor();
            
            if( !this.SheepColor.containsValue(color)) {
                this.SheepColor.put(nik, color);
                break;
            } 
        }
        
    }
    
    
    public DyeColor GetSheepColor ( String nik ) {
        if (this.SheepColor.containsKey(nik)) return this.SheepColor.get(nik);
        else return DyeColor.WHITE;
    }
    
    

    
    public boolean isInThisArena(Player p) {
        return players.contains(p.getName());
    }
    
    public boolean IsInThisGame(Player p) {
        return playerTracker.containsKey(p.getName());
    }
    
    public String GetScoreStatus (Player p) {
        if (!players.contains(p.getName())) {
            
            return "§f§o- Зритель -";
            
        } else if (state == GameState.WAITING || state == GameState.STARTING ) {
            
            return ColorUtils.ChatColorfromDyeColor(GetSheepColor(p.getName()))+p.getName();
            
        } else if (playerTracker.containsKey(p.getName())) {
            
            return "§2§o✔ "+ ColorUtils.ChatColorfromDyeColor(GetSheepColor(p.getName()))+p.getName();
            
        } else return "§4§o✖ "+ ColorUtils.ChatColorfromDyeColor(GetSheepColor(p.getName()))+p.getName();
    }
    
    
    
   /* 
    public boolean HasSpeedBoost(Player player) {
        return players.contains(player.getName()) && playerTracker.containsKey(player.getName()) ? ( playerTracker.get(player.getName())).GetSpeedBoost() != 0 : false;
    }
    public boolean HasSugarBoosted(Player player) {
        return players.contains(player.getName()) && playerTracker.containsKey(player.getName()) ? ( playerTracker.get(player.getName())).IsSugarBoost() : false;
    }
    public void SetSpeedBoost(Player p, int speed) {
        if ( players.contains(p.getName()) && playerTracker.containsKey(p.getName()))  playerTracker.get(p.getName()).SetSpeedBoost(speed);
    }
    public void SetSugarBoosted(Player p, boolean b) {
         if ( players.contains(p.getName()) && playerTracker.containsKey(p.getName()))  playerTracker.get(p.getName()).SetSugarBoosted(b);
     }
*/

    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
  
    
    
    
    
    
    

    public String getName() {
        return this.name;
    }

    public GameState getState() {
        return this.state;
    }
    public void setState(GameState gamestate) {
         this.state = gamestate;
        // SignsListener.updateSigns( getName(), players.size(), maxplayers, getStateAsString(), playtime );
     }

    public String getStateAsString() {
         switch (this.state) {
             case WAITING:
                 return "§2Заходите §8(§f"+players.size()+"§8)";
             case STARTING:
                 return "§6Стартует §8(§f"+players.size()+"§8)";
             case STARTED:
                 return "§eЗаправка §8(§f"+players.size()+"§8)";
             case INGAME:
                 return "§4Игра §8(§f"+players.size()+"§8)";
             case ENDING:
                 return "§5Финиш";
             default:
                 return "";
         }
     }


    public String getScoreTimer() {
         switch (getState()) {
             case INGAME:
                 return "§7Время: §f§l"+getTime(playtime);
             case WAITING:
                 return "§aОжидаем игроков.. (§b"+(minPlayers-players.size())+"§a)";
             case STARTING:
                 return "§7До старта: §b§l"+cdCounter ;
             case STARTED:
                 return "§f§lЗаправка сеном.." ;
             default:
                 return getStateAsString();
         }
    }

    public HashMap<String,DyeColor> getScorePlayer() {
         return this.SheepColor;
    }
   
   
   
   
    
    
    public List<Player> getPlayers() {
        List<Player>list=new ArrayList<>();
        players.stream().filter((nik) -> (Bukkit.getPlayerExact(nik)!=null)).forEachOrdered((nik) -> {
            list.add(Bukkit.getPlayerExact(nik));
        });
        return list;
    }
    
    
    
    
    
    public boolean hasStarted() {
        return ( state == GameState.INGAME );
    }
    
    public boolean IsJonable() {
        return ( state == GameState.WAITING || state == GameState.STARTING );
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
        if (i<=this.maxplayers) this.minPlayers = i;
        else this.minPlayers=this.maxplayers;
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
            p.playSound(p.getLocation(), s , 5.0F, 5.0F);
        });
    }

    public void SendTitle(final String t, final String st) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            ApiOstrov.sendTitle(p,  t, st, 5, 20, 5);
        });
    }
    
   /* public void SetLevel(final int lv) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            p.setLevel(lv);
        });
    }*/


 
    
///салютики
    private static void firework (Player p) {
        
        for (int i = 0; i < 6; ++i) {                           //салютики
            new BukkitRunnable() {
                @Override
                public void run() {
            Random random = new Random();
            Firework firework = (Firework) p.getWorld().spawn(p.getLocation().clone().add(0, 5, 0), Firework.class);
            FireworkMeta fireworkmeta = firework.getFireworkMeta();
            FireworkEffect fireworkeffect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256))).withFade(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256))).with(FireworkEffect.Type.STAR).trail(true).build();

            fireworkmeta.addEffect(fireworkeffect);
            firework.setFireworkMeta(fireworkmeta);   
                }}.runTaskLater(Main.getInstance(), (long)(i * 5));}
    }
    
    
    


    
 
    private static String getTime(final long n) {

            final long sec = TimeUnit.SECONDS.toSeconds(n) - TimeUnit.SECONDS.toMinutes(n) * 60L;
            final long min = TimeUnit.SECONDS.toMinutes(n) - TimeUnit.SECONDS.toHours(n) * 60L;

           // return  ( n2>0 ? n+":"+n2 : "00:"+n );
           return String.format("%02d", min) + ":" + String.format("%02d", sec);
        }






    private void StartMusic () {

        try {
            if (Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {

                File[] files = new File(Main.getInstance().getDataFolder().getPath() + "/songs/").listFiles();
                List<File> songs = new ArrayList<>();
                for (File f : files)
                    if (f.getName().contains(".nbs")) songs.add(f);
                File song = songs.get(new Random().nextInt(songs.size()));
                Song s = NBSDecoder.parse(song);

               // songPlayer = new PositionSongPlayer(s);
                songPlayer = new RadioSongPlayer(s);
                songPlayer.setAutoDestroy(true);

                //songPlayer.setTargetLocation(arenaLobby);
                songPlayer.setPlaying(true);

                arenaLobby.getWorld().getPlayers().stream().forEach((p) -> { songPlayer.addPlayer(p); });

                songPlayer.setVolume((byte) 60);
                songPlayer.setFadeStart((byte) 25);
            }
        } catch (NullPointerException e){}

    }


    private void StopMusic () {
        try {
            if (Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
                songPlayer.setPlaying(false);
                this.songPlayer.destroy(); 
            }
        } catch (NullPointerException e){}
    }




    
}
