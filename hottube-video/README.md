# 视频业务

## 综合解决方案选取

1. minio存储+range分片获取播放
2. **minio存储+m3u8分片流式处理**
3. 磁盘存储+分片方案

> 1. `ffmpeg`~~如何直接内存操作数据流 不通过磁盘读写从而提升速度~~
>    - 由于空间有限 这里需要限流->考虑消息队列 但是视频缓存在哪里 - 硬盘
>    - 还是需要通过**临时硬盘目录**存储视频文件 再通过异步处理视频分片和上传
> 2. 对于大视频文件如`GB`级别的 内存中放不下怎么处理 - 还是硬盘

## 处理思路

1. 前端文件切片上传 - 前台显示上传进度
```markdown

**整体步骤：**

- 前端计算文件 md5 查询上传进度 - 返回上传的id列表
- 前端按照进度信息继续上传 - 临时文件夹目录 = base+uploadId
- 如果上传完毕 前端调用完成上传的接口
    - 将视频处理的业务异步处理 同时将此条视频的状态标记为 **转码中**
    - 任务完成后 返回地址+更新状态

```
   - ~~秒传？通过文件hash判断是否存在 感觉没必要 不实现~~
   - 分片上传
   - 续传

2. 后端合并文件利用`ffmpeg`切片生成`m3u8`文件
3. 将视频文件上传到`minio`服务器 注意保存在同一目录中
   *redis中需要发布时存储 视频表的id键 视频处理完成后会进行url和状态的更新*