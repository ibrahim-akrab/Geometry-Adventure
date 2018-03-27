package com.actionteam.geometryadventures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by theartful on 3/27/18.
 */

public abstract class GameUtils {
    // used for txt files
    public abstract InputStream openFile(String fileName) throws FileNotFoundException,
            IOException;
    public abstract File getFile(String fileName);
}
