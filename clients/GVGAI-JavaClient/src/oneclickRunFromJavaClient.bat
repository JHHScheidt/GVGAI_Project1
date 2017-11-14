@echo off

set path=%PATH%;C:\Program Files\Java\jdk1.8.0_144\bin

set gameId=-1

:START

TASKKILL /F /T /FI "WINDOWTITLE eq Java-VGDL*"

set /A gameId=gameId+1

echo running game %gameId%

IF %gameId% gtr 111 GOTO END

set shDir=utils
set serverDir=..\..\..

set DIRECTORY=logs
if not exist %DIRECTORY% mkdir %DIRECTORY%


rem Build the client
set src_folder=..\src
set build_folder=client-out

if not exist %build_folder% mkdir %build_folder%

dir /s/b %src_folder%\*.java > sources.txt
javac -d %build_folder% @sources.txt

rem run with screen visualisation
java -cp %build_folder% TestLearningClient -shDir %shDir% -serverDir %serverDir% -visuals -gameId %gameId%
rem run without screen visualisation
rem java -cp %build_folder% TestLearningClient -shDir %shDir% -serverDir %serverDir%

GOTO START

:END