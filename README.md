# Banner
BannerView
 * 这个bannerview就是一个普通的ViewPager
 * 只不过你传递进来的页面必须是满足CABCA模式，这样会实现循环播放
 * 另外反射传递一个新的自定义的scroller，实现控制换页面的时长
 * 另外一个task+postDelay实现自动轮播
 * 注意外界回调pos从1开始
