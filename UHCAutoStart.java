package fr.gravenilvec.uhc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class UHCAutoStart extends BukkitRunnable {

	private UHC main;
	private int timer = 15;
	
	public UHCAutoStart(UHC main) { this.main = main; }
	
	@Override
	public void run() {
		
		if(timer == 15 || timer == 10 || timer == 5 || timer == 4 || timer == 3 || timer == 2 || timer == 1){
			for(Player pls : main.getPlayers()){
				pls.setLevel(timer);
			}
			Bukkit.broadcastMessage("Teleportation dans " + timer +" s");
		}
		
		if(main.getPlayers().size() < 2){
			cancel();
			Bukkit.broadcastMessage("Pas assez de joueurs arret du chrono");
		}
		
		if(timer == 0){
			cancel();
			main.setState(UHCState.GAME);
			UHCTask task = new UHCTask(main);
			task.runTaskTimer(main, 20, 20);
			
			launchGame();
			
		}
		
		timer--;
	}

	private void launchGame() {
		
		for(int i = 0; i < main.getPlayers().size(); i++){
			Player p = main.getPlayers().get(i);
			Location l = main.getSpawns().get(i);
			p.teleport(l);
			p.getInventory().clear();
			
			p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET, 1));
			p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
			p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
			p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS, 1));
			
			p.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
			p.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 64));
			p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 3));
			p.getInventory().addItem(new ItemStack(Material.FISHING_ROD, 1));
			
		}
		
	}

}
