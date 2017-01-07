package iosf.github.kaisubr.sciencefair.Database;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created at 10:28 AM for ScienceFair.
 */

public class DatabaseManager {
    public static boolean isFromGovt(String number, Context ctx) {
        File f = new File(ctx.getFilesDir(), "govt.db"); //internal storage.
        Scanner scanner;

        try {
            scanner = new Scanner(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return true; //just in case
        }
        while (scanner.hasNextLine()) {
            if (scanner.nextLine().equals(number)) {
                return true;
            }
        }

        return false;

    }
}
