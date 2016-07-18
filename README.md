###1.自定义View动画
代码中通过两种方式实现，主要提供一种思路
#####方式一：
自定义动画采用View实现 
#####方式二：
自定义动画采用SurfaceView实现 
###2.说明
可以根据View的可见和是否有焦点来确定是否刷新，不会一直充绘消耗CPU
对onWindowVisibilityChanged，onWindowFocusChanged处理

###3.使用
在xml布局文件中加入
```
 <com.simple.canvas.view.CanvasView
        android:id="@+id/animview"
        android:layout_width="250dp"
        android:layout_height="250dp"
        android:layout_centerInParent="true"/>
```
###4.动画效果

![](http://uploadgif.55.la/upload/temp/2016/07/18/16/3138839935.gif)