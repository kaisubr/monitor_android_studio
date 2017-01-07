package iosf.github.kaisubr.sciencefair.FallbackActivities;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created at 1:59 PM for Science Fair.
 */
public interface TemporaryStorage {
    //TODO How about NOT using an interface (since it clearly causes problems?)
    String username = "?";
    int maxRecentCalls = 8;
    String phoneNumber = "DEF";
}
