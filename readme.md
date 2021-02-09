##### 目的 

  GMS_CHECK是一个能把目前项目的常见出错项目列出来并且能够自动快速检测check gms的一些常见出错项的apk，缩短遇到问题解决问题的周期。

| No.  | title                    |
| ---- | ------------------------ |
| 1    | Check APP Category       |
| 2    | fingertprinter 检查      |
| 3    | 相关版本检查             |
| 4    | 刘海屏项目标志位检查。   |
| 5    | 检查手机应用的sdk target |
| 6    | 添加GTS AssistantTest    |

#####　注意事项
　　在测试第六个项目GTS AssistantTest时先要安装VoiceSettingsService.apk,此时可以安装运行voiceSettingsServiceinstaller这个apk，他会先自动检测你的设备有没有安装所需要的apk，如果没有他会为你安装；如果已经完成了gms测试可以再次运行voiceSettingsServiceinstaller,他会自动卸载apk.
