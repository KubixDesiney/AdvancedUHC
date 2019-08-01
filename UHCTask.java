package euw.uhc.cc;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UHCTask extends BukkitRunnable {
	
	private UHC main;
	private int timer = 5;
	
	public UHCTask(UHC main) { this.main = main; }

	@Override
	public void run() {
		
		if(timer == 5 || timer == 4 || timer == 3 || timer == 2 || timer == 1){
			for(Player pls : main.getPlayers()){
				pls.setLevel(timer);
			}
			Bukkit.broadcastMessage("Pvp dans " + timer +" s");
		}
		
		if(timer == 0){
			main.setState(UHCState.GAMEPVP);
			Bukkit.broadcastMessage("§cPvP On !");
		}
		
		if(main.border.getSize() > 25){
			main.border.setSize(main.border.getSize() - 1.0);
			
			for(Entry<Player, ScoreboardSign> board : main.sb.entrySet()){
				board.getValue().setLine(1,"Borders : " + Math.round(main.border.getSize()));
			}
		}
		
		timer--;
	}

}
