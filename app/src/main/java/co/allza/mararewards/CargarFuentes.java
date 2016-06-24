package co.allza.mararewards;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Tavo on 07/06/2016.
 */
public class CargarFuentes {

public static final int ROBOTO_MEDIUM =   0;
public static final int ROBOTO_REGULAR =   1;
public static final int RUBIK_LIGHT =   2;
public static final int RUBIK_REGULAR =   3;
public static final int RUBIK_MEDIUM =   4;
public static final int RUBIK_BOLD =   5;

private static final int NUM_OF_CUSTOM_FONTS = 6;

private static boolean fontsLoaded = false;

private static Typeface[] fonts = new Typeface[6];

private static String[] fontPath = {
        "fonts/Roboto-Medium.ttf",
        "fonts/Roboto-Regular.ttf",
        "fonts/Rubik-Light.ttf",
        "fonts/Rubik-Regular.ttf",
        "fonts/Rubik-Medium.ttf",
        "fonts/Rubik-Bold.ttf"
};
        /**
         * Returns a loaded custom font based on it's identifier.
         *
         * @param context - the current context
         * @param fontIdentifier = the identifier of the requested font
         *
         * @return Typeface object of the requested font.
         */
        public static Typeface getTypeface(Context context, int fontIdentifier) {
            if (!fontsLoaded) {
                loadFonts(context);
            }
            return fonts[fontIdentifier];
        }


        private static void loadFonts(Context context) {
            for (int i = 0; i < NUM_OF_CUSTOM_FONTS; i++) {
                fonts[i] = Typeface.createFromAsset(context.getAssets(), fontPath[i]);
            }
            fontsLoaded = true;

        }
}
