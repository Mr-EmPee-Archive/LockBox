package ml.empee.lockbox.controllers;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.lockbox.SafeRegistry;
import ml.empee.lockbox.config.CommandsConfig;
import ml.empee.lockbox.model.Safe;
import ml.empee.lockbox.utils.helpers.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/** Controller used for managing the plugin. **/
@RequiredArgsConstructor
public class SafeController implements Bean {

  private final CommandsConfig commandsConfig;
  private final Logger logger;
  private final SafeRegistry safeRegistry;

  @Override
  public void onStart() {
    commandsConfig.register(this);
  }

  @CommandMethod("safe give <type> [target]")
  public void giveSafe(
      CommandSender sender,
      @Argument Safe.Type type,
      @Argument @Nullable Player target
  ) {
    if (target == null) {
      if (sender instanceof Player) {
        target = (Player) sender;
      } else {
        logger.log(sender, "You must select a player");
        return;
      }
    }

    ItemStack lock = safeRegistry.getByType(type).build();

    boolean hasSucceed = target.getInventory().addItem(lock).isEmpty();
    if (!hasSucceed) {
      logger.log(sender,
          "Unable to give safe to &e%s&r his inventory was full", target.getName()
      );
      return;
    }

    logger.log(sender, "Safe given to &e%s", target.getName());
  }

}
