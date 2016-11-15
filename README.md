####支付宝芝麻分曲线图
####效果图：

![](https://raw.githubusercontent.com/FelixLee0527/ZhiMaScoreCurve/master/Demo.png)
   


####使用：
####XML

     <com.felix.library.ScoreTrend
        android:id="@+id/scoreTrend"
        android:background="#ffffff"
        android:layout_width="match_parent"
        android:layout_alignParentBottom="true"
        android:layout_height="300dp"/>
        
####attrs

    <resources>
        <declare-styleable name="ScoreTrend">
            <attr name="max_score" format="integer"/>
            <attr name="min_score" format="integer"/>
            <attr name="broken_line_color" format="color"/>
        </declare-styleable>
    </resources>
-- 暂时只有这些属性，因为没有上传到jcenter,所需需要拓展或者修改功能直接修改源码吧 
