package tn.esprit.autoloc;

public final class Utils {

    private Utils() {
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
