package com.changdu.apkpackage;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by davidleen29 on 2017/8/7.
 */

public class Command {


    public static void execute(List<String> commands, IPrintable iPrintable) throws CmdExecuteException {


        ProcessBuilder builder = new ProcessBuilder(
                commands);

//       // builder.directory(new File(path));
//


        Process process = null;
        try {
            process = builder.start();

            configStream(process, iPrintable);

            int value = process.waitFor();
            if (value != 0) {
                throw new CmdExecuteException(value,"cnd 执行失败,返回错误:"+value);

            }
        } catch (Exception e) {
            throw new CmdExecuteException(e.hashCode(),"编译出现错误：" + e.getMessage());
        }

    }

    public static final void executeCmd(String[] commands, IPrintable iPrintable) throws CmdExecuteException {


        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe");
//        builder.redirectErrorStream(true);
        try {

            for (String cmd : commands) {
                builder.command("cmd.exe", "/k", cmd);
            }
//           builder.command( "cmd.exe","/k",   commands[0]  );


            Process p = builder.start();

            configStream(p, iPrintable);

//


            //强制进程等待。
            int exitVal = p.waitFor();
            if (exitVal != 0) {


                throw new CmdExecuteException(exitVal,"cmd 执行失败,返回错误:"+exitVal);

            }


        } catch (Exception e) {


            throw new CmdExecuteException(e.hashCode(),"编译出现错误：" + e.getMessage());

        }


    }


    public static void configStream(Process p, IPrintable iPrintable) {

        new Thread(new Command.SyncPipe(p.getInputStream(), iPrintable)).start();


        PrintWriter stdin = new PrintWriter(p.getOutputStream());

        //      stdin.println("cmd /c start D://log.txt");                    //定位到D盘根目录


        stdin.close();
    }

    /**
     * 与 window 控制台 输入输出处理  字符编码GBK 转换到 UTF-8
     */
    static class SyncPipe implements Runnable {
        public SyncPipe(InputStream istrm, IPrintable iPrintable) {
            istrm_ = istrm;
            this.iPrintable = iPrintable;
        }

        public void run() {
            iPrintable.println();
            try {
                copyStreamFromDos(istrm_, iPrintable);
            } catch (CmdExecuteException e) {
                e.printStackTrace();
            }


        }

        private final IPrintable iPrintable;
        private final InputStream istrm_;
    }


    public static void copyStreamFromDos(InputStream istrm, IPrintable iPrintable) throws CmdExecuteException {
        try {
            final byte[] buffer = new byte[1024];

            for (int length = 0; (length = istrm.read(buffer)) != -1; ) {


                String s = new String(buffer, 0, length, "GBK");


                iPrintable.print(s.getBytes());

            }


        } catch (Exception e) {
            iPrintable.println("处理命令出现错误：" + e.getMessage());
        }

    }


}
