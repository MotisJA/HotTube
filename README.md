#  2024 Autumn JAVA EE 课程项目 `HotTube` 
## 项目简介
以视频为核心的内容创作平台，基于`Spring Cloud`的微服务项目

## 视频处理

为实现视频流播放 使用`ffmpeg`进行视频的切片操作 这里使用的是`@BtbN`打包的程序包 [下载地址](https://github.com/BtbN/FFmpeg-Builds)

暂定使用截至2024/11/21的latest版本 `ffmpeg version N-117844-g05e079c948-20241120`

## commit 提交规范
参考：[git commit 规范指南](https://segmentfault.com/a/1190000009048911) 此处为了开发效率做了简化

**基本格式**： `type : message`
其中`type`可以是如下选项：
- feat：新功能（feature）
- fix：修补bug
- docs：文档（documentation）
- style： 格式（不影响代码运行的变动）
- refactor：重构（即不是新增功能，也不是修改bug的代码变动）
- test：增加测试
- chore：构建过程或辅助工具的变动