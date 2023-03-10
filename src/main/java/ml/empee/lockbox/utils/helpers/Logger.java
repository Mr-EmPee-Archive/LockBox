package ml.empee.lockbox.utils.helpers;

import lombok.Getter;
import lombok.Setter;
import ml.empee.ioc.Bean;
import ml.empee.lockbox.LockBox;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.logging.Level;

/**
 * This class allow you to easily log messages.
 **/

public final class Logger implements Bean {

  @Getter @Setter
  private String prefix;
  @Getter @Setter
  private boolean isDebugEnabled;
  @Setter
  private java.util.logging.Logger consoleLogger;
  private final TranslationManager translationManager;

  public Logger(LockBox plugin, TranslationManager translationManager) {
    this.translationManager = translationManager;

    prefix = LockBox.PREFIX;
    consoleLogger = plugin.getLogger();
  }

  public void log(CommandSender sender, String message, Object... args) {
    message = String.format(message, args);

    message = message.replace("\n", "\n&r");
    message = prefix + message;
    if (message.endsWith("\n")) {
      message += " ";
    }

    message = message.replace("\t", "    ");

    sender.sendMessage(
        ChatColor.translateAlternateColorCodes('&', message).split("\n")
    );
  }

  public void translatedLog(CommandSender sender, String key, Object... args) {
    Locale locale = Locale.ENGLISH;
    if(sender instanceof Player) {
      String[] language = ((Player) sender).getLocale().split("_");
      if(language.length == 2) {
        locale = new Locale(language[0], language[1]);
      }
    }

    log(sender, translationManager.getTranslation(key, locale), args);
  }

  /** Log to the console a debug message. **/
  public void debug(String message, Object... args) {
    if (isDebugEnabled) {
      consoleLogger.info(String.format(Locale.ROOT, message, args));
    }
  }

  /** Log a debug message to a player. **/
  public void debug(CommandSender player, String message, Object... args) {
    if (isDebugEnabled) {
      log(player, message, ChatColor.DARK_GRAY, args);
    }
  }

  /** Log to the console an info message. **/
  public void info(String message, Object... args) {
    if (consoleLogger.isLoggable(Level.INFO)) {
      consoleLogger.info(String.format(Locale.ROOT, message, args));
    }
  }

  /** Log to the console a warning message. **/
  public void warning(String message, Object... args) {
    if (consoleLogger.isLoggable(Level.WARNING)) {
      consoleLogger.warning(String.format(Locale.ROOT, message, args));
    }
  }

  /** Log to the console an error message. **/
  public void error(String message, Object... args) {
    if (consoleLogger.isLoggable(Level.SEVERE)) {
      consoleLogger.severe(String.format(Locale.ROOT, message, args));
    }
  }
}
