package me.bridgar;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;


/*
 * Encoding:
 *  1::
 *  1:  Quantity 1
 *  2:  Quantity 2
 *  3:  Quantity 3
 *  4:  Quantity 4
 *  5:  Quantity 5
 *  6:  Quantity 6
 *  7:  Quantity 7
 *  8:  Pants or Chest
 *    
 *  2::
 *  1:  Color 1
 *  2:  Color 2
 *  3:  Color 3
 *  4:  Color 4
 *  5:  Color 5
 *  6:  Color 6
 *  7:  Color 7
 *  8:  Color 8
 *
 *  3::  
 *  1:  Color 9
 *  2:  Color 10
 *  3:  Color 11
 *  4:  Color 12
 *  5:  Color 13
 *  6:  Color 14
 *  7:  Color 15
 *  8:  Color 16
 *
 *  4::
 *  1:  Color 17
 *  2:  Color 18
 *  3:  Color 19
 *  4:  Color 20
 *  5:  Color 21
 *  6:  Color 22
 *  7:  Color 23
 *  8:  Color 24
 * 
 *  5:: 
 *  1: Protection Enchant Level 1
 *  2: Protection Enchant Level 2 
 *  3: Protection Enchant Level 3
 *  4: Protection Enchant Level 4
 *  5: Protection Enchant Level 5
 *  6: Protection Enchant Level 6
 *  7: Protection Enchant Level 7
 *  8: Protection Enchant Level 8
 *
 *  6::
 *  1: Protection Enchant Level 9
 *  2: Protection Enchant Level 10 
 *  3: Protection Enchant Level 11
 *  4: Protection Enchant Level 12
 *  5: Protection Enchant Level 13
 *  6: Protection Enchant Level 14
 *  7: Protection Enchant Level 15
 *  8: Protection Enchant Level 16
 * 
 *  7:: 
 *  1: Fire Protection Enchant Level 1
 *  2: Fire Protection Enchant Level 2 
 *  3: Fire Protection Enchant Level 3
 *  4: Fire Protection Enchant Level 4
 *  5: Fire Protection Enchant Level 5
 *  6: Fire Protection Enchant Level 6
 *  7: Fire Protection Enchant Level 7
 *  8: Fire Protection Enchant Level 8
 *
 *  8::
 *  1: Fire Protection Enchant Level 9
 *  2: Fire Protection Enchant Level 10 
 *  3: Fire Protection Enchant Level 11
 *  4: Fire Protection Enchant Level 12
 *  5: Fire Protection Enchant Level 13
 *  6: Fire Protection Enchant Level 14
 *  7: Fire Protection Enchant Level 15
 *  8: Fire Protection Enchant Level 16
 * 
 *  9:: 
 *  1: Feather Falling Enchant Level 1
 *  2: Feather Falling Enchant Level 2 
 *  3: Feather Falling Enchant Level 3
 *  4: Feather Falling Enchant Level 4
 *  5: Feather Falling Enchant Level 5
 *  6: Feather Falling Enchant Level 6
 *  7: Feather Falling Enchant Level 7
 *  8: Feather Falling Enchant Level 8
 *
 *  10::
 *  1: Feather Falling Enchant Level 9
 *  2: Feather Falling Enchant Level 10 
 *  3: Feather Falling Enchant Level 11
 *  4: Feather Falling Enchant Level 12
 *  5: Feather Falling Enchant Level 13
 *  6: Feather Falling Enchant Level 14
 *  7: Feather Falling Enchant Level 15
 *  8: Feather Falling Enchant Level 16
 *
 *  11:: 
 *  1: Blast Protection Enchant Level 1
 *  2: Blast Protection Enchant Level 2 
 *  3: Blast Protection Enchant Level 3
 *  4: Blast Protection Enchant Level 4
 *  5: Blast Protection Enchant Level 5
 *  6: Blast Protection Enchant Level 6
 *  7: Blast Protection Enchant Level 7
 *  8: Blast Protection Enchant Level 8
 *
 *  12::
 *  1: Blast Protection Enchant Level 9
 *  2: Blast Protection Enchant Level 10 
 *  3: Blast Protection Enchant Level 11
 *  4: Blast Protection Enchant Level 12
 *  5: Blast Protection Enchant Level 13
 *  6: Blast Protection Enchant Level 14
 *  7: Blast Protection Enchant Level 15
 *  8: Blast Protection Enchant Level 16
 * 
 *  13:: 
 *  1: Projectile Protection Enchant Level 1
 *  2: Projectile Protection Enchant Level 2 
 *  3: Projectile Protection Enchant Level 3
 *  4: Projectile Protection Enchant Level 4
 *  5: Projectile Protection Enchant Level 5
 *  6: Projectile Protection Enchant Level 6
 *  7: Projectile Protection Enchant Level 7
 *  8: Projectile Protection Enchant Level 8
 *
 *  14::
 *  1: Projectile Protection Enchant Level 9
 *  2: Projectile Protection Enchant Level 10 
 *  3: Projectile Protection Enchant Level 11
 *  4: Projectile Protection Enchant Level 12
 *  5: Projectile Protection Enchant Level 13
 *  6: Projectile Protection Enchant Level 14
 *  7: Projectile Protection Enchant Level 15
 *  8: Projectile Protection Enchant Level 16
 * 
 *  15:: 
 *  1: Respiration Enchant Level 1
 *  2: Respiration Enchant Level 2 
 *  3: Respiration Enchant Level 3
 *  4: Respiration Enchant Level 4
 *  5: Respiration Enchant Level 5
 *  6: Respiration Enchant Level 6
 *  7: Respiration Enchant Level 7
 *  8: Respiration Enchant Level 8
 *
 *  16::
 *  1: Respiration Enchant Level 9
 *  2: Respiration Enchant Level 10 
 *  3: Respiration Enchant Level 11
 *  4: Respiration Enchant Level 12
 *  5: Respiration Enchant Level 13
 *  6: Respiration Enchant Level 14
 *  7: Respiration Enchant Level 15
 *  8: Respiration Enchant Level 16
 * 
 *  17:: 
 *  1: Aqua Affinity Enchant Level 1
 *  2: Aqua Affinity Enchant Level 2 
 *  3: Aqua Affinity Enchant Level 3
 *  4: Aqua Affinity Enchant Level 4
 *  5: Aqua Affinity Enchant Level 5
 *  6: Aqua Affinity Enchant Level 6
 *  7: Aqua Affinity Enchant Level 7
 *  8: Aqua Affinity Enchant Level 8
 *
 *  18::
 *  1: Aqua Affinity Enchant Level 9
 *  2: Aqua Affinity Enchant Level 10 
 *  3: Aqua Affinity Enchant Level 11
 *  4: Aqua Affinity Enchant Level 12
 *  5: Aqua Affinity Enchant Level 13
 *  6: Aqua Affinity Enchant Level 14
 *  7: Aqua Affinity Enchant Level 15
 *  8: Aqua Affinity Enchant Level 16
 * 
 *  19:: 
 *  1: Thorns Enchant Level 1
 *  2: Thorns Enchant Level 2 
 *  3: Thorns Enchant Level 3
 *  4: Thorns Enchant Level 4
 *  5: Thorns Enchant Level 5
 *  6: Thorns Enchant Level 6
 *  7: Thorns Enchant Level 7
 *  8: Thorns Enchant Level 8
 *
 *  20::
 *  1: Thorns Enchant Level 9
 *  2: Thorns Enchant Level 10 
 *  3: Thorns Enchant Level 11
 *  4: Thorns Enchant Level 12
 *  5: Thorns Enchant Level 13
 *  6: Thorns Enchant Level 14
 *  7: Thorns Enchant Level 15
 *  8: Thorns Enchant Level 16
 * 
 *  21:: 
 *  1: Depth Strider Enchant Level 1
 *  2: Depth Strider Enchant Level 2 
 *  3: Depth Strider Enchant Level 3
 *  4: Depth Strider Enchant Level 4
 *  5: Depth Strider Enchant Level 5
 *  6: Depth Strider Enchant Level 6
 *  7: Depth Strider Enchant Level 7
 *  8: Depth Strider Enchant Level 8
 *
 *  22::
 *  1: Depth Strider Enchant Level 9
 *  2: Depth Strider Enchant Level 10 
 *  3: Depth Strider Enchant Level 11
 *  4: Depth Strider Enchant Level 12
 *  5: Depth Strider Enchant Level 13
 *  6: Depth Strider Enchant Level 14
 *  7: Depth Strider Enchant Level 15
 *  8: Depth Strider Enchant Level 16
 *
 *  23:: 
 *  1: Sharpness Enchant Level 1
 *  2: Sharpness Enchant Level 2 
 *  3: Sharpness Enchant Level 3
 *  4: Sharpness Enchant Level 4
 *  5: Sharpness Enchant Level 5
 *  6: Sharpness Enchant Level 6
 *  7: Sharpness Enchant Level 7
 *  8: Sharpness Enchant Level 8
 *
 *  24::
 *  1: Sharpness Enchant Level 9
 *  2: Sharpness Enchant Level 10 
 *  3: Sharpness Enchant Level 11
 *  4: Sharpness Enchant Level 12
 *  5: Sharpness Enchant Level 13
 *  6: Sharpness Enchant Level 14
 *  7: Sharpness Enchant Level 15
 *  8: Sharpness Enchant Level 16
 * 
 *  25:: 
 *  1: Smite Enchant Level 1
 *  2: Smite Enchant Level 2 
 *  3: Smite Enchant Level 3
 *  4: Smite Enchant Level 4
 *  5: Smite Enchant Level 5
 *  6: Smite Enchant Level 6
 *  7: Smite Enchant Level 7
 *  8: Smite Enchant Level 8
 *
 *  26::
 *  1: Smite Enchant Level 9
 *  2: Smite Enchant Level 10 
 *  3: Smite Enchant Level 11
 *  4: Smite Enchant Level 12
 *  5: Smite Enchant Level 13
 *  6: Smite Enchant Level 14
 *  7: Smite Enchant Level 15
 *  8: Smite Enchant Level 16
 * 
 *  27:: 
 *  1: Bane of Arthropods Enchant Level 1
 *  2: Bane of Arthropods Enchant Level 2 
 *  3: Bane of Arthropods Enchant Level 3
 *  4: Bane of Arthropods Enchant Level 4
 *  5: Bane of Arthropods Enchant Level 5
 *  6: Bane of Arthropods Enchant Level 6
 *  7: Bane of Arthropods Enchant Level 7
 *  8: Bane of Arthropods Enchant Level 8
 *
 *  28::
 *  1: Bane of Arthropods Enchant Level 9
 *  2: Bane of Arthropods Enchant Level 10 
 *  3: Bane of Arthropods Enchant Level 11
 *  4: Bane of Arthropods Enchant Level 12
 *  5: Bane of Arthropods Enchant Level 13
 *  6: Bane of Arthropods Enchant Level 14
 *  7: Bane of Arthropods Enchant Level 15
 *  8: Bane of Arthropods Enchant Level 16
  * 
 *  29:: 
 *  1: Knockback Enchant Level 1
 *  2: Knockback Enchant Level 2 
 *  3: Knockback Enchant Level 3
 *  4: Knockback Enchant Level 4
 *  5: Knockback Enchant Level 5
 *  6: Knockback Enchant Level 6
 *  7: Knockback Enchant Level 7
 *  8: Knockback Enchant Level 8
 *
 *  30::
 *  1: Knockback Enchant Level 9
 *  2: Knockback Enchant Level 10 
 *  3: Knockback Enchant Level 11
 *  4: Knockback Enchant Level 12
 *  5: Knockback Enchant Level 13
 *  6: Knockback Enchant Level 14
 *  7: Knockback Enchant Level 15
 *  8: Knockback Enchant Level 16
 * 
 *  31:: 
 *  1: Fire Aspect Enchant Level 1
 *  2: Fire Aspect Enchant Level 2 
 *  3: Fire Aspect Enchant Level 3
 *  4: Fire Aspect Enchant Level 4
 *  5: Fire Aspect Enchant Level 5
 *  6: Fire Aspect Enchant Level 6
 *  7: Fire Aspect Enchant Level 7
 *  8: Fire Aspect Enchant Level 8
 *
 *  32::
 *  1: Fire Aspect Enchant Level 9
 *  2: Fire Aspect Enchant Level 10 
 *  3: Fire Aspect Enchant Level 11
 *  4: Fire Aspect Enchant Level 12
 *  5: Fire Aspect Enchant Level 13
 *  6: Fire Aspect Enchant Level 14
 *  7: Fire Aspect Enchant Level 15
 *  8: Fire Aspect Enchant Level 16
 * 
 *  33:: 
 *  1: Looting Enchant Level 1
 *  2: Looting Enchant Level 2 
 *  3: Looting Enchant Level 3
 *  4: Looting Enchant Level 4
 *  5: Looting Enchant Level 5
 *  6: Looting Enchant Level 6
 *  7: Looting Enchant Level 7
 *  8: Looting Enchant Level 8
 *
 *  34::
 *  1: Looting Enchant Level 9
 *  2: Looting Enchant Level 10 
 *  3: Looting Enchant Level 11
 *  4: Looting Enchant Level 12
 *  5: Looting Enchant Level 13
 *  6: Looting Enchant Level 14
 *  7: Looting Enchant Level 15
 *  8: Looting Enchant Level 16
 *
 *  35:: 
 *  1: Efficiency Enchant Level 1
 *  2: Efficiency Enchant Level 2 
 *  3: Efficiency Enchant Level 3
 *  4: Efficiency Enchant Level 4
 *  5: Efficiency Enchant Level 5
 *  6: Efficiency Enchant Level 6
 *  7: Efficiency Enchant Level 7
 *  8: Efficiency Enchant Level 8
 *
 *  36::
 *  1: Efficiency Enchant Level 9
 *  2: Efficiency Enchant Level 10 
 *  3: Efficiency Enchant Level 11
 *  4: Efficiency Enchant Level 12
 *  5: Efficiency Enchant Level 13
 *  6: Efficiency Enchant Level 14
 *  7: Efficiency Enchant Level 15
 *  8: Efficiency Enchant Level 16
 * 
 *  37:: 
 *  1: Silk Touch Enchant Level 1
 *  2: Silk Touch Enchant Level 2 
 *  3: Silk Touch Enchant Level 3
 *  4: Silk Touch Enchant Level 4
 *  5: Silk Touch Enchant Level 5
 *  6: Silk Touch Enchant Level 6
 *  7: Silk Touch Enchant Level 7
 *  8: Silk Touch Enchant Level 8
 *
 *  38::
 *  1: Silk Touch Enchant Level 9
 *  2: Silk Touch Enchant Level 10 
 *  3: Silk Touch Enchant Level 11
 *  4: Silk Touch Enchant Level 12
 *  5: Silk Touch Enchant Level 13
 *  6: Silk Touch Enchant Level 14
 *  7: Silk Touch Enchant Level 15
 *  8: Silk Touch Enchant Level 16
 * 
 *  39:: 
 *  1: Unbreaking Enchant Level 1
 *  2: Unbreaking Enchant Level 2 
 *  3: Unbreaking Enchant Level 3
 *  4: Unbreaking Enchant Level 4
 *  5: Unbreaking Enchant Level 5
 *  6: Unbreaking Enchant Level 6
 *  7: Unbreaking Enchant Level 7
 *  8: Unbreaking Enchant Level 8
 *
 *  40::
 *  1: Unbreaking Enchant Level 9
 *  2: Unbreaking Enchant Level 10 
 *  3: Unbreaking Enchant Level 11
 *  4: Unbreaking Enchant Level 12
 *  5: Unbreaking Enchant Level 13
 *  6: Unbreaking Enchant Level 14
 *  7: Unbreaking Enchant Level 15
 *  8: Unbreaking Enchant Level 16
 * 
 *  41:: 
 *  1: Fortune Enchant Level 1
 *  2: Fortune Enchant Level 2 
 *  3: Fortune Enchant Level 3
 *  4: Fortune Enchant Level 4
 *  5: Fortune Enchant Level 5
 *  6: Fortune Enchant Level 6
 *  7: Fortune Enchant Level 7
 *  8: Fortune Enchant Level 8
 *
 *  42::
 *  1: Fortune Enchant Level 9
 *  2: Fortune Enchant Level 10 
 *  3: Fortune Enchant Level 11
 *  4: Fortune Enchant Level 12
 *  5: Fortune Enchant Level 13
 *  6: Fortune Enchant Level 14
 *  7: Fortune Enchant Level 15
 *  8: Fortune Enchant Level 16
 * 
 *  43:: 
 *  1: Power Enchant Level 1
 *  2: Power Enchant Level 2 
 *  3: Power Enchant Level 3
 *  4: Power Enchant Level 4
 *  5: Power Enchant Level 5
 *  6: Power Enchant Level 6
 *  7: Power Enchant Level 7
 *  8: Power Enchant Level 8
 *
 *  44::
 *  1: Power Enchant Level 9
 *  2: Power Enchant Level 10 
 *  3: Power Enchant Level 11
 *  4: Power Enchant Level 12
 *  5: Power Enchant Level 13
 *  6: Power Enchant Level 14
 *  7: Power Enchant Level 15
 *  8: Power Enchant Level 16
 * 
 *  45:: 
 *  1: Punch Enchant Level 1
 *  2: Punch Enchant Level 2 
 *  3: Punch Enchant Level 3
 *  4: Punch Enchant Level 4
 *  5: Punch Enchant Level 5
 *  6: Punch Enchant Level 6
 *  7: Punch Enchant Level 7
 *  8: Punch Enchant Level 8
 *
 *  46::
 *  1: Punch Enchant Level 9
 *  2: Punch Enchant Level 10 
 *  3: Punch Enchant Level 11
 *  4: Punch Enchant Level 12
 *  5: Punch Enchant Level 13
 *  6: Punch Enchant Level 14
 *  7: Punch Enchant Level 15
 *  8: Punch Enchant Level 16
 *
 *  47:: 
 *  1: Flame Enchant Level 1
 *  2: Flame Enchant Level 2 
 *  3: Flame Enchant Level 3
 *  4: Flame Enchant Level 4
 *  5: Flame Enchant Level 5
 *  6: Flame Enchant Level 6
 *  7: Flame Enchant Level 7
 *  8: Flame Enchant Level 8
 *
 *  48::
 *  1: Flame Enchant Level 9
 *  2: Flame Enchant Level 10 
 *  3: Flame Enchant Level 11
 *  4: Flame Enchant Level 12
 *  5: Flame Enchant Level 13
 *  6: Flame Enchant Level 14
 *  7: Flame Enchant Level 15
 *  8: Flame Enchant Level 16
 * 
 *  49:: 
 *  1: Infinity Enchant Level 1
 *  2: Infinity Enchant Level 2 
 *  3: Infinity Enchant Level 3
 *  4: Infinity Enchant Level 4
 *  5: Infinity Enchant Level 5
 *  6: Infinity Enchant Level 6
 *  7: Infinity Enchant Level 7
 *  8: Infinity Enchant Level 8
 *
 *  50::
 *  1: Infinity Enchant Level 9
 *  2: Infinity Enchant Level 10 
 *  3: Infinity Enchant Level 11
 *  4: Infinity Enchant Level 12
 *  5: Infinity Enchant Level 13
 *  6: Infinity Enchant Level 14
 *  7: Infinity Enchant Level 15
 *  8: Infinity Enchant Level 16
 * 
 *  51:: 
 *  1: Luck of the Sea Enchant Level 1
 *  2: Luck of the Sea Enchant Level 2 
 *  3: Luck of the Sea Enchant Level 3
 *  4: Luck of the Sea Enchant Level 4
 *  5: Luck of the Sea Enchant Level 5
 *  6: Luck of the Sea Enchant Level 6
 *  7: Luck of the Sea Enchant Level 7
 *  8: Luck of the Sea Enchant Level 8
 *
 *  52::
 *  1: Luck of the Sea Enchant Level 9
 *  2: Luck of the Sea Enchant Level 10 
 *  3: Luck of the Sea Enchant Level 11
 *  4: Luck of the Sea Enchant Level 12
 *  5: Luck of the Sea Enchant Level 13
 *  6: Luck of the Sea Enchant Level 14
 *  7: Luck of the Sea Enchant Level 15
 *  8: Luck of the Sea Enchant Level 16
 * 
 *  53:: 
 *  1: Lure Enchant Level 1
 *  2: Lure Enchant Level 2 
 *  3: Lure Enchant Level 3
 *  4: Lure Enchant Level 4
 *  5: Lure Enchant Level 5
 *  6: Lure Enchant Level 6
 *  7: Lure Enchant Level 7
 *  8: Lure Enchant Level 8
 *
 *  54::
 *  1: Lure Enchant Level 9
 *  2: Lure Enchant Level 10 
 *  3: Lure Enchant Level 11
 *  4: Lure Enchant Level 12
 *  5: Lure Enchant Level 13
 *  6: Lure Enchant Level 14
 *  7: Lure Enchant Level 15
 *  8: Lure Enchant Level 16
 * 
 */


