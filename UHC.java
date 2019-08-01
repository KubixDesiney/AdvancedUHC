package euw.uhc.cc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class UHC extends JavaPlugin {
	
	private List<Location> spawns = new ArrayList<>();
	private List<Player> players = new ArrayList<>();
	private UHCState currentState;
	private Location spawn;
	public Map<Player, ScoreboardSign> sb = new HashMap<>();
	public boolean started = false;
	public WorldBorder border;
	
	@Override
	public void onEnable() {
		
		saveDefaultConfig();
		
		spawn = new Location(Bukkit.getWorld("world"), getConfig().getDouble("spawn.x"), getConfig().getDouble("spawn.y"), getConfig().getDouble("spawn.z"));
		
		Bukkit.getPluginManager().registerEvents(new UHCListeners(this), this);
		
		WorldCreator wc = new WorldCreator("builduhc");
		long mapSeed = new Random().nextLong();
		wc.seed(mapSeed);
		wc.createWorld();
		
		border = Bukkit.getWorld("builduhc").getWorldBorder();
		border.setCenter(spawn);
		border.setSize(100);
		
		setState(UHCState.LOBBY);
		loadSpawns();
		
		Bukkit.getScheduler().runTaskTimer(this, new Runnable(){

			@Override
			public void run() {
				if(!isState(UHCState.LOBBY)){
					checkWin();
				}
			}
			
		},10,10);
		
	}

	private void loadSpawns() {
		for(String path : getConfig().getStringList("spawns")){
			String[] parse = path.split(",");
			double x = Double.valueOf(parse[0]);
			double y = Double.valueOf(parse[1]);
			double z = Double.valueOf(parse[2]);
			spawns.add(new Location(Bukkit.getWorld("builduhc"), x,y,z));
		}
		
		System.out.println(spawns.size() + " spawns loaded");
	}

	@Override
	public void onDisable() {
		
		for(Player pls : Bukkit.getOnlinePlayers()){
			pls.kickPlayer("Restarting...");
		}
		
		Bukkit.unloadWorld("builduhc", false);
		deleteWorld(new File("builduhc"));
		
	}
	
	private void deleteWorld(File path) {
		System.out.println("Deleting... world");
		if(path.exists()){
			File files[] = path.listFiles();
			
			for(int i = 0; i < files.length; i++){
				if(files[i].isDirectory()){
					deleteWorld(files[i]);
				}else{
					files[i].delete();
				}
			}
			
		}
	}

	private void checkWin() {
		
		if(players.size() == 0){
			Bukkit.reload();
			Bukkit.broadcastMessage("Redemarrage du serveur");
			setState(UHCState.FINISH);
		}
		
		if(players.size() == 1){
			
			Player winner = players.get(0);
			winner.sendMessage("Vous gagnez le jeu");
			setState(UHCState.FINISH);
			Bukkit.getScheduler().runTaskLater(this, new Runnable(){

				@Override
				public void run() {
					Bukkit.broadcastMessage("Fermeture du jeu");
					Bukkit.reload();
				}
				
				
			},100);
			
		}
	}
	
	public void setState(UHCState state){
		this.currentState = state;
	}
	
	public boolean isState(UHCState state){
		return currentState == state;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Location> getSpawns() {
		return spawns;
	}
	
}
