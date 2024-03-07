package ru.ostrov77.snake;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.komiss77.ApiOstrov;
import ru.komiss77.enums.GameState;
import ru.ostrov77.minigames.MG;


public class SnakeCmd implements CommandExecutor, TabCompleter {

    public static List<String> subCommands = List.of("join", "leave", "start");
    
    
    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String command, String[] args) {
        final List<String> sugg = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                //0- пустой (то,что уже введено)
                for (String s : subCommands) {
                    if (s.startsWith(args[0])) {
                        sugg.add(s);
                    }
                }
            }
            case 2 -> {
                //1-то,что вводится (обновляется после каждой буквы
                //for (Arena a : AM.arenas.values()) {
                    sugg.addAll(AM.arenas.keySet());
                //}
            }

        }
        return sugg;
    }    
    
    
    @Override
    public boolean onCommand(CommandSender cs, Command command, String s, String[] args) {
        
        if (!(cs instanceof Player)) {
            cs.sendMessage("Not console commad!");
            return false;
        }
        
        
        Player p= (Player) cs;
        
        if (args.length == 0) {
            cs.sendMessage("join <арена>");
            cs.sendMessage("leave");
            cs.sendMessage("start <арена>");
            if ( ApiOstrov.isLocalBuilder(p, false)) {
                cs.sendMessage("create <арена>");
                cs.sendMessage("addspawn <арена>");
                cs.sendMessage("setlobby <арена>");
                cs.sendMessage("posh <арена>");
                cs.sendMessage("posl <арена>");
                cs.sendMessage("stop <арена>"); 
            }
            return false;
        }
        
        
            if (args[0].equalsIgnoreCase("join") ) {
                if (args.length == 2) {

                    Arena arena = AM.getArena(args[1]);

                    if (arena == null) {
                        p.sendMessage("§сНет такой арены!");
                        return true;
                    } 
                    if (AM.getArena(p)!=null) {
                        p.sendMessage("§сВы уже в игре!");
                        return true;
                    } else if (arena.arenaName == null) {
                        p.sendMessage("§4Арена испортилась - нет названия..");
                        return true;
                    } else if (arena.arenaLobby == null) {
                        p.sendMessage("§4Арена испортилась - нет лобби ожидания..");
                        return true;
                    } else if (arena.spawns == null || arena.spawns.isEmpty()) {
                        p.sendMessage("§4Арена испортилась - нет стартовых точек..");
                        return true;
                    }
                    if (arena.state == GameState.ОЖИДАНИЕ || arena.state == GameState.СТАРТ) {
                        if (arena.getPlayers().size() + 1 > arena.spawns.size()) {
                            p.sendMessage("§4Арена заполнена!");
                        } else {
                            arena.addPlayer(p);
                        }
                    } else {
                        arena.spectate(p);
                    }
                    
                    
                } else {
                    cs.sendMessage("§cНеверные аргументы: join <арена>");
                }
                return true;
                
            } else if (args[0].equalsIgnoreCase("leave") ) {
                
                final Arena a = AM.getArena(p);
                if (a!=null) {
                    a.removePlayer(p);
                }
                MG.lobbyJoin(p);
                
            } else if (args[0].equalsIgnoreCase("start")) {

                if ((args.length != 2  )) {
                    cs.sendMessage("§cInsufficiant Arguments. Proper use of the command goes like this: /ss start <arena name>");
                } else {
                    final Arena a = AM.getArena(args[1]);
                    if (a == null) {
                        cs.sendMessage("§cThat arena doesn\'t exist!");
                        return true;
                    }
                   a.forceStart(p);
                }
                return true;

            }
            
            
           
            
            
            
            
     if ( !ApiOstrov.isLocalBuilder(cs, false)) return false; 

    if (args[0].equalsIgnoreCase("create")) {

        if ((args.length != 2 )) {
            cs.sendMessage("§cInsufficiant Arguments! Proper use of the command goes like this: /ss create <arena name>");

        } else {

            if (AM.getArena(args[1]) != null) {
                cs.sendMessage("Арена с таким названием уже есть!");
                return true;
            }
            if (AM.getArenaByWorld(p.getWorld().getName()) != null) {
                cs.sendMessage("В этом мире уже есть арена!");
                return true;
            }
            if ( p.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) {
                cs.sendMessage("В этом мире нельзя добавить арену! Это глобальное лобби");
                return true;
            }
            
            AM.createArena( ((Player) cs).getLocation(), args[1]);
            cs.sendMessage("Создана арена "+args[1]+" !");

        }

        return true;








    } else  if (args[0].equalsIgnoreCase("posl") ) {
                if ((args.length != 2 || !cs.hasPermission("supersnake.arenacreation")) && (args.length != 2 || !cs.isOp())) {
                    cs.sendMessage("§cInsufficiant Arguments. Proper use of the command goes like this: /ss setboundslow <arena name>");
                } else {
                    if (AM.getArena(args[1]) == null) {
                        cs.sendMessage("§cCaptain, it is illogical to set the parameters of something that doesn\'t exist!");
                        return true;
                    }

                    if (AM.getArena(args[1]).getPlayers().size() > 0) {
                        cs.sendMessage("§cThat arena has players in it. Please stop the game before performing this operation.");
                        return true;
                    }

                    AM.getArena(args[1]).boundsLow = (((Player) cs).getLocation());
                    cs.sendMessage("§bSuccessfully set lower bound!");
                    AM.save = true;
                }

                return true;






    } else  if ((args[0].equalsIgnoreCase("list") )) {

        cs.sendMessage("§b§lАрен найдено: " + AM.arenas.size() );

        for ( Entry<String, Arena> e : AM.arenas.entrySet() ) {
            cs.sendMessage( "§e" + e.getKey()+" :§5"+ e.getValue().state   );
        }

        return true;








    } else  if (args[0].equalsIgnoreCase("stop") ) {

            if ((args.length != 2 )) {
                cs.sendMessage("§cInsufficiant Arguments! Proper use of the command goes like this: /ss stop <arena name>");
            } else {
                final Arena a = AM.getArena(args[1]);
                if (a == null) {
                    cs.sendMessage("§cThat arena doesn\'t exist!");
                    return true;
                }

                a.resetGame();
                cs.sendMessage("§bArena Stopped");
            }

            return true;









            }  else if (args[0].equalsIgnoreCase("posh") ) {
                    if ((args.length != 2 )) {
                        cs.sendMessage("§cInsufficiant Arguments. Proper use of the command goes like this: /ss setboundshigh <arena name>");
                    } else {
                        if (AM.getArena(args[1]) == null) {
                            cs.sendMessage("§cCaptain, it is illogical to set the parameters of something that doesn\'t exist!");
                            return true;
                        }

                        if (AM.getArena(args[1]).getPlayers().size() > 0) {
                            cs.sendMessage("§cThat arena has players in it. Please stop the game before performing this operation.");
                            return true;
                        }

                        if (AM.getArena(args[1]).boundsLow == null) {
                            AM.getArena(args[1]).boundsLow = (((Player) cs).getLocation());
                        }

                        AM.getArena(args[1]).boundsHigh = (((Player) cs).getLocation());
                        cs.sendMessage("§bSuccessfully set higher bound!");
                        AM.save = true;
                    }

                    return true;






               /* } else if (astring[0].equalsIgnoreCase("setlobby") ) {
                    if ((astring.length != 2 || !commandsender.hasPermission("supersnake.arenacreation")) && (astring.length != 2 || !commandsender.isOp())) {
                        commandsender.sendMessage("§cInsufficiant Arguments. Proper use of the command goes like this: /ss setlobby <arena name>");
                    } else {
                        if (AM.getArena(astring[1]) == null) {
                            commandsender.sendMessage("§cCaptain, it is illogical to set the parameters of something that doesn\'t exist!");
                            return true;
                        }

                        if (AM.getArena(astring[1]).getPlayers().size() > 0) {
                            commandsender.sendMessage("§cThat arena has players in it. Please stop the game before performing this operation.");
                            return true;
                        }

                        AM.setArenaLobby(((Player) commandsender).getLocation(), astring[1]);
                        commandsender.sendMessage("§bSuccessfully set arena lobby!");
                    }

                    return true;

                */    



                }  else if (args[0].equalsIgnoreCase("addspawn") ) {
                    if ((args.length != 2 )) {
                        cs.sendMessage("§cInsufficiant Arguments. Proper use of the command goes like this: /ss addspawn <arena name>");
                    } else {
                        if (AM.getArena(args[1]) == null) {
                            cs.sendMessage("§cCaptain, it is illogical to set the parameters of something that doesn\'t exist!");
                            return true;
                        }

                        if (!AM.getArena(args[1]).getPlayers().isEmpty()) {
                            cs.sendMessage("§cThat arena has players in it. Please stop the game before performing this operation.");
                            return true;
                        }

                        AM.getArena(args[1]).spawns.add(((Player) cs).getLocation());
                        cs.sendMessage("§bYou have added a new spawn point at your location!");
                        AM.save = true;
                    }

                    return true; 






                            
                            
        } 


        return true;
    }



    }
