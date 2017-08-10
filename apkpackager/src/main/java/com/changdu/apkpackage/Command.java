package com.changdu.apkpackage;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by davidleen29 on 2017/8/7.
 */

public class Command {


    public static void execute(List<String> commands, OutputStream outputStream) throws CmdExecuteException {


        ProcessBuilder builder = new ProcessBuilder(
                commands);

//       // builder.directory(new File(path));
//


        Process process = null;
        try {
            process = builder.start();

            configStream(process, outputStream);

            int value = process.waitFor();
            if (value != 0) {
                throw new CmdExecuteException("cnd 执行失败,返回错误");

            }
        } catch (Exception e) {
            throw new CmdExecuteException("编译出现错误：" + e.getMessage());
        }

    }

    public static final void executeCmd(String[] commands, OutputStream outputStream) throws CmdExecuteException {


        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe");
//        builder.redirectErrorStream(true);
        try {

            for (String cmd : commands) {
                builder.command("cmd.exe", "/k", cmd);
            }
//           builder.command( "cmd.exe","/k",   commands[0]  );


            Process p = builder.start();

            configStream(p, outputStream);

//


            //强制进程等待。
            int exitVal = p.waitFor();
            if (exitVal != 0) {


                throw new CmdExecuteException("cmd 执行失败,返回错误");

            }


        } catch (Exception e) {


            throw new CmdExecuteException("编译出现错误：" + e.getMessage());

        }


    }


    public static void configStream(Process p, OutputStream outputStream) {
        new Thread(new Command.SyncErrorPipe(p.getErrorStream(), outputStream == null ? System.err : outputStream)).start();
        new Thread(new Command.SyncPipe(p.getInputStream(), outputStream == null ? System.out : outputStream)).start();

//        new Command.SyncPipe(p.getErrorStream(),outputStream==null?System.err:outputStream).run();
//        new Command.SyncPipe(p.getInputStream(), outputStream==null?System.out:outputStream).run();;
        PrintWriter stdin = new PrintWriter(p.getOutputStream());

        //      stdin.println("cmd /c start D://log.txt");                    //定位到D盘根目录


        stdin.close();
    }

    /**
     * 与 window 控制台 输入输出处理  字符编码GBK 转换到 UTF-8
     */
    static class SyncPipe implements Runnable {
        public SyncPipe(InputStream istrm, OutputStream ostrm) {
            istrm_ = istrm;
            ostrm_ = ostrm;
        }

        public void run() {

            try {
                copyStreamFromDos(istrm_, ostrm_);
            } catch (CmdExecuteException e) {
                e.printStackTrace();
            }


        }

        private final OutputStream ostrm_;
        private final InputStream istrm_;
    }


    static class SyncErrorPipe implements Runnable {
        private final OutputStream ostrm_;
        private final InputStream istrm_;

        public SyncErrorPipe(InputStream istrm, OutputStream ostrm) {
            istrm_ = istrm;
            ostrm_ = ostrm;

        }

        public void run() {

            try {
                copyStreamFromDos(istrm_, ostrm_);
            } catch (CmdExecuteException e) {
                e.printStackTrace();
            }


        }


    }

    public static void copyStreamFromDos(InputStream istrm, OutputStream ostrm) throws CmdExecuteException {
        try {
            final byte[] buffer = new byte[1024];
            boolean hasError=false;
            for (int length = 0; (length = istrm.read(buffer)) != -1; ) {


                String s = new String(buffer, 0, length, "GBK");


                hasError=true;

                ostrm.write(s.getBytes());

            }



//            if(hasError)
//              throw new CmdExecuteException("编译出现错误");
        } catch (Exception e) {
            throw new CmdExecuteException("处理命令出现错误：" + e.getMessage());
        }

    }


}