public class Transcoder {
	
	static final int PROTECTION = 0;
	static final int FIRE_PROTECTION = 1;
	static final int FEATHER_FALLING = 2;
	static final int BLAST_PROTECTION = 3;
	static final int PROJECTILE_PROTECTION = 4;
	static final int RESPIRATION = 5;
	static final int AQUA_AFFINITY = 6;
	static final int THORNS = 7;
	static final int DEPTH_STRIDER = 8;
	static final int SHARPNESS = 16;
	static final int SMITE = 17;
	static final int BANE_OF_ARTHROPODS = 18;
	static final int KNOCKBACK = 19;
	static final int FIRE_ASPECT = 20;
	static final int LOOTING = 21;
	static final int EFFICIENCY = 32;
	static final int SILK_TOUCH = 33;
	static final int UNBREAKING = 34;
	static final int FORTUNE = 35;
	static final int POWER = 48;
	static final int PUNCH = 49;
	static final int FLAME = 50;
	static final int INFINITY = 51;
	static final int LUCK_OF_THE_SEA = 61;
	static final int LURE = 62;
	
	public static final int BYTES_PER_SLOT = 54;
	public static final int SLOTS_PER_CHEST = 27;
	
	//bytes must be precisely 54 bytes
	public ItemStack encodeItemStack(ArrayList<Byte> bytes) {
		if(bytes.size() != BYTES_PER_SLOT) return null;
		
		Byte first = bytes.get(0);
		
		//first bit in bytes:
		//zero is chest
		//one is pants
		boolean isChest = (first & 0x01) == 0;
		
		int quantity = first | 0x01 ;
		
		Material mat;
		
		if(isChest) {
			mat = Material.LEATHER_CHESTPLATE;
		} else {
			mat = Material.LEATHER_LEGGINGS;
		}
		
		ItemStack item = new ItemStack(mat, quantity);
		
		LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
		
		//Set Color
		lam.setColor(Color.fromRGB(bytes.get(1) & 0xff, bytes.get(2) & 0xff, bytes.get(3) & 0xff));
		lam.setDisplayName("PickleCannon");
		
		item.setItemMeta(lam);
		
		handleEnchants(true, item, bytes);
		
		return item;
	}

