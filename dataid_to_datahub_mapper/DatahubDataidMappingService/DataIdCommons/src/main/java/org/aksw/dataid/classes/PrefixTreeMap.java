package org.aksw.dataid.classes;

import java.util.HashMap;

/**
 * Created by Chile on 10/27/2015.
 */


public class PrefixTreeMap<K,V> extends HashMap<K,V> {
    public PrefixTreeMap()
    {
        super();
    }
    @Override
    public boolean equals(Object o) {
        if(Integer.class.isAssignableFrom(o.getClass()))
        {
            int i = (int)o;
            if(i < 0 && i > -500)
                return false;
            if(i <= -500)
            {
                if(i <= -2000)
                    return true;
                if(i <= -1000)
                {
                    int zw = ((i + 1000)*(-1));
                    zw = zw - (zw % 16);
                    for(int j =0; j < 16; j++)
                    {
                        if(super.equals(zw + j))
                            return true;
                    }
                    return false;
                }
                int zw = ((i + 500)*(-1));
                for(int j =0; j < 16; j++)
                {
                    if(super.equals(zw + (j*16)))
                        return true;
                }
                return false;
            }
            return super.equals(o);
        }
        return false;
    }
}