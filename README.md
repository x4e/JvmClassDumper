# JvmClassDumper
```
Replace jdk path with the jdk that the running application is using, otherwise dumping will not be successful.

java -cp "C:\Program Files\Java\jdk1.8.0_241\lib\*;D:\Computing\dumper\build\libs\*;" cookiedragon.dumper.EntryPoint list vm
java -cp "C:\Program Files\Java\jdk1.8.0_241\lib\*;D:\Computing\dumper\build\libs\*;" cookiedragon.dumper.EntryPoint list classes [pid]
java -cp "C:\Program Files\Java\jdk1.8.0_241\lib\*;D:\Computing\dumper\build\libs\*;" cookiedragon.dumper.EntryPoint dump [pid] com/example
```
