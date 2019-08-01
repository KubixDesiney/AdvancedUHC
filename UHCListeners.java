package euw.uhc.cc;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UHCListeners implements Listener {
	
	private UHC main;
	
	public UHCListeners(UHC main) { this.main = main; }

	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		
		for(Entry<Player, ScoreboardSign> board : main.sb.entrySet()){
			board.getValue().setLine(0,  "Players : " + Bukkit.getOnlinePlayers().size());
		}
		
		Player player = event.getPlayer();
		player.teleport(new Location(Bukkit.getWorld("world"), 0, 5, 0));
		
		if(main.isState(UHCState.GAME) || main.isState(UHCState.GAMEPVP)){
			player.setGameMode(GameMode.SPECTATOR);
			player.sendMessage("Vous êtes spectateur");
			event.setJoinMessage(null);
			return;
		}
		
		if(main.getPlayers().contains(player)) return;
		
		ScoreboardSign board = new ScoreboardSign(player, "§eUHC");
		board.create();
		board.setLine(0, "Players : " + Bukkit.getOnlinePlayers().size());
		board.setLine(1, "Borders : " + Math.round(main.border.getSize()));
		
		main.sb.put(player, board);
		
		main.getPlayers().add(player);
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.setHealth(player.getMaxHealth());
		
		if(main.getPlayers().size() == 2 && main.started == false){
			main.started = true;
			UHCAutoStart start = new UHCAutoStart(main);
			start.runTaskTimer(main, 20, 20);
		}
		
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(main.getPlayers().contains(player)) main.getPlayers().remove(player);
		if(main.sb.containsKey(player)) main.sb.remove(player);
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent event){
		event.setFoodLevel(20);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		if(main.isState(UHCState.LOBBY)){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event){
		if(!main.isState(UHCState.GAMEPVP))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent event){
		if(main.isState(UHCState.LOBBY)){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event){
		
		Entity killer = event.getEntity().getKiller();
		
		if(event.getEntity() instanceof Player){
			
			Player victim = event.getEntity();
			victim.setGameMode(GameMode.SPECTATOR);
			victim.setSpectatorTarget(killer);
			event.setDeathMessage(victim.getName() + " est mort !");
			main.getPlayers().remove(victim);
			
			if(victim.getKiller() instanceof Player){
				Player killp = (Player)killer;
				event.setDeathMessage(killp.getName() +" a tué le joueur " + victim.getName());
			}
			
		}
		
	}

}
