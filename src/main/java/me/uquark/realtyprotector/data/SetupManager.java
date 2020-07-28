package me.uquark.realtyprotector.data;

import me.uquark.quarkcore.QuarkCore;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SetupManager {
    public static void ensureValidDBPresent(String modName, String dbName) throws IOException {
        File db = new File(QuarkCore.getModDir(modName) + dbName + ".h2.db");
        if (!db.exists()) {
            InputStream inputStream = SetupManager.class.getResourceAsStream("/" + dbName + ".h2.db");
            FileUtils.copyInputStreamToFile(inputStream, db);
        }
    }
}
