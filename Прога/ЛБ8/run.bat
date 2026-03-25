@echo off
chcp 65001 > nul
set WSL_IP=192.168.179.73 
java -jar "C:\Users\user\Desktop\ITMO_2sem\Прога\ЛБ8\client-all.jar" %WSL_IP%
pause