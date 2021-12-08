package ip.swagenaar25.samlibs;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Util {
    public static String getJarPath() {
        if (MainGUI.DEV) {
            return "/Users/FAYOGA/IdeaProjects/SamLibs/";
        }
        String path = Util.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("/C:","");
        return URLDecoder.decode(path, StandardCharsets.UTF_8);
    }
}