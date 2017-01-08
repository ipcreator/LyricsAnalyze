
# 歌词解析器

乐乐第一个版本，使用的是KSC卡拉OK的歌词格式，该歌词格式，虽然可以实现动态歌词的效果， 解析也方便，但是该歌词文件的大小差不多是酷狗krc文件的两倍多，所以在存储时还是占用了比较大的空间。

# hrc (happy lyrics)歌词

乐乐第二个版本，使用自定义的歌词格式文件hrc (happy lyrics) 。 该版本可自制歌词并生成hrc歌词，它与酷狗krc比较，感觉还是不错的，占空间小了好多。

# hrcx (happy lyrics)歌词
hrc的简化和优化

#添加酷狗krc歌词的解析
#令我最意想不到的是，原来我自定义的歌词格式，和krc也有相似的地方，都是将字符串，进行了压缩处理。。。。
- 目前只实现了krc歌词的读取
- 参考帖子如下：
- http://www.jianshu.com/p/f6e7c8b9b2a3
- http://blog.csdn.net/qingzi635533/article/details/30231733
