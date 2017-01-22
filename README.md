## FlymeTabStrip
Flyme6 ViewPager TabStrip
自定义实现Flyme6的ViewPager指示器，实现效果如下：

![效果图][1]

### github地址:[http://blog.csdn.net/u010072711/article/details/54667861](http://blog.csdn.net/u010072711/article/details/54667861)

## Gradle

[![](https://jitpack.io/v/Dawish/FlymeTabStrip.svg)](https://jitpack.io/#Dawish/FlymeTabStrip)

``` groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.Dawish:FlymeTabStrip:v1.0.2'
}
```
## Attrs

``` xml
    <declare-styleable name="FlymeTabStrip">
        <!-- 指示器高度 -->
        <attr name="indicatorHeight" format="dimension" />
        <!-- 指示器滑动条颜色 -->
        <attr name="indicatorColor" format="color" />
        <!-- 指示器左右间距 -->
        <attr name="indicatorMargin" format="dimension" />
        <!-- 指示器文字颜色 -->
        <attr name="indicatorTextColor" format="color" />
        <!-- 指示器文字大小 -->
        <attr name="indicatorTextSize" format="dimension" />
        <!-- 指示器文字被选中后的大小 -->
        <attr name="selectedIndicatorTextSize" format="dimension" />
    </declare-styleable>
```
## Example

``` java
    ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
    mViewPager.setAdapter(new ViewPagerAdapter());
    FlymeTabStrip tabStrip = (FlymeTabStrip) findViewById(R.id.tabstrip);
    tabStrip.setViewPager(mViewPager);
```

## Sample

[Sample sources][2]

[Sample APK][3]

[1]: ./assets/demo.gif
[2]: ./samples
[3]: ./assets/FlymeTabStrip_Demo_V1.0.2.apk