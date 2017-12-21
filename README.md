# ayau-logger 阿呀哟 logger

ayau-logger 是Android日志输出工具，可以格式化输出Json字符串，Xml字符串和简单的Java对象；可输出超长信息，并对输出的日志信息进行了美化；

   可自定义全局标记，控制Log打印开关，显示方法调用堆栈；既可打印信息到Logcat,也能输出到文件。

感谢[Logger](https://github.com/orhanobut/logger),[android-ueueo-log](https://github.com/lijinzhe/android-ueueo-log) ，因为是基于Logger和 android-ueueo-log,然后根据自己的需求做的一些修改。

特性
--------

*   输出Json，Xml和Java对象（包括数组，集合，Map），并进行了格式化缩进；
*   输出打印日志的方法调用栈信息；
*   输出当前线程信息；
*   支持将日志存储到文件中；
*   可自由控制日志输出级别；
*   支持日志的拼接组合输出；
*   支持超长日志输出；
*   文件输出更安全；
*   既可以初始化时配置输出参数，也可以在单个方法中进行临时改变；
*   可打印的对象类型，进行了扩展；


![](https://github.com/nx1988/ayauLogger/static/image1.png)


下载和导入
--------
可以从Github上下载源码：[ayau_logger](https://github.com/nx1988/ayauLogger)

在主工程根目录的build.gradle中，添加maven库
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

项目中使用Gradle导入：

```gradle
dependencies {
    compile 'com.github.nx1988:ayauLogger:1.0.0'
}
```

如何使用
--------

### 初始化

`Logger`共提供了4个初始化方法：

```java
在Application的oncreate方法中进行初始化：
Logger.init("mytag");
Logger.init("mytag", 2);
Logger.init("mytag", 3, Logger.INFO);
Logger.init("mytag", 4, Logger.INFO, true);
```

第一个参数：全局日志打印的Tag（默认为`NX`）;

第二个参数：打印方法调用栈的数量（默认为 `1`）；

第三个参数：指定日志打印级别，只有要输出的日志级别大于等于(>=)此参数值，才会打印。
    日志级别从低到高分别为：
    VERBOSE=2,DEBUG=3,INFO=4,WARN=5,ERROR=6,ASSERT=7,NONE=8，当指定为NONE时就不会输出任何日志了；

第四个参数：指定日志是否保存到文件中（默认为`false`不保存）。
    日志文件存储路径为外部存储空间的根目录下 `nxlogger`文件夹里，日志文件会根据不同的Tag而存储在不同的文件夹中，当程序运行打印第一条日志时会根据当前时间创建日志文件，并且此次运行都存储在此日志文件中，当退出应用重新启动进程，则会创建新的日志文件。


**如果不进行任何初始化操作，则所有参数都为默认值。**

### 输出Json，Xml和Java对象

```java
//输出Json字符串
Logger.json("{\"id\":221,\"name\":\"my name is ueueo\",\"desc\":\"this is description!\"}");
//输出Xml字符串
Logger.xml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><html><title>this is a title</title><body>这个是网页</body></html>");

//创建Java对象
User user = new User();
user.id = 102;
user.name = "UEUEO";
user.age = 22;
//输出对象
Logger.object(user);   
             
```

输出结果分别如下图：

![](https://raw.githubusercontent.com/lijinzhe/android-ueueo-log/master/static/image3.png)

### 输出打印日志的方法调用栈信息

![](https://raw.githubusercontent.com/lijinzhe/android-ueueo-log/master/static/image2.png)

上图分别为输出1级方法调用栈，输入3级方法调用栈和不输出方法调用栈的日志输出结果。

前两条日志当有方法调用栈输出时，日志信息会通过边框美化输出，而第三条日志因为不需要输出方法调用栈信息，而且日志信息是单行日志，输出时为了不占用控制台输出空间，所以不会添加边框。但是如果输出多行日志则会有边框，例如：

```java
Logger.i("第一行日志 \n 换行输出日志");
```

输出结果如下：

![](https://raw.githubusercontent.com/lijinzhe/android-ueueo-log/master/static/image11.png)

### Exception输出

```java
try {
    Object obj = null;
    obj.toString();
} catch (Exception e) {
    Logger.e(e, "空指针异常");
}
```

输出结果如下：

![](https://raw.githubusercontent.com/lijinzhe/android-ueueo-log/master/static/image4.png)

### 输出有不定参数的字符串日志

```java
     Logger.i("指定参数的日志输出  参数1:%d  参数2：%s   参数3：%s", 110, "apple", "ueueo");
```

输出结果如下：

![](https://raw.githubusercontent.com/lijinzhe/android-ueueo-log/master/static/image7.png)

### 设置当前要打印日志的Tag，方法调用栈数量和文件存储

上面说明了，当调用`UELog`的`init`方法进行初始化时，可以指定日志的Tag等配置信息，这些配置影响的是全局的日志输出，但是有些时候我们可能希望当前要输出的日志与`init`方法指定的配置不一样，例如：

`init`时指定Tag为AAA，但是当前的日志希望Tag为BBB，则：

```java
Logger.init("AAA");            
Logger.i("输出的日志Tag是AAA");
Logger.tag("BBB").i("输出的日志Tag是AAA");
Logger.i("再次输出的日志Tag是AAA");
```

输出结果如下：

![](https://raw.githubusercontent.com/lijinzhe/android-ueueo-log/master/static/image5.png)

除了可以单独指定Tag外，还可以指定方法调用栈显示数量和是否存储到文件：

```java
Logger.tag("BBB").method(3).file(true).i("输出的日志Tag是BBB，显示方法数量为3，并且保存到文件中");
```

### 日志的拼接组合输出

为了方便大家理解我所说的日志的拼接组合，我先来举个例子：

当发送网络请求时，需要打印请求的URL、请求参数和返回结果，一般的做法是：

```java
//打印请求地址
Logger.i("POST  http://www.baidu.com/api/gps");
//打印请求参数
Logger.json("{\"id\":221}");
//打印返回结果
Logger.json("{\"name\":\"my name is ueueo\",\"desc\":\"this is description!\"}");
```

也就是分步打印数据，这样打印出来的结果如下：

![](https://raw.githubusercontent.com/lijinzhe/android-ueueo-log/master/static/image8.png)

这样看其实也挺清楚明白的，但是当网络请求多线程并发时，上面的日志就有可能变成如下：

![](https://raw.githubusercontent.com/lijinzhe/android-ueueo-log/master/static/image9.png)

这时候你还能看出谁是谁的参数，谁是谁的结果吗，肯定是不行的，而日志的拼接组合就是为了解决这个问题，我们先来看看下面的日志输出：

![](https://raw.githubusercontent.com/lijinzhe/android-ueueo-log/master/static/image10.png)

看到这样的输出是不是更加的清楚明白，那这个日志是怎样输出的呢？如下：

```java
//拼接合并输出
Logger.append("POST  http://www.baidu.com/api/gps");
Logger.append("请求参数");
Logger.appendJson("{\"id\":221}");
Logger.append("返回结果");
Logger.json("{\"name\":\"my name is ueueo\",\"desc\":\"this is description!\"}");
```

UELog提供了append方法，可以对多次要输出的内容进行拼接，然后最后一次行的输出，append方法有：

```java
Logger.append("字符串");//拼接字符串
Logger.appendJson("{\"id\":221}");//拼接Json字符串
Logger.appendXml("<html></html>");//拼接Xml字符串
Logger.appendObject(obj);//拼接对象
```

也可以这样拼接：

```java
Logger.append("字符串").appendJson("{\"id\":221}").appendXml("<html></html>").appendObject(obj).i("输出");
```

`append`方法并不会进行日志输出，只有调用了日志输出方法才会最终输出的控制台，输出方法就是：

```java
Logger.v("verbose level log");
Logger.d("debug level log");
Logger.i("info level log");
Logger.w("warn level log");
Logger.e("error level log");
Logger.wtf("assert level log");
Logger.json("json string log");
Logger.xml("xml string log");
Logger.object(obj);
```

**注意：**

*   其中json，xml和object的输出都是以debug等级输出的；
*   `append`方法的调用必须是在同一线程内才有效，所以最好保证你的`append`方法的调用都是在同一个方法里，而且调用日志输出方法输出日志之后，`append`拼接的日志将被清空，再次打印的日志将没有之前的拼接信息；

### 作者 niexuan 






