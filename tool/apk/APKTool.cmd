cls
@echo off

GOTO MENU
:MENU
cls
ECHO.                  black-snowflake������
ECHO.========================================================================
ECHO.             ��      APK������ǩ������ ApkTool v1.4.1               
ECHO.                                                                    
ECHO.˵������������ҪJava����������APKǰ��������Ϊ123.apk���ڱ���ϵͳAPKǰ
ECHO.      ���Ȱ�װ������������Ŀ���ļ�framework-res.apk�� HTC Rom����Ҫ
ECHO.      �ṩcom.htc.resources.apk���õ���Ŀ¼�£���ѡ��5��6��װ���ɣ�
ECHO.                                                                 
ECHO.      1. �������ļ� 123.apk �󣬱����ڡ�APK���ļ����ڣ�        
ECHO.      2. �ر����ļ��С�APK���󣬱����ڡ�Apk/build���ļ����ڣ�          
ECHO.      3. �ر��롾APK������ǩ�����������ڡ�Apk/build���ļ����ڣ�         
ECHO.      4. ����ǩ�����ѱ���(δǩ��).apk���ļ���                  
ECHO.      5. ��װ������ϵͳAPK�����framework-res.apk
ECHO.      6. ��װ������ϵͳAPK�����com.htc.resources.apk(HTC Rom��ִ�д���)
ECHO.      7. ɾ����һ�η��������ʱ������APK��framework�ļ���
ECHO.      0. �˳�
ECHO.                                                    
ECHO.========================================================================
ECHO.����������ѡ��1��2��3��4��5��6��7��0�����س�����
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
ECHO.�������ڷ�������...
java -jar apktool.jar d 123.apk APK
ECHO. 
ECHO. 
ECHO. �����������Ŀ¼
pause>nul
@echo off
goto menu

:cmd2
ECHO.�������ڻر�����...
java -jar apktool.jar b APK
goto end
:end
@echo off
ECHO. 
ECHO. 
ECHO. �����������Ŀ¼
pause>nul
move .\APK\dist\123.apk .\APK\build\�ѱ���(δǩ��).apk
rd /s /q .\APK\dist\
goto menu

:cmd3
ECHO.�������ڻر�����...
java -jar apktool.jar b APK
goto end
:end
@echo off
ECHO. 
ECHO. 
ECHO.   �����������Ŀ¼
pause>nul
cls
move .\APK\dist\123.apk .\APK\build\�ѱ���(δǩ��).apk
rd /s /q .\APK\dist\
ECHO.������ʼǩ��APK�ļ�...
java -jar .\sign\signapk.jar .\sign\testkey.x509.pem .\sign\testkey.pk8 .\APK\build\�ѱ���(δǩ��).apk .\APK\build\�ѱ���(��ǩ��).apk
ECHO.�������ǩ����
cls
goto menu

:cmd4
cls
ECHO.������ʼǩ��APK�ļ�...
java -jar .\sign\signapk.jar .\sign\testkey.x509.pem .\sign\testkey.pk8 .\APK\build\�ѱ���(δǩ��).apk .\APK\build\�ѱ���(��ǩ��).apk
ECHO.�������ǩ����
cls
exit

:cmd5
echo.    ���ڰ�װϵͳ���framework-res.apk
java -jar apktool.jar if framework-res.apk
@echo off
ECHO. 
ECHO. 
ECHO.   ��װ��ϣ������������Ŀ¼
pause>nul
goto menu

:cmd6
echo.    ���ڰ�װϵͳ���com.htc.resources.apk
java -jar apktool.jar if com.htc.resources.apk
@echo off
ECHO. 
ECHO. 
ECHO.   ��װ��ϣ������������Ŀ¼
pause>nul
goto menu

:cmd7
rd /s /q .\APK
rd /s /q .\framework
ECHO. 
ECHO. 
ECHO.   ɾ����ʱ�ļ��гɹ��������������Ŀ¼
pause>nul
goto menu

:cmd0
exit
