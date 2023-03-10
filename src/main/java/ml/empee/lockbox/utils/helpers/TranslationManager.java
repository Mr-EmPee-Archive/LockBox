package ml.empee.lockbox.utils.helpers;

import ml.empee.ioc.Bean;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationManager implements Bean {

  private final ResourceBundle defaultBundle;
  private final List<ResourceBundle> bundles = new ArrayList<>();

  public TranslationManager() {
    defaultBundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);
    bundles.add(defaultBundle);
  }

  public String getTranslation(String key, Locale locale) {
    return findByLocale(locale).getString(key);
  }

  @NotNull
  public ResourceBundle findByLocale(Locale locale) {
    return bundles.stream()
        .filter(b -> b.getLocale().equals(locale))
        .findFirst().orElse(defaultBundle);
  }

}
