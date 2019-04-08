# 水位线(水印) Watermark
和Apache Calcite基本无关,和流式SQL有关,作为附加学习内容

## Watermark的概念
数据流中经常出现事件时间(Event Time)乱序的情况,如果创建一个时间窗口为5s,但是数据延迟到达,
就会引起数据的不准确,为了解决类似的问题,提出了水位线概念

## Flink Watermark设计

### 周期Watermark (Periodic Watermark)
每个时间窗口,周期性生成watermark,和数据流中的数据事件时间没有关系
![周期Watermark](/calcite-tutorial-1-basic/md/resource/periodic-watermark.png)



### 标点Watermark (Punctuated Watermark)
根据数据流中的时间,生成watermark
![标点Watermark](/calcite-tutorial-1-basic/md/resource/punctuated-watermark.png)