	public ArrayList<Byte> decodeItemStack(ItemStack item) {
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		
		int quantity = item.getAmount() >> 1;
		
		boolean isChest = item.getType() == Material.LEATHER_CHESTPLATE;
		
		Byte first;
		if(isChest) {
			first = (byte)(quantity << 1);
		} else {
			first = (byte)((quantity << 1) | 0x01);
		}
		
		bytes.add(first);
		
		LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
		
		Color c = lam.getColor();
		
		bytes.add((byte) c.getRed());
		bytes.add((byte) c.getGreen());
		bytes.add((byte) c.getBlue());

		handleEnchants(false, item, bytes);
		
		return bytes;
	}
	
	private void applyEnchant(boolean isEncoding, ItemStack item, ArrayList<Byte> bytes, int index, Enchantment e) {
		if(e == null) {
			Bukkit.broadcastMessage("Null enchant index " + index);
			return;
		}
		
		if(isEncoding) {
			LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
			int level = (bytes.get(index) << 8) | (bytes.get(index + 1) & 0x00FF);
			lam.addEnchant(e, level, true);
			item.setItemMeta(lam);
		} else {
			int level = item.getEnchantmentLevel(e);
			bytes.add((byte) ((level >> 8) & 0xFF));
			bytes.add((byte) (level & 0xFF));
		}
	}
	
