# 流SQL中的窗口概念

## 滚动窗口 Tumbling Window
将元素分配给每个固定长度的窗口,滚动窗口具有固定的尺寸,不重叠元素

![滑动窗口](/calcite-tutorial-1-basic/md/resource/window-tumbling1.png)


## 滑动窗口 Sliding Window
滑动窗口将元素分配给固定长度的窗口,并且附加每次窗口的滑动频率,可以存在窗口重叠的情况

![滑动窗口](/calcite-tutorial-1-basic/md/resource/window-sliding1.png)


## 会话窗口 Session Window
按照会话元素进行分组,会话窗口不重叠,没有固定的开始时间和结束时间,当一定时间没有接收到新的元素的话,则会话窗口关闭
![会话窗口](/calcite-tutorial-1-basic/md/resource/window-session1.png)


## 注意
具体窗口定义由不同软件具体定义,总体来说就这三种窗口的种类