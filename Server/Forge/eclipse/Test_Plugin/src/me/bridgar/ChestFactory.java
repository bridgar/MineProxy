package me.bridgar;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


/*
 * 
 * Chest Order:
 * (+0,0,+1)
 * (+1,0,+0)
 * (+0,0,-1)
 * (-1,0,+0)
 * (+0,1,+1)
 * (+1,1,+0)
 * (+0,1,-1)
 * (-1,1,+0)
 * (+0,2,+1)
 * (+1,2,+0)
 * (+0,2,-1)
 * (-1,2,+0)
 * ...
 * 
 * X and Z are relative to player position
 */

public class ChestFactory {
	private Transcoder t;
	
	private static final int[] X_PRESET = {0,1,0,-1};
	private static final int[] Z_PRESET = {1,0,-1,0};
	
	public ChestFactory(Transcoder t) {
		this.t= t;
	}
	
	public void sendBytes(byte[] bytes, Player player) {
		//TODO
		int totalBytes = bytes.length;
		
		double numSlotsD = totalBytes / t.BYTES_PER_SLOT;
		numSlotsD = Math.ceil(numSlotsD);
		int numSlots = (int) numSlotsD;
		
		double numChestsD = numSlots / t.SLOTS_PER_CHEST;
		numChestsD = Math.ceil(numChestsD);
		int numChests = (int) numChestsD;
		
		Location pl = player.getLocation();
		pl.setY(0);
		
		
		for(int chestNum = 0; chestNum < numChests; chestNum++) {
			Location bl = pl.add(X_PRESET[chestNum%4], chestNum/4, Z_PRESET[chestNum%4]);
			
			Block block = bl.getBlock();
			block.setType(Material.CHEST);
			Chest c = (Chest)block.getState();
			Inventory i = c.getBlockInventory();
			ItemStack[] contents = new ItemStack[t.SLOTS_PER_CHEST];
			for(int slotNum = 0; slotNum < t.SLOTS_PER_CHEST && slotNum + chestNum * t.SLOTS_PER_CHEST < numSlots; slotNum++) {
				if((chestNum * t.SLOTS_PER_CHEST + slotNum) * t.BYTES_PER_SLOT >= totalBytes) {
					
					//TODO
					
					
				}
				
				
				//TODO
			}
			i.setContents(contents);
		}
	}
}
