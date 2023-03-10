package ml.empee.lockbox.config;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.exceptions.ArgumentParseException;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ml.empee.ioc.Bean;
import ml.empee.lockbox.LockBox;
import ml.empee.lockbox.utils.helpers.Logger;
import org.bukkit.command.CommandSender;

import java.util.function.Function;

@RequiredArgsConstructor
public class CommandsConfig implements Bean {

  private final LockBox plugin;
  private final Logger logger;
  private PaperCommandManager<CommandSender> commandManager;
  private AnnotationParser<CommandSender> commandParser;

  @Override
  @SneakyThrows
  public void onStart() {
    commandManager = new PaperCommandManager<>(
        plugin, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity()
    );

    registerExceptionHandlers();

    commandParser = new AnnotationParser<>(
        commandManager, CommandSender.class, parameters -> SimpleCommandMeta.empty()
    );

    try {
      commandManager.registerBrigadier();
    } catch (BukkitCommandManager.BrigadierFailureException e) {
      logger.warning("Command suggestion not supported! If you think this is an error make sure to use paper");
    }
  }

  private void registerExceptionHandlers() {
    commandManager.registerExceptionHandler(NoPermissionException.class, (sender, e) -> {
      logger.log(sender, "cmd-no-permission");
    });

    commandManager.registerExceptionHandler(InvalidSyntaxException.class, (sender, e) -> {
      logger.log(sender, "cmd-invalid-syntax");
    });

    commandManager.registerExceptionHandler(InvalidCommandSenderException.class, (sender, e) -> {
      logger.log(sender, "cmd-invalid-sender");
    });

    commandManager.registerExceptionHandler(ArgumentParseException.class, (sender, e) -> {
      logger.log(sender, "cmd-invalid-argument", e.getCause().getMessage());
    });

    commandManager.registerExceptionHandler(Exception.class, (sender, e) -> {
      logger.log(sender, "cmd-exception");
    });
  }

  public <T> void register(T command) {
    commandParser.parse(command);
  }

}
