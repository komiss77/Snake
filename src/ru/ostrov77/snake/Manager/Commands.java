package ru.ostrov77.snake.Manager;

import ru.ostrov77.snake.Objects.Arena;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ostrov77.snake.Messages;

public class Commands extends JavaPlugin {

    public static boolean handleCommand(CommandSender commandsender, Command command, String s, String[] astring) {
        
        if ( !(commandsender instanceof Player) ) return false;
        
        
        Player p= (Player) commandsender;
        
        if (astring.length == 0) {
            commandsender.sendMessage("? аргументы ?");
            commandsender.sendMessage("join <арена>");
            if ( !p.isOp()) {
                commandsender.sendMessage("create <арена>");
                commandsender.sendMessage("addspawn <арена>");
                commandsender.sendMessage("setlobby <арена>");
                commandsender.sendMessage("posh <арена>");
                commandsender.sendMessage("posl <арена>");
                commandsender.sendMessage("setminplayers <арена>");
                commandsender.sendMessage("start <арена>");
                commandsender.sendMessage("stop <арена>"); 
            }
            return false;
        }
        
        if (astring[0].equalsIgnoreCase("join") && commandsender instanceof Player) {
            if (astring.length == 2) {
                if (AM.getArena(astring[1]) == null) {
                    commandsender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Messages.joinInvalidArenaMessage));
                    return true;
                }

                AM.addPlayer((Player) commandsender, astring[1]);
            } else {
                commandsender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Messages.joinArenaBadArguments));
            }

            return true;
            
        }    
            
            
           
            
            
            
            
     if ( !p.isOp()) return false; 
     
     /*   }  else if (astring[0].equalsIgnoreCase("setminplayers")) {
            if (!commandsender.hasPermission("supersnake.arenacreation") && !commandsender.isOp()) {
                commandsender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
            } else if (astring.length == 3) {
                if (AM.getArena(astring[1]) == null) {
                    commandsender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Messages.joinInvalidArenaMessage));
                    return true;
                }

                boolean flag = true;

                int i;

                try {
                    i = Integer.valueOf(astring[2]);
                } catch (Exception exception) {
                    commandsender.sendMessage(ChatColor.RED + "The third argument is not a valid int!");
                    return true;
                }

                AM.getArena(astring[1]).setMinPlayers(i);
                commandsender.sendMessage(ChatColor.AQUA + "Successfully set the min players to " + astring[2] + "!");
            } else {
                commandsender.sendMessage(ChatColor.RED + "Proper usage of the command goes like this: /snake setminplayers <arena name> <int>");
            }

            return true;
            
            
            
            
            
            
            
            
        } else 
            if ((!astring[0].equalsIgnoreCase("leave") || !(commandsender instanceof Player)) && (!astring[0].equalsIgnoreCase("depart") || !(commandsender instanceof Player))) {
            */
            
            
            
            
            
            if (astring[0].equalsIgnoreCase("create")) {
               
                if ((astring.length != 2 )) {
                    commandsender.sendMessage(ChatColor.RED + "Insufficiant Arguments! Proper use of the command goes like this: /ss create <arena name>");
               
                } else {
                
                    if (AM.ArenaExist(astring[1])) 
                        commandsender.sendMessage("Арена с таким названием уже есть!");
                    else if (!AM.CanCreate((Player) commandsender))
                        commandsender.sendMessage("В этом мире уже есть арена!");
                    else if ( p.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName()))
                        commandsender.sendMessage("В этом мире нельзя добавить арену! Это глобальное лобби");
                    else {
                        AM.createArena( ((Player) commandsender).getLocation(), astring[1]);
                        commandsender.sendMessage("Создана арена "+astring[1]+" !");
                    }
                    
                }

                return true;
                
              
                
                
                
                
                
                
            } else  if (astring[0].equalsIgnoreCase("posl") ) {
                        if ((astring.length != 2 || !commandsender.hasPermission("supersnake.arenacreation")) && (astring.length != 2 || !commandsender.isOp())) {
                            commandsender.sendMessage(ChatColor.RED + "Insufficiant Arguments. Proper use of the command goes like this: /ss setboundslow <arena name>");
                        } else {
                            if (AM.getArena(astring[1]) == null) {
                                commandsender.sendMessage(ChatColor.RED + "Captain, it is illogical to set the parameters of something that doesn\'t exist!");
                                return true;
                            }

                            if (AM.getArena(astring[1]).getPlayers().size() > 0) {
                                commandsender.sendMessage(ChatColor.RED + "That arena has players in it. Please stop the game before performing this operation.");
                                return true;
                            }

                            AM.setBoundsLow(((Player) commandsender).getLocation(), astring[1]);
                            commandsender.sendMessage(ChatColor.AQUA + "Successfully set lower bound!");
                        }

                        return true;

                
                        
                        
                        
                
            } else  if ((astring[0].equalsIgnoreCase("list") )) {
                
                commandsender.sendMessage("§b§lАрен найдено: " + AM.getAllArenas().size() );
                
                for ( Entry<String, Arena> e : AM.getAllArenas().entrySet() ) {
                    commandsender.sendMessage( "§e" + e.getKey()+" :§5"+ e.getValue().getStateAsString()   );
                }
                
                return true;
                    
                
                
                
                
                
                
                    
            } else  if (astring[0].equalsIgnoreCase("stop") ) {
                    
                    if ((astring.length != 2 )) {
                        commandsender.sendMessage(ChatColor.RED + "Insufficiant Arguments! Proper use of the command goes like this: /ss stop <arena name>");
                    } else {
                        if (AM.getArena(astring[1]) == null) {
                            commandsender.sendMessage(ChatColor.RED + "That arena doesn\'t exist!");
                            return true;
                        }

                       // if ( !AM.getArena(astring[1]).hasStarted() ) {
                       //     commandsender.sendMessage(ChatColor.RED + "Арена не запущена!");
                      //      return true;
                     //   }

                        AM.stopArena(astring[1], (Player) commandsender);
                        commandsender.sendMessage(ChatColor.AQUA + "Arena Stopped");
                        commandsender.sendMessage(ChatColor.RED + "Arena is not in game!");
                    }

                    return true;
                    
                    
                    
                                
                        
                        
                        
                        
                        
                    } else if (astring[0].equalsIgnoreCase("start")) {
                        
                        if ((astring.length != 2  )) {
                            commandsender.sendMessage(ChatColor.RED + "Insufficiant Arguments. Proper use of the command goes like this: /ss start <arena name>");
                        } else {
                            if (AM.getArena(astring[1]) == null) {
                                commandsender.sendMessage(ChatColor.RED + "That arena doesn\'t exist!");
                                return true;
                            }

                            if (AM.getArena(astring[1]).getPlayers().size() < 1) {
                                commandsender.sendMessage(ChatColor.RED + "You can\'t start an arena with no players in it!");
                                return true;
                            }

                            AM.startArenaByName(astring[1]);
                            commandsender.sendMessage(ChatColor.AQUA + "Время до старта уменьшено");
                        }

                        return true;
                        
                        
                        
                        
                        
                    } else if (astring[0].equalsIgnoreCase("posh") ) {
                            if ((astring.length != 2 )) {
                                commandsender.sendMessage(ChatColor.RED + "Insufficiant Arguments. Proper use of the command goes like this: /ss setboundshigh <arena name>");
                            } else {
                                if (AM.getArena(astring[1]) == null) {
                                    commandsender.sendMessage(ChatColor.RED + "Captain, it is illogical to set the parameters of something that doesn\'t exist!");
                                    return true;
                                }

                                if (AM.getArena(astring[1]).getPlayers().size() > 0) {
                                    commandsender.sendMessage(ChatColor.RED + "That arena has players in it. Please stop the game before performing this operation.");
                                    return true;
                                }

                                if (AM.getArena(astring[1]).getBoundsLow() == null) {
                                    AM.setBoundsLow(((Player) commandsender).getLocation(), astring[1]);
                                }

                                AM.setBoundsHigh(((Player) commandsender).getLocation(), astring[1]);
                                commandsender.sendMessage(ChatColor.AQUA + "Successfully set higher bound!");
                            }

                            return true;
                            
                            
                            
                            
                            
                            
                        } else if (astring[0].equalsIgnoreCase("setlobby") ) {
                            if ((astring.length != 2 || !commandsender.hasPermission("supersnake.arenacreation")) && (astring.length != 2 || !commandsender.isOp())) {
                                commandsender.sendMessage(ChatColor.RED + "Insufficiant Arguments. Proper use of the command goes like this: /ss setlobby <arena name>");
                            } else {
                                if (AM.getArena(astring[1]) == null) {
                                    commandsender.sendMessage(ChatColor.RED + "Captain, it is illogical to set the parameters of something that doesn\'t exist!");
                                    return true;
                                }

                                if (AM.getArena(astring[1]).getPlayers().size() > 0) {
                                    commandsender.sendMessage(ChatColor.RED + "That arena has players in it. Please stop the game before performing this operation.");
                                    return true;
                                }

                                AM.setArenaLobby(((Player) commandsender).getLocation(), astring[1]);
                                commandsender.sendMessage(ChatColor.AQUA + "Successfully set arena lobby!");
                            }

                            return true;
                            
                            
                            
                            
                            
                        }  else if (astring[0].equalsIgnoreCase("addspawn") ) {
                            if ((astring.length != 2 )) {
                                commandsender.sendMessage(ChatColor.RED + "Insufficiant Arguments. Proper use of the command goes like this: /ss addspawn <arena name>");
                            } else {
                                if (AM.getArena(astring[1]) == null) {
                                    commandsender.sendMessage(ChatColor.RED + "Captain, it is illogical to set the parameters of something that doesn\'t exist!");
                                    return true;
                                }

                                if (AM.getArena(astring[1]).getPlayers().size() > 0) {
                                    commandsender.sendMessage(ChatColor.RED + "That arena has players in it. Please stop the game before performing this operation.");
                                    return true;
                                }

                                AM.addSpawn(((Player) commandsender).getLocation(), astring[1]);
                                commandsender.sendMessage(ChatColor.AQUA + "You have added a new spawn point at your location!");
                            }

                            return true; 
                           
                            
                            
                            
                            
                            
                            
                            
        }   else if (astring[0].equalsIgnoreCase("setminplayers")) {
            if (astring.length == 3) {
                if (AM.getArena(astring[1]) == null) {
                    commandsender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Messages.joinInvalidArenaMessage));
                    return true;
                }

                int i;

                try {
                    i = Integer.valueOf(astring[2]);
                } catch (Exception exception) {
                    commandsender.sendMessage(ChatColor.RED + "The third argument is not a valid int!");
                    return true;
                }

                AM.getArena(astring[1]).setMinPlayers(i);
                commandsender.sendMessage(ChatColor.AQUA + "Successfully set the min players to " + astring[2] + "!");
            } else {
                commandsender.sendMessage(ChatColor.RED + "Proper usage of the command goes like this: /snake setminplayers <arena name> <int>");
            }

            return true;
            
            
            
            
            
            
        } 

                
                return true;
            }
            
            
            
      /*  } else {
            if (AM.isInGame((Player) commandsender)) {
                AM.removePlayer((Player) commandsender, 0);
                commandsender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Messages.playerLeaveArenaMessage));
            } else {
                commandsender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Messages.playerLeaveNotInArena));
            }

            return true;
        }*/
            
            
    }
