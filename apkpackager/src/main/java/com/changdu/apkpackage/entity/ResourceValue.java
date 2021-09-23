package com.changdu.apkpackage.entity;



public class ResourceValue {

    public static final String TYPE_STRING="string";
    public static final String TYPE_BOOL="bool";
    public static final String TYPE_INTEGER="integer";
    public static final String TYPE_META_DATA="meta-data";
    public String key;
    public String type;
    public String value;
    public static final String[] TYPES= new String[]{TYPE_STRING,TYPE_BOOL, TYPE_INTEGER,TYPE_META_DATA
    };


    @Override
    public String toString()
    {

        return key+" "+type+" "+value;

    }
}