	private void handleEnchants(boolean isEncoding, ItemStack item, ArrayList<Byte> bytes) {
		Enchantment e;
		
		e = Enchantment.getById(PROTECTION);
		applyEnchant(isEncoding, item, bytes, 4, e);
		
		e = Enchantment.getById(FIRE_PROTECTION);
		applyEnchant(isEncoding, item, bytes, 6, e);
		
		e = Enchantment.getById(FEATHER_FALLING);
		applyEnchant(isEncoding, item, bytes, 8, e);
		
		e = Enchantment.getById(BLAST_PROTECTION);
		applyEnchant(isEncoding, item, bytes, 10, e);
		
		e = Enchantment.getById(PROJECTILE_PROTECTION);
		applyEnchant(isEncoding, item, bytes, 12, e);
		
		e = Enchantment.getById(RESPIRATION);
		applyEnchant(isEncoding, item, bytes, 14, e);
		
		e = Enchantment.getById(AQUA_AFFINITY);
		applyEnchant(isEncoding, item, bytes, 16, e);
		
		e = Enchantment.getById(THORNS);
		applyEnchant(isEncoding, item, bytes, 18, e);
		
		e = Enchantment.getById(DEPTH_STRIDER);
		applyEnchant(isEncoding, item, bytes, 20, e);
		
		e = Enchantment.getById(SHARPNESS);
		applyEnchant(isEncoding, item, bytes, 22, e);
		
		e = Enchantment.getById(SMITE);
		applyEnchant(isEncoding, item, bytes, 24, e);
		
		e = Enchantment.getById(BANE_OF_ARTHROPODS);
		applyEnchant(isEncoding, item, bytes, 26, e);
		
		e = Enchantment.getById(KNOCKBACK);
		applyEnchant(isEncoding, item, bytes, 28, e);
		
		e = Enchantment.getById(FIRE_ASPECT);
		applyEnchant(isEncoding, item, bytes, 30, e);
		
		e = Enchantment.getById(LOOTING);
		applyEnchant(isEncoding, item, bytes, 32, e);
		
		e = Enchantment.getById(EFFICIENCY);
		applyEnchant(isEncoding, item, bytes, 34, e);
		
		e = Enchantment.getById(SILK_TOUCH);
		applyEnchant(isEncoding, item, bytes, 36, e);
		
		e = Enchantment.getById(UNBREAKING);
		applyEnchant(isEncoding, item, bytes, 38, e);
		
		e = Enchantment.getById(FORTUNE);
		applyEnchant(isEncoding, item, bytes, 40, e);
		
		e = Enchantment.getById(POWER);
		applyEnchant(isEncoding, item, bytes, 42, e);
		
		e = Enchantment.getById(PUNCH);
		applyEnchant(isEncoding, item, bytes, 44, e);
		
		e = Enchantment.getById(FLAME);
		applyEnchant(isEncoding, item, bytes, 46, e);
		
		e = Enchantment.getById(INFINITY);
		applyEnchant(isEncoding, item, bytes, 48, e);
		
		e = Enchantment.getById(LUCK_OF_THE_SEA);
		applyEnchant(isEncoding, item, bytes, 50, e);
		
		e = Enchantment.getById(LURE);
		applyEnchant(isEncoding, item, bytes, 52, e);
	}
}
