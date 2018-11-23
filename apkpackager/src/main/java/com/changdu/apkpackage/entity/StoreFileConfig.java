package com.changdu.apkpackage.entity;

import java.io.Serializable;

public class StoreFileConfig implements Serializable {

    private static final long serialVersionUID = 7825224039759649114l;


        //签名文件
        public String keyStoreFilePath;
        //存储密码
        public String storepass;
        //密钥
        public String keypass;
        //别名
        public String alias;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoreFileConfig config = (StoreFileConfig) o;

        if (!keyStoreFilePath.equals(config.keyStoreFilePath)) return false;
        if (!storepass.equals(config.storepass)) return false;
        if (!keypass.equals(config.keypass)) return false;
        return alias.equals(config.alias);
    }

    @Override
    public int hashCode() {
        int result = keyStoreFilePath.hashCode();
        result = 31 * result + storepass.hashCode();
        result = 31 * result + keypass.hashCode();
        result = 31 * result + alias.hashCode();
        return result;
    }
}
