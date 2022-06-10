# scanner-master

Android 扫码枪封装，Android端获取扫码枪数据一般有如下三种获取方式：

- USB键盘模式，自感模式（出厂默认）：大多数扫码枪是模拟键盘进行输入（不支持包含有汉字的二维码），大多数时候需要使用带焦点的 EditText 接收其扫描的信息。设置一个带焦点的EditText进行接收，此方式容易漏读扩展的ASCII码数据，除非二维码是标准的ASCII码（1-128），此外使用EditText不方便且焦点控制不好导致扫码不到数据。由于扫码枪会将扫描出来的内容转化为键盘事件，对应的就是Android中的KeyEvent事件，所以我们只需要在我们的activity中重写dispatchKeyEvent方法，即可获取相应事件。

- 通过 USB COM连接扫码：发送扫码开关指令进行扫码，即基于Usb相关类控制扫码开关，控制不好容易出问题
  **特别地**：使用此模式前提是先对扫码枪配置USB COM模式，再配置指令触发

- USB串口自感模式：即串口连接（当扫码枪使用 USB 通讯接口，但主机应用程序是采用串口通讯方式接收数据），客户端只负责监听读取数据，无须控制扫码枪指令，需要依赖usb串口传输数据的第三方库：https://github.com/mik3y/usb-serial-for-android
  **特别地**：使用此模式前提是先对扫码枪配置USB 虚拟串口通讯方式


# 使用

步骤 1. 在工程跟目录build.gradle文件下添加仓库


	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

步骤2. 添加依赖

	dependencies {
	      implementation 'com.github.kellysong:scanner-master:1.1.0'

	}

# 注意

1. 使用之前请检查扫码器模式是否设置正确
2. app build.gradle 配置

		compileOptions {
		   sourceCompatibility JavaVersion.VERSION_1_8
		   targetCompatibility JavaVersion.VERSION_1_8
		}