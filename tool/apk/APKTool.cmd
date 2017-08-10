cls
@echo off

GOTO MENU
:MENU
cls
ECHO.                  black-snowflake制作！
ECHO.========================================================================
ECHO.             　      APK编译与签名工具 ApkTool v1.4.1               
ECHO.                                                                    
ECHO.说明：本工具需要Java环境；编译APK前请重命名为123.apk；在编译系统APK前
ECHO.      请先安装反编译所必须的框架文件framework-res.apk； HTC Rom还需要
ECHO.      提供com.htc.resources.apk放置到此目录下，用选项5、6安装即可！
ECHO.                                                                 
ECHO.      1. 反编译文件 123.apk 后，保存在【APK】文件夹内；        
ECHO.      2. 回编译文件夹【APK】后，保存在【Apk/build】文件夹内；          
ECHO.      3. 回编译【APK】并【签名】，保存在【Apk/build】文件夹内；         
ECHO.      4. 单独签名【已编译(未签名).apk】文件；                  
ECHO.      5. 安装反编译系统APK所需的framework-res.apk
ECHO.      6. 安装反编译系统APK所需的com.htc.resources.apk(HTC Rom需执行此项)
ECHO.      7. 删除上一次反编译操作时产生的APK、framework文件夹
ECHO.      0. 退出
ECHO.                                                    
ECHO.========================================================================
ECHO.请输入您的选择：1、2、3、4、5、6、7、0（按回车键）
set /p ID=
if "%id%"=="1" goto cmd1
if "%id%"=="2" goto cmd2
if "%id%"=="3" goto cmd3
if "%id%"=="4" goto cmd4
if "%id%"=="5" goto cmd5
if "%id%"=="6" goto cmd6
if "%id%"=="7" goto cmd7
if "%id%"=="0" goto cmd0
pause
goto menu

:cmd1
ECHO.　　正在反编译中...
java -jar apktool.jar d 123.apk APK
ECHO. 
ECHO. 
ECHO. 按任意键返回目录
pause>nul
@echo off
goto menu

:cmd2
ECHO.　　正在回编译中...
java -jar apktool.jar b APK
goto end
:end
@echo off
ECHO. 
ECHO. 
ECHO. 按任意键返回目录
pause>nul
move .\APK\dist\123.apk .\APK\build\已编译(未签名).apk
rd /s /q .\APK\dist\
goto menu

:cmd3
ECHO.　　正在回编译中...
java -jar apktool.jar b APK
goto end
:end
@echo off
ECHO. 
ECHO. 
ECHO.   按任意键返回目录
pause>nul
cls
move .\APK\dist\123.apk .\APK\build\已编译(未签名).apk
rd /s /q .\APK\dist\
ECHO.　　开始签名APK文件...
java -jar .\sign\signapk.jar .\sign\testkey.x509.pem .\sign\testkey.pk8 .\APK\build\已编译(未签名).apk .\APK\build\已编译(已签名).apk
ECHO.　　完成签名！
cls
goto menu

:cmd4
cls
ECHO.　　开始签名APK文件...
java -jar .\sign\signapk.jar .\sign\testkey.x509.pem .\sign\testkey.pk8 .\APK\build\已编译(未签名).apk .\APK\build\已编译(已签名).apk
ECHO.　　完成签名！
cls
exit

:cmd5
echo.    正在安装系统框架framework-res.apk
java -jar apktool.jar if framework-res.apk
@echo off
ECHO. 
ECHO. 
ECHO.   安装完毕，按任意键返回目录
pause>nul
goto menu

:cmd6
echo.    正在安装系统框架com.htc.resources.apk
java -jar apktool.jar if com.htc.resources.apk
@echo off
ECHO. 
ECHO. 
ECHO.   安装完毕，按任意键返回目录
pause>nul
goto menu

:cmd7
rd /s /q .\APK
rd /s /q .\framework
ECHO. 
ECHO. 
ECHO.   删除临时文件夹成功，按任意键返回目录
pause>nul
goto menu

:cmd0
exit
