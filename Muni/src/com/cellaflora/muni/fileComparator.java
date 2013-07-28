package com.cellaflora.muni;

import java.io.File;
import java.util.Comparator;

/**
 * Created by sdickson on 7/28/13.
 */
public class fileComparator implements Comparator<File>
{
    public int compare(File f1, File f2)
    {
        int result = 0;

        if(f1.length() < f2.length())
        {
            result = 1;
        }
        else if(f1.length() > f2.length())
        {
            result = -1;
        }

        return result;
    }
}
