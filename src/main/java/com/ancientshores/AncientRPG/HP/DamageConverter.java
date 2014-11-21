package com.ancientshores.AncientRPG.HP;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.ancientshores.AncientRPG.AncientRPG;
import com.ancientshores.AncientRPG.PlayerData;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DamageConverter {
	/* ===
	 * Damage config
	 * and
	 * Armor config
	 * ===
	 */ 
	private static FileConfiguration config;
	
	// ===
	// Methods
	// ===

	@SuppressWarnings("deprecation")
	public static double convertDamageByEventForCreatures(EntityDamageEvent event) {
		CreatureHp hps = CreatureHp.getCreatureHpByEntity((LivingEntity) event.getEntity());
		switch (event.getCause()) {
			case FIRE_TICK: case FIRE:
				if (Math.abs(hps.lastFireDamage - System.currentTimeMillis()) >= 1000) {
					hps.lastFireDamage = System.currentTimeMillis();
					return config.getDouble("HP.damage of fire");
				} else {
					return 0;
				}
			case MAGIC:
				if (event instanceof EntityDamageByEntityEvent) {
					Entity e = ((EntityDamageByEntityEvent) event).getDamager();
					if (e instanceof ThrownPotion) {
						ThrownPotion p = (ThrownPotion) e;
						for (PotionEffect pe : p.getEffects()) {
							if (pe.getType().equals(PotionEffectType.HARM)) return config.getDouble("HP.damage per harm potion per tier") * (pe.getAmplifier() + 1);
							if (pe.getType().equals(PotionEffectType.POISON)) return config.getDouble("HP.posion potion damage per tick per tier") * (pe.getAmplifier() + 1);			
						}
					} else if (e instanceof Potion) {
						Potion p = (Potion) e;
						for (PotionEffect pe : p.getEffects())
							if (pe.getType().equals(PotionEffectType.HARM)) return config.getDouble("HP.damage per harm potion per tier") * (pe.getAmplifier() + 1);
					}
				}
			case POISON:
				if (event instanceof EntityDamageByEntityEvent) {
					Entity e = ((EntityDamageByEntityEvent) event).getDamager();
					if (e instanceof ThrownPotion) {
						ThrownPotion p = (ThrownPotion) e;
						for (PotionEffect pe : p.getEffects()) {
							if (pe.getType().equals(PotionEffectType.HARM)) return config.getDouble("HP.damage per harm potion per tier") * (pe.getAmplifier() + 1);	
							if (pe.getType().equals(PotionEffectType.POISON)) return config.getDouble("HP.posion potion damage per tick per tier") * (pe.getAmplifier() + 1);
						}
					}
				}
				return config.getDouble("HP.posion potion damage per tick per tier");
			case BLOCK_EXPLOSION:
				double percentb = ((double) event.getDamage() / 65);
				return Math.round(config.getDouble("HP.damage of TNT") * percentb);
			case STARVATION:
				return config.getDouble("HP.damage of starvation");
			case FALL:
				if (event.getEntity().getFallDistance() >= config.getDouble("HP.first block fall damage")) {
					return (config.getDouble("HP.damage of fall per block") * (Math.round(event.getEntity().getFallDistance()) - config.getDouble("HP.first block fall damage")));
				}
				return 0;
			case ENTITY_EXPLOSION:
				if (event instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent mEvent = (EntityDamageByEntityEvent) event;
					if (mEvent.getDamager() instanceof WitherSkull) {
						double percente = (event.getDamage() / (double) 49);
						return config.getDouble("HP.damage of a wither skull") * percente;
					} else if (mEvent.getDamager() instanceof SmallFireball) {
						double percente = (event.getDamage() / (double) 17);
						return config.getDouble("HP.damage of a small fireball") * percente;
					} else if (mEvent.getDamager() instanceof Fireball) {
						double percente = (event.getDamage() / (double) 7);
						return config.getDouble("HP.damage of a fireball") * percente;
					}
				}
				double percente = ((double) event.getDamage() / 49);
				return config.getDouble("HP.damage of a creeper") * percente;
			case DROWNING:
				return config.getDouble("HP.damage of drowning");
			case CONTACT:
				if (Math.abs(hps.lastCactusDamage - System.currentTimeMillis()) >= 1000) {
					hps.lastCactusDamage = System.currentTimeMillis();
					return config.getDouble("HP.damage of a Cactus per second");
				} else return 0;
			case LAVA:
				if (Math.abs(hps.lastLavaDamage - System.currentTimeMillis()) >= 1000) {
					hps.lastLavaDamage = System.currentTimeMillis();
					return config.getDouble("HP.damage of lava per second");
				}
				break;
			case PROJECTILE:
				if (event instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) event;
					int id = damageByEntityEvent.getDamager().getType().getTypeId();
					if (id == EntityType.ARROW.getTypeId()) {
						if (((Arrow) damageByEntityEvent.getDamager()).getShooter() instanceof Player) return getBowDamage((Player) ((Arrow) damageByEntityEvent.getDamager()).getShooter());
						return config.getDouble("HP.damage of an arrow");
					} else if (id == EntityType.SNOWBALL.getTypeId()) return config.getDouble("HP.damage of a snowball");
					 else if (id == EntityType.EGG.getTypeId()) return config.getDouble("HP.damage of an egg");
					 else if (id == EntityType.FIREBALL.getTypeId()) return config.getDouble("HP.damage of a fireball");
					 else if (id == EntityType.SMALL_FIREBALL.getTypeId()) return config.getDouble("HP.damage of a small fireball");
					 else if (id == EntityType.WITHER_SKULL.getTypeId()) return config.getDouble("HP.damage of a wither skull");
					break;
				}
			case LIGHTNING:
				return config.getDouble("HP.damage of a lightning");
			case CUSTOM:
				return event.getDamage();
			case WITHER:
				return config.getDouble("HP.damage of wither effect");
			case ENTITY_ATTACK: {
				if (Math.abs(hps.lastAttackDamage - System.currentTimeMillis()) < CreatureHp.getMinimumTimeBetweenAttacks()) {
					return 0;
				}
				hps.lastAttackDamage = System.currentTimeMillis();
				EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) event;
				Entity c = damageByEntityEvent.getDamager();
				if (c instanceof CaveSpider) return config.getDouble("HP.damage of a cave spider");
				else if (c instanceof Spider) return config.getDouble("HP.damage of a spider");
				else if (c instanceof PigZombie) return config.getDouble("HP.damage of a pig zombie");
				else if (c instanceof EnderDragon) return config.getDouble("HP.damage of an ender dragon");
				else if (c instanceof Skeleton) return getDamageOfItem((LivingEntity) c, 0);
				else if (c instanceof Creeper) return Math.round(config.getDouble("HP.damage of a creeper") * event.getDamage() / 49);
				else if (c instanceof Ghast) return config.getDouble("HP.damage of a ghast");
				else if (c instanceof Enderman) return config.getDouble("HP.damage of an enderman");
				else if (c instanceof Giant) return config.getDouble("HP.damage of a giant");
				else if (c instanceof Wolf) return config.getDouble("HP.damage of a wolf");
				else if (c instanceof MagmaCube) return config.getDouble("HP.damage of a magma cube");
				else if (c instanceof Slime) return config.getDouble("HP.damage of a slime");
				else if (c instanceof Ocelot) return config.getDouble("HP.damage of a ocelot");
				else if (c instanceof Snowman) return config.getDouble("HP.damage of a snowman");
				else if (c instanceof IronGolem) return config.getDouble("HP.damage of an iron golem");
				else if (c instanceof Silverfish) return config.getDouble("HP.damage of a silverfish");
				else if (c instanceof Zombie) {
					Zombie z = (Zombie) c;
					if (z.isVillager()) {
						if (z.isBaby()) {
							return getDamageOfItem((LivingEntity) c, config.getDouble("HP.damage of a villager baby zombie"));
						} else {
							return getDamageOfItem((LivingEntity) c, config.getDouble("HP.damage of a villager zombie"));
						}
					} else {
						if (z.isBaby()) {
							return getDamageOfItem((LivingEntity) c, config.getDouble("HP.damage of a baby zombie"));
						} else {
							return getDamageOfItem((LivingEntity) c, config.getDouble("HP.damage of a zombie"));
						}
					}
				} else if (c instanceof Player) {
					Player p = (Player) c;
					switch (p.getItemInHand().getType()) {
						case IRON_SWORD:
							return getDamageOfEnchantementAndPotion(p, config.getDouble("HP.damage of an iron sword"));
						case STONE_SWORD:
							return getDamageOfEnchantementAndPotion(p, config.getDouble("HP.damage of a stone sword"));
						case DIAMOND_SWORD:
							return getDamageOfEnchantementAndPotion(p, config.getDouble("HP.damage of a diamond sword"));
						case GOLD_SWORD:
							return getDamageOfEnchantementAndPotion(p, config.getDouble("HP.damage of a gold sword"));
						case WOOD_SWORD:
							return getDamageOfEnchantementAndPotion(p, config.getDouble("HP.damage of a wood sword"));
						case IRON_AXE:
							return getDamageOfEnchantementAndPotion(p, config.getDouble("HP.damage of an iron axe"));
						case STONE_AXE:
							return getDamageOfEnchantementAndPotion(p, config.getDouble("HP.damage of a stone axe"));
						case DIAMOND_AXE:
							return getDamageOfEnchantementAndPotion(p, config.getDouble("HP.damage of a diamond axe"));
						case GOLD_AXE:
							return getDamageOfEnchantementAndPotion(p, config.getDouble("HP.damage of a gold axe"));
						case WOOD_AXE:
							return getDamageOfEnchantementAndPotion(p, config.getDouble("HP.damage of a wood axe"));
						default:
							return getDamageOfEnchantementAndPotion(p, config.getDouble("HP.damage of fists"));
					}
				}
			}
			default:
				break;
		}
		return 0;
	}

	public static double convertDamageByCreature(LivingEntity c, Player mPlayer, double defaultHP, EntityDamageEvent e) {
		PlayerData.getPlayerData(mPlayer.getUniqueId()).getHpsystem().lastAttackDamage = System.currentTimeMillis();
		if (c instanceof CaveSpider) return reduceDamageByArmor(config.getDouble("HP.damage of a cave spider"), mPlayer);
		else if (c instanceof Spider) return reduceDamageByArmor(config.getDouble("HP.damage of a spider"), mPlayer);
		else if (c instanceof PigZombie) return reduceDamageByArmor(config.getDouble("HP.damage of a pig zombie"), mPlayer);
		else if (c instanceof EnderDragon) return reduceDamageByArmor(config.getDouble("HP.damage of an ender dragon"), mPlayer);
		else if (c instanceof Ghast) return reduceDamageByArmor(config.getDouble("HP.damage of a ghat"), mPlayer);
		else if (c instanceof Enderman) return reduceDamageByArmor(config.getDouble("HP.damage of an enderman"), mPlayer);
		else if (c instanceof Giant) return reduceDamageByArmor(config.getDouble("HP.damage of a giant"), mPlayer);
		else if (c instanceof Wolf) return reduceDamageByArmor(config.getDouble("HP.damage of a wolf"), mPlayer);
		else if (c instanceof MagmaCube) return reduceDamageByArmor(config.getDouble("HP.damage of a magma cube"), mPlayer);
		else if (c instanceof Slime) return reduceDamageByArmor(config.getDouble("HP.damage of a slime"), mPlayer);
		else if (c instanceof Ocelot) return reduceDamageByArmor(config.getDouble("HP.damage of a ocelot"), mPlayer);
		else if (c instanceof Snowman) return reduceDamageByArmor(config.getDouble("HP.damage of a snowman"), mPlayer);
		else if (c instanceof IronGolem) return reduceDamageByArmor(config.getDouble("HP.damage of an iron golem"), mPlayer);
		else if (c instanceof Silverfish) return reduceDamageByArmor(config.getDouble("HP.damage of a silverfish"), mPlayer);
		else if (c instanceof Skeleton) return reduceDamageByArmor(getDamageOfItem(c, 0), mPlayer);
		else if (c instanceof Zombie) {
			Zombie z = (Zombie) c;
			if (z.isVillager()) {
				if (z.isBaby()) {
					return reduceDamageByArmor(getDamageOfItem(c, config.getDouble("HP.damage of a villager baby zombie")), mPlayer);
				} else {
					return reduceDamageByArmor(getDamageOfItem(c, config.getDouble("HP.damage of a villager zombie")), mPlayer);
				}
			} else {
				if (z.isBaby()) {
					return reduceDamageByArmor(getDamageOfItem(c, config.getDouble("HP.damage of a baby zombie")), mPlayer);
				} else {
					return reduceDamageByArmor(getDamageOfItem(c, config.getDouble("HP.damage of a zombie")), mPlayer);
				}
			}
		} else if (c instanceof Creeper) {
			if (e.getCause() != DamageCause.ENTITY_ATTACK) {
				return 0;
			}
			double distance = c.getLocation().distance(mPlayer.getLocation());
			if (distance <= 1) return config.getDouble("HP.damage of a creeper");
			
			return (config.getDouble("HP.damage of a creeper")) / Math.sqrt(distance);
		}
		return AncientRPGHP.getHpByMinecraftDamage(mPlayer.getUniqueId(), defaultHP);
	}

	public static boolean isEnabledInWorld(World w) {
		if (w == null) return false;
		if (!isEnabled()) return false;
		
		List<String> worlds = config.getStringList("HP.HPsystem enabled world");
		
		if (worlds == null || worlds.size() == 0) return true; // if enabled and no worlds in list its everywhere enabled
		if (worlds.size() == 1 && worlds.get(0).equalsIgnoreCase("")) return true; // if list is not empty but only has a blank line
		for (String s : worlds) 
			if (w.getName().equalsIgnoreCase(s)) return true;
		return false;
	}

	@SuppressWarnings("deprecation")
	public static double convertDamageByCause(DamageCause c, Player p, double damage, EntityDamageEvent event) {
		PlayerData pd = PlayerData.getPlayerData(p.getUniqueId());
		switch (c) {
			case FIRE_TICK: case FIRE:
				if (Math.abs(pd.lastFireDamage - System.currentTimeMillis()) >= 1000) {
					pd.lastFireDamage = System.currentTimeMillis();
					return reduceDamageByArmor(config.getDouble("HP.damage of fire"), p);
				} else return 0;
			case BLOCK_EXPLOSION:
				double percentb = ((double) damage / 65);
				return reduceDamageByArmor(Math.round(config.getDouble("HP.damage of tnt") * percentb), p);
			case STARVATION:
				return config.getDouble("HP.damage of starvation");
			case FALL:
				if (p.getFallDistance() >= config.getDouble("HP.first block fall damage")) {
					return (config.getDouble("HP.damage of fall per block") * (Math.round(p.getFallDistance()) - config.getDouble("HP.first block fall damage")));
				}
				return 0;
			case ENTITY_EXPLOSION:
				double percente = ((double) event.getDamage() / 49);
				if (event instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent mEvent = (EntityDamageByEntityEvent) event;
					if (mEvent.getDamager() instanceof WitherSkull) return config.getDouble("HP.damage of a wither skull") * percente;
					else if (mEvent.getDamager() instanceof SmallFireball) return config.getDouble("HP.damage of a small fireball") * percente;
					else if (mEvent.getDamager() instanceof Fireball) return config.getDouble("HP.damage of fireball") * percente;
				}
				return Math.round(config.getDouble("HP.damage of a creeper") * percente);
			case DROWNING:
				return config.getDouble("HP.damage of drowning");
			case CONTACT:
				if (Math.abs(pd.lastCactusDamage - System.currentTimeMillis()) >= 1000) {
					pd.lastCactusDamage = System.currentTimeMillis();
					return reduceDamageByArmor(config.getDouble("HP.damage of a Cactus per second"), p);
				} else return 0;
			case LAVA:
				if (Math.abs(pd.lastLavaDamage - System.currentTimeMillis()) >= 1000) {
					pd.lastLavaDamage = System.currentTimeMillis();
					return reduceDamageByArmor(config.getDouble("HP.damage of lava per second"), p);
				} else return 0;
			case VOID:
				return Double.MAX_VALUE;
			case PROJECTILE:
				if (event instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) event;
					short id = damageByEntityEvent.getDamager().getType().getTypeId();
					if (id == EntityType.ARROW.getTypeId()) {
						if (((Arrow) damageByEntityEvent.getDamager()).getShooter() instanceof Player) return reduceDamageByArmor(getBowDamage((Player) ((Arrow) damageByEntityEvent.getDamager()).getShooter()), p);
						return reduceDamageByArmor(config.getDouble("HP.damage of an arrow"), p);
					} else if (id == EntityType.SNOWBALL.getTypeId()) return reduceDamageByArmor(config.getDouble("HP.damage of a snowball"), p);
					else if (damageByEntityEvent.getDamager() instanceof Egg) return reduceDamageByArmor(config.getDouble("HP.damage of an egg"), p);
					else if (id == EntityType.FIREBALL.getTypeId()) return reduceDamageByArmor(config.getDouble("HP.damage of a fireball"), p);
					else if (id == EntityType.SMALL_FIREBALL.getTypeId()) return reduceDamageByArmor(config.getDouble("HP.damage of a small fireball"), p);
					else if (id == EntityType.WITHER_SKULL.getTypeId()) return reduceDamageByArmor(config.getDouble("HP.damage of a wither skull"), p);
					break;
				}
			case LIGHTNING:
				return reduceDamageByArmor(config.getDouble("HP.damage of a lightning"), p);
			default:
				break;
		}
		return 0;
	}

	public static void writeConfig() {
		File file = new File(AncientRPG.plugin.getDataFolder().getPath() + File.separator + "hpconfig.yml");
		try {
			config.save(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void loadConfig() {
		File file = new File(AncientRPG.plugin.getDataFolder().getPath() + File.separator + "hpconfig.yml");
		if (!file.exists()) AncientRPG.plugin.saveResource("hpconfig.yml", true);
		
		config = YamlConfiguration.loadConfiguration(file);
		for (PlayerData pd : PlayerData.playerData) pd.getHpsystem().maxhp = config.getDouble("HP.HP of a user");
	}

	public static double reduceDamageByArmor(double basedamage, LivingEntity attackedEntity) {
		return basedamage;//(double) basedamage * (1 - getArmorReduction(attackedEntity) / 100);
	}

	@SuppressWarnings("unused")
	private static double getArmorReduction(LivingEntity attackedEntity) {
		double reduction = 0;
		if (attackedEntity.getEquipment().getHelmet() != null) {
			switch (attackedEntity.getEquipment().getHelmet().getType()) {
				case DIAMOND_HELMET:
					reduction += config.getDouble("Armor.Diamond.Damage reduction of a diamond helmet in percentage points");
					break;
				case IRON_HELMET:
					reduction += config.getDouble("Armor.Iron.Damage reduction of an iron helmet in percentage points");
					break;
				case GOLD_HELMET:
					reduction += config.getDouble("Armor.Gold.Damage reduction of a gold helmet in percentage points");
					break;
				case LEATHER_HELMET:
					reduction += config.getDouble("Armor.Leather.Damage reduction of a leather cap in percentage points");
					break;
				case CHAINMAIL_HELMET:
					reduction += config.getDouble("Armor.Chain.Damage reduction of a chain helmet in percentage points");
					break;
				default:
					break;
			}
		}
		
		if (attackedEntity.getEquipment().getChestplate() != null) {
			switch (attackedEntity.getEquipment().getChestplate().getType()) {
				case DIAMOND_CHESTPLATE:
					reduction += config.getDouble("Armor.Diamond.Damage reduction of a diamond chestplate in percentage points");
					break;
				case IRON_CHESTPLATE:
					reduction += config.getDouble("Armor.Iron.Damage reduction of an iron chestplate in percentage points");
					break;
				case GOLD_CHESTPLATE:
					reduction += config.getDouble("Armor.Gold.Damage reduction of a gold chestplate in percentage points");
					break;
				case LEATHER_CHESTPLATE:
					reduction += config.getDouble("Armor.Leather.Damage reduction of a leather tunic in percentage points");
					break;
				case CHAINMAIL_CHESTPLATE:
					reduction += config.getDouble("Armor.Chain.Damage reduction of a chain chestplate in percentage points");
					break;
				default:
					break;
			}
		}
		if (attackedEntity.getEquipment().getLeggings() != null) {
			switch (attackedEntity.getEquipment().getLeggings().getType()) {
				case DIAMOND_LEGGINGS:
					reduction += config.getDouble("Armor.Diamond.Damage reduction of a diamond leggings in percentage points");
					break;
				case IRON_LEGGINGS:
					reduction += config.getDouble("Armor.Iron.Damage reduction of an iron leggings in percentage points");
					break;
				case GOLD_LEGGINGS:
					reduction += config.getDouble("Armor.Gold.Damage reduction of a gold leggings in percentage points");
					break;
				case LEATHER_LEGGINGS:
					reduction += config.getDouble("Armor.Leather.Damage reduction of a leather pants in percentage points");
					break;
				case CHAINMAIL_LEGGINGS:
					reduction += config.getDouble("Armor.Chain.Damage reduction of a chain leggings in percentage points");
					break;
				default:
					break;
			}
		}
		if (attackedEntity.getEquipment().getBoots() != null) {
			switch (attackedEntity.getEquipment().getBoots().getType()) {
				case DIAMOND_BOOTS:
					reduction += config.getDouble("Armor.Diamond.Damage reduction of diamond boots in percentage points");
					break;
				case IRON_BOOTS:
					reduction += config.getDouble("Armor.Iron.Damage reduction of iron boots in percentage points");
					break;
				case GOLD_BOOTS:
					reduction += config.getDouble("Armor.Gold.Damage reduction of gold boots in percentage points");
					break;
				case LEATHER_BOOTS:
					reduction += config.getDouble("Armor.Leather.Damage reduction of leather boots in percentage points");
					break;
				case CHAINMAIL_BOOTS:
					reduction += config.getDouble("Armor.Chain.Damage reduction of chain boots in percentage points");
					break;
				default:
					break;
			}
		}
		return reduction;
	}
	
	// DELETE müsste überflüssig sein. Testen, ob es nicht auch mit LivingEntity zeug geht
	@SuppressWarnings("unused")
	private static double getArmorReduction(Player attackedEntity) {
		double reduction = 0;
		if (attackedEntity.getInventory().getHelmet() != null) {
			switch (attackedEntity.getInventory().getHelmet().getType()) {
				case DIAMOND_HELMET:
					reduction += config.getDouble("Armor.Diamond.Damage reduction of a diamond helmet in percentage points");
					break;
				case IRON_HELMET:
					reduction += config.getDouble("Armor.Iron.Damage reduction of an iron helmet in percentage points");
					break;
				case GOLD_HELMET:
					reduction += config.getDouble("Armor.Gold.Damage reduction of a gold helmet in percentage points");
					break;
				case LEATHER_HELMET:
					reduction += config.getDouble("Armor.Leather.Damage reduction of a leather cap in percentage points");
					break;
				case CHAINMAIL_HELMET:
					reduction += config.getDouble("Armor.Chain.Damage reduction of a chain helmet in percentage points");
					break;
				default:
					break;
			}
		}
		
		if (attackedEntity.getInventory().getChestplate() != null) {
			switch (attackedEntity.getInventory().getChestplate().getType()) {
				case DIAMOND_CHESTPLATE:
					reduction += config.getDouble("Armor.Diamond.Damage reduction of a diamond chestplate in percentage points");
					break;
				case IRON_CHESTPLATE:
					reduction += config.getDouble("Armor.Iron.Damage reduction of an iron chestplate in percentage points");
					break;
				case GOLD_CHESTPLATE:
					reduction += config.getDouble("Armor.Gold.Damage reduction of a gold chestplate in percentage points");
					break;
				case LEATHER_CHESTPLATE:
					reduction += config.getDouble("Armor.Leather.Damage reduction of a leather tunic in percentage points");
					break;
				case CHAINMAIL_CHESTPLATE:
					reduction += config.getDouble("Armor.Chain.Damage reduction of a chain chestplate in percentage points");
					break;
				default:
					break;
			}
		}
		if (attackedEntity.getInventory().getLeggings() != null) {
			switch (attackedEntity.getInventory().getLeggings().getType()) {
				case DIAMOND_LEGGINGS:
					reduction += config.getDouble("Armor.Diamond.Damage reduction of a diamond leggings in percentage points");
					break;
				case IRON_LEGGINGS:
					reduction += config.getDouble("Armor.Iron.Damage reduction of an iron leggings in percentage points");
					break;
				case GOLD_LEGGINGS:
					reduction += config.getDouble("Armor.Gold.Damage reduction of a gold leggings in percentage points");
					break;
				case LEATHER_LEGGINGS:
					reduction += config.getDouble("Armor.Leather.Damage reduction of a leather pants in percentage points");
					break;
				case CHAINMAIL_LEGGINGS:
					reduction += config.getDouble("Armor.Chain.Damage reduction of a chain leggings in percentage points");
					break;
				default:
					break;
			}
		}
		if (attackedEntity.getInventory().getBoots() != null) {
			switch (attackedEntity.getInventory().getBoots().getType()) {
				case DIAMOND_BOOTS:
					reduction += config.getDouble("Armor.Diamond.Damage reduction of diamond boots in percentage points");
					break;
				case IRON_BOOTS:
					reduction += config.getDouble("Armor.Iron.Damage reduction of iron boots in percentage points");
					break;
				case GOLD_BOOTS:
					reduction += config.getDouble("Armor.Gold.Damage reduction of gold boots in percentage points");
					break;
				case LEATHER_BOOTS:
					reduction += config.getDouble("Armor.Leather.Damage reduction of leather boots in percentage points");
					break;
				case CHAINMAIL_BOOTS:
					reduction += config.getDouble("Armor.Chain.Damage reduction of chain boots in percentage points");
					break;
				default:
					break;
			}
		}
		return reduction;
	}

	public static double getHpByMcDamage(UUID uuid, double hp) {
		AncientRPGHP hpinstance = PlayerData.getPlayerData(uuid).getHpsystem();
		return (hpinstance.maxhp * (hp / 20));
	}

	public static double getDamageOfItem(LivingEntity attacker, double d) {
		Material iteminhand = attacker.getEquipment().getItemInHand().getType();
		switch (iteminhand) {
			case IRON_SWORD:
				return getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of an iron sword"));
			case STONE_SWORD:
				return getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a stone sword"));
			case DIAMOND_SWORD:
				return getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a diamond sword"));
			case GOLD_SWORD:
				return getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a gold sword"));
			case WOOD_SWORD:
				return getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a wood sword"));
			case IRON_AXE:
				return getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of an iron axe"));
			case STONE_AXE:
				return getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a stone axe"));
			case DIAMOND_AXE:
				return getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a diamond axe"));
			case GOLD_AXE:
				return getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a gold axe"));
			case WOOD_AXE:
				return getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a wooden axe"));
			default:
				break;
		}
		return d;
	}

	public static double getPlayerDamageOfItem(Player attackedEntity, Material iteminhand, LivingEntity attacker, double basedamage) {
		switch (iteminhand) {
			case IRON_SWORD:
				return reduceDamageByArmor(getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of an iron sword")), attackedEntity);
			case STONE_SWORD:
				return reduceDamageByArmor(getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a stone sword")), attackedEntity);
			case DIAMOND_SWORD:
				return reduceDamageByArmor(getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a diamond sword")), attackedEntity);
			case GOLD_SWORD:
				return reduceDamageByArmor(getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a gold sword")), attackedEntity);
			case WOOD_SWORD:
				return reduceDamageByArmor(getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a wood sword")), attackedEntity);
			case IRON_AXE:
				return reduceDamageByArmor(getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of an iron axe")), attackedEntity);
			case STONE_AXE:
				return reduceDamageByArmor(getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a stone axe")), attackedEntity);
			case DIAMOND_AXE:
				return reduceDamageByArmor(getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a diamond axe")), attackedEntity);
			case GOLD_AXE:
				return reduceDamageByArmor(getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a gold axe")), attackedEntity);
			case WOOD_AXE:
				return reduceDamageByArmor(getDamageOfEnchantementAndPotion(attacker, config.getDouble("HP.damage of a wood axe")), attackedEntity);
			default:
				break;
		}
		return reduceDamageByArmor(basedamage, attackedEntity);
	}

	public static double getBowDamage(Player attacker, Player mPlayer) {
		double basedamage = config.getDouble("HP.damage of an arrow");
		for (Entry<Enchantment, Integer> entry : attacker.getItemInHand().getEnchantments().entrySet()) 
			if (entry.getKey().equals(EnchantmentWrapper.ARROW_DAMAGE)) basedamage += config.getDouble("HP.extra damage of bow enchantments") * entry.getValue();
		return reduceDamageByArmor(basedamage, mPlayer);
	}

	public static double getBowDamage(Player attacker) {
		double basedamage = config.getDouble("HP.damage of an arrow");
		for (Entry<Enchantment, Integer> entry : attacker.getItemInHand().getEnchantments().entrySet()) 
			if (entry.getKey().equals(EnchantmentWrapper.ARROW_DAMAGE)) basedamage += config.getDouble("HP.extra damage of bow enchantments") * entry.getValue();
		return basedamage;
	}

	public static double getDamageOfEnchantementAndPotion(LivingEntity attacker, double d) {
		for (PotionEffect pe : attacker.getActivePotionEffects()) {
			if (pe.getType().equals(PotionEffectType.INCREASE_DAMAGE)) d += config.getDouble("HP.extra damage of strenght potion per tier") * pe.getAmplifier();
			if (pe.getType().equals(PotionEffectType.WEAKNESS)) d -= config.getDouble("HP.less damage of weaken potion per tier") * pe.getAmplifier();
		}
		for (Entry<Enchantment, Integer> entry : attacker.getEquipment().getItemInHand().getEnchantments().entrySet()) {
			if (entry.getKey() == Enchantment.DAMAGE_ALL) d += config.getDouble("HP.extra damage of sharpness enchantment") * entry.getValue();
			if (entry.getKey() == Enchantment.DAMAGE_ARTHROPODS) d += config.getDouble("HP.max extra damage of bane of arthropods and smite enchantment") * entry.getValue();
		}
		return d;
	}

	public static boolean isEnabled() {
		return config.getBoolean("HP.HPsystem enabled");
	}

	public static double getStandardHP() {
		return config.getDouble("HP.HP of a user");
	}
	
	public static double getHPRegeneration() {
		return config.getDouble("HP.regeneration per interval");
	}
	
	public static double getHPRegenerationInterval() {
		return config.getDouble("HP.regeneration interval");
	}
	
	public static double getMinimalFoodLevelForReg() {
		return config.getDouble("HP.minfoodlevel for regeneration");
	}

	public static double getWitherDamage() {
		return config.getDouble("HP.damage of wither effect");
	}

	public static double getHarmPotionDamage() {
		return config.getDouble("HP.damage per harm potion per tier");
	}

	public static double getPoisonDamage() {
		return config.getDouble("HP.posion potion damage per tick per tier");
	}

	public static double getFistDamage() {
		return config.getDouble("HP.damage of fists");
	}

	public static long getTimeBetweenAttacks() {
		 return config.getLong("HP.minimum time between each attack on a player in milliseconds");
	}

	public static double getHealPotionHP() {
		return config.getDouble("HP.heal per heal potion per tier");
	}

	public static double getRegenerationPotionHP() {
		return config.getDouble("HP.regeneration potion per tick per tier");
	}
	
//	public static double getDisplayDivider() {
//		return 1; // maybe later via config changeable
//	}
}
