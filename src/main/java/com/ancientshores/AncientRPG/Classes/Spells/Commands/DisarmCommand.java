package com.ancientshores.AncientRPG.Classes.Spells.Commands;

import com.ancientshores.AncientRPG.AncientRPG;
import com.ancientshores.AncientRPG.Classes.Spells.CommandDescription;
import com.ancientshores.AncientRPG.Classes.Spells.ParameterType;
import com.ancientshores.AncientRPG.Listeners.AncientRPGSpellListener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DisarmCommand extends ICommand {
    @CommandDescription(description = "<html>Disarms the player for the specified time</html>",
            argnames = {"player", "duration"}, name = "Disarm", parameters = {ParameterType.Player, ParameterType.Number})
    public DisarmCommand() {
        ParameterType[] buffer = {ParameterType.Player, ParameterType.Number};
        this.paramTypes = buffer;
    }

    @Override
    public boolean playCommand(final EffectArgs ca) {
        try {
            if (ca.params.get(0) instanceof Player[] && ca.params.get(1) instanceof Number) {
                final Player[] target = (Player[]) ca.params.get(0);
                final int time = (int) ((Number) ca.params.get(1)).doubleValue();
                if (target != null && target.length > 0 && target[0] instanceof Player) {
                    AncientRPG.plugin.scheduleThreadSafeTask(AncientRPG.plugin, new Runnable() {

                        @Override
                        public void run() {
                            for (final Player p : target) {
                                if (p == null) {
                                    continue;
                                }
                                if (time > 0 && !(AncientRPGSpellListener.disarmList.containsKey(p))) {
                                    final ItemStack is = p.getItemInHand();
                                    AncientRPGSpellListener.disarmList.put(p, is);
                                    p.setItemInHand(new ItemStack(0));
                                    AncientRPG.plugin.scheduleThreadSafeTask(AncientRPG.plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            if (p.getItemInHand() == null) {
                                                p.setItemInHand(is);
                                            } else {
                                                p.getInventory().addItem(is);
                                            }
                                            AncientRPGSpellListener.disarmList.remove(p);
                                        }
                                    }, Math.round(time / 50));
                                }
                            }
                        }
                    });
                    return true;
                } else if (ca.p.active) {
                    ca.caster.sendMessage("Target not found");
                }
            }
        } catch (IndexOutOfBoundsException ignored) {

        }
        return false;
    }
}