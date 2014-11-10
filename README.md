MSearcher
=========

a search file util support different encoding ,such as GBK UTF-8
<br><br>
文件、目录搜索的小工具，开始是专门为了搜索GBK和UTF8两种编码的文件，现在可以制定多种编码方式来搜索文件<br><br>
本科生，代码可能很不规范<br><br>
因为windows下好像没有grep，win7默认的搜索用起来也不习惯，search my file也不支持搜索中文，所以开始试着按自己想法尝试写这个东西用来搜源码文件。<br>
做了个很粗陋的GUI，在缺少-f参数时会启动图形界面
<br>粗陋的成品<br>
http://dl.vmall.com/c01z8rm42e
<br>
<br>
<br>
参数<br>
<br>
&ensp;  -f &ensp;搜索的文件夹，可以用正则式表示<br>
&ensp;  -k &ensp;搜索文件包含的关键字，可以用正则式表示，没有这个参数则根据n参数搜索匹配的文件名<br>
&ensp;  -n &ensp;文件名，可以用正则式表示，如果为空则视为 .*(匹配任何文件)<br>
&ensp;  -s &ensp;单独列出名字含有关键字的文件和目录<br>
&ensp;  -i &ensp;忽略正则式的大小写<br>
&ensp;  -e &ensp;限制编码方式，没有这个参数默认同时支持GBK和UTF8（先根据GBK查找，然后用UTF8查找），多个编码用'&'连接<br>
<br>
特殊符号<br>
<br>
&ensp;  & &ensp;连接多个参数表示存在这些参数中的一个即匹配， 如 -k &haha&hehe 匹配包含 haha 或者包含 hehe 的文件<br>
&ensp;  | &ensp;连接多个参数表示必须存在这些参数， 如 -k |haha|hehe 匹配同时含有haha和hehe的文件，同时出现'&'和'|'，以'|'为准<br>
&ensp;  ^ &ensp;连接多个参数表示不允许存在的参数， 如 -k &haha&hehe^hoho 匹配包含 haha 或者包含 hehe 但不允许出现 hoho 的文件，出现在-f参数后面如果为路径表示排除路径，为目录名则是排除所有这个名字的目录<br>
<br>
注意：<br>
&ensp;  windows的console 中'&' '|' '^' 要用 '^&' '^|' '^^' 表示<br>
&ensp;  linux的terminal 中'&' '|' '^' 要用 '\&' '\|' '\^' 表示<br>
<br><br>
关键词 -f -n -k 与 & | ^整合使用<br><br>
-f 表示目录名<br>
    &ensp;-f C:\a\&C:\b\&ensp;&ensp;&ensp;                     表示在这两个文件夹下搜索<br>
    &ensp;-f C:\a\&C:\b\^..理工&ensp;&ensp;&ensp;         表示在这两个文件夹下搜索，跳过名为'XX理工'的文件夹<br><br>
-n 表示文件名<br>
    &ensp;-n &.*?\.txt&.*?\.lua&ensp;&ensp;&ensp;            表示搜索文件后缀可以为txt或lua中的一个<br>
    &ensp;-n ^.*?\.txt^.*?\.lua&ensp;&ensp;&ensp;            表示搜索文件后缀不能为txt或lua，同时出现&和^以 ^优先<br><br>
-k 表示关键字<br>
    &ensp;-k &武汉&理工&ensp;&ensp;&ensp;                    表示搜索文件内容包含''武汉和'理工'的文件，出现任意一个关键词即可<br>
    &ensp;-k |武汉|理工&ensp;&ensp;&ensp;                        表示搜索必须包含''武汉和'理工'的文件，同时出现&和|以 |优先<br>
    &ensp;-k &武汉&理工^马房山&ensp;&ensp;&ensp;       搜索包含''武汉和'理工'，但不许出现'马房山'字样的文件<br>
<br>
例子<br>
<br>
在testFolder目录下搜索名字由字母组成并带字母后缀的文件<br>
&ensp;  java -jar MSearcher.jar -f C:\testFolder -n [a-zA-Z]+\.[a-zA-Z]+<br>
<br>
在testFolder目录下搜索任何包含java（-i不区分大小写）字样的文件<br>
&ensp;  java -jar MSearcher.jar -f C:\testFolder -k java -i<br>
<br>
在testFolder目录下搜索任何包含'理工'字样的文件，只用GBK和GB2312两种编码方式读取文件内容<br>
&ensp;  java -jar MSearcher.jar -f C:\testFolder -k 理工 -e GBK&GB2312<br>
<br>
在testFolder目录下搜索包含'武汉理工大学'字样的文本（默认支持GBK和UTF8），并列出名字包含'武汉理工大学'字样的文件和目录（-s参数）<br>
&ensp;  java -jar MSearcher.jar  -f C:\testFolder -n .*  -k 武汉理工大学 -s<br>
<br>
 在C:\testFolder和C:\testFolder2两个目录下搜索 txt或者lua 格式的文件 ，包含关键字'武汉理工大学'或者包含'泰晤士报'，但不允许出现'空调字样'的文件<br>
&ensp;  java -jar MSearcher.jar  -f C:\testFolder&C:\testFolder2 -n .*?\.txt&.*?\.lua  -k 武汉理工大学&泰晤士报^空调<br>
<br>
注意：<br>
1.空格只是为了看起来明显，解析时参数可以不用空格隔开。<br>
2.关键字（-f -n -k 这些参数后面的搜索内容）中出现 % & | ^ - 这些特殊符号要用 %% %& %| %^ %- 来转义表示<br>
&ensp;  比如 java -jar MSearcher.jar  -f C:\testFolder -n .*  -k 武汉理工%&大%^学 表示搜索的关键字为 武汉理工&大^学
<br>（正在做还没做完）<br>
3.后续想添加 -c 参数，支持在压缩文件（rar、zip、tar.gz）中搜索压缩的文件<br>
4.后续想添加 -u 限制文件的修改时间， -l 限制文件的大小<br>
5.正在做GUI，在没有-f参数的情况下启动gui界面



  
