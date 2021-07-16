package ru.ostrov77.snake.Manager;


import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.ostrov77.snake.Main;
import ru.ostrov77.snake.Objects.Arena;



public class ScoreBoard {
 
    // private static HashMap < UUID, Scoreboard > scoreStore = new HashMap<>();
    // private static List <String> state = new ArrayList<>();
     
     
     
     
    public static void StartScore () {     
         
    
    (new BukkitRunnable() {
            @Override
            public void run() {
                
                for (Player p : Bukkit.getOnlinePlayers()) {
                    
                    final Oplayer op = PM.getOplayer(p);
                    
                    if ( AM.isInGame(p)) {
                        
                        op.score.getSideBar().reset();
                        
                    } else {
                        
                        op.score.getSideBar().setTitle("§6§l§oЗмейка");
                        int line=1;
                        
                        Arena arena = AM.getArenaByWorld(p.getWorld().getName());
                        
                        if (arena==null) { //lobby
                                
                            for (World w : Bukkit.getWorlds()) {
                                arena = AM.getArenaByWorld(w.getName());
                                if (arena!=null) {
                                    op.score.getSideBar().updateLine( line, "§6" + arena.getName()+"§7:"+ arena.getStateAsString() );
                                    line++;
                                }
                            }

                        } else { //в мире арены
                            
                            for (Player ap:arena.getPlayers()) {
                                op.score.getSideBar().updateLine(line, arena.GetScoreStatus(ap));
                                line++;
                            }
                            
                        }
                        

                    }
                    
                    
                }
                
                
             /*   Bukkit.getWorlds().forEach((w) -> {
                    final Scoreboard sb = Get_score(AM.getArenaByWorld(w.getName()));
                        w.getPlayers().forEach((p) -> {
                            /in game
                            p.setScoreboard(sb);
                        });                
                });*/
      
            }}).runTaskTimer(Main.getInstance(), 3L, 20L);  
    

     
    }
 



/*

  private static Scoreboard Get_score ( final Arena ar ) {   
     
        
            final Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            final Objective registerNewObjective = newScoreboard.registerNewObjective("vote", "dummy");
            registerNewObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            
            registerNewObjective.setDisplayName("§6§l§oЗмейка");
            
            registerNewObjective.getScore("§a-----------------").setScore(16); 
            
           
            
            if ( ar==null ) {        //если в лобби
                
                int pos=15;
                for (Arena arena:AM.getAllArenas().values()) { 
                    registerNewObjective.getScore("§e" + arena.getName()+" : "+ arena.getStateAsString()).setScore(pos); 
                    pos--;
                }
                
                registerNewObjective.getScore("").setScore(pos); 
                pos--;
                registerNewObjective.getScore("§a------------------").setScore(pos); 
                
            
            
            
            
            } else {                                                    //если в мир игры
                
                registerNewObjective.getScore(ar.getScoreTimer()).setScore(15); 
                registerNewObjective.getScore("").setScore(14); 

                int pos=13;

                for (Player p2:ar.getPlayers()) {
                    registerNewObjective.getScore(ar.GetScoreStatus(p2)).setScore(pos);
                    pos--;
                }
            
            registerNewObjective.getScore("").setScore(pos); 
            pos--;
            registerNewObjective.getScore("§a------------------").setScore(pos); 
                
            }

           

    return newScoreboard;
}
 */
 
 

 
 
 

 
 
 
 
 
 
 
 
 
}
    
