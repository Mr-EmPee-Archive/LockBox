package ml.empee.lockbox.utils.helpers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.ResourceBundle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Translator {
  private static final ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.ROOT);

  public static String translate(String key) {
    return bundle.getString(key);
  }

  public static String[] translateBlock(String key) {
    String translation = Translator.translate(key);
    if(translation.endsWith("\n")) {
      translation += " ";
    }

    return translation.split("\n");
  }

}
