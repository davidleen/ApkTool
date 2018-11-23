package com.changdu.apkpackage.entity;


import java.util.ArrayList;
import java.util.List;

public class StoreFileHistory  extends ArrayList<StoreFileConfig> {

    private static final long serialVersionUID = 7825224039759649115l;

    public void addItem(StoreFileConfig newConfig)
    {

        if (newConfig==null) return ;

        List<StoreFileConfig> removedList=new ArrayList<>();
        for (StoreFileConfig config:this)
        {
            if(config.keyStoreFilePath.equalsIgnoreCase(newConfig.keyStoreFilePath))
            {
                removedList.add(config);

            }


        }

        this.removeAll(removedList);
        this.add(newConfig);

    }

    public StoreFileConfig getItem(String keyStoreFilePath)
    {
        if(keyStoreFilePath==null) return null;
        for (StoreFileConfig config:this)
        {
            if(keyStoreFilePath.equalsIgnoreCase(config.keyStoreFilePath))
            {
                return config;
            }

        }
        return null;
    }


}
