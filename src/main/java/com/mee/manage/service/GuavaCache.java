package com.mee.manage.service;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mee.manage.vo.MeeProductVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class GuavaCache {

    private static final Logger logger = LoggerFactory.getLogger(GuavaCache.class);

    private LoadingCache<String,List<MeeProductVo>> cache;

    @Autowired
    private IProductsService productsService;

    //用于初始化cache的参数及其缺省值
    private int maximumSize = 1000;                 //最大缓存条数，子类在构造方法中调用setMaximumSize(int size)来更改
    private int expireAfterWriteDuration = 1;      //数据存在时长，子类在构造方法中调用setExpireAfterWriteDuration(int duration)来更改
    private TimeUnit timeUnit = TimeUnit.DAYS;   //时间单位（分钟）

    private Date resetTime;     //Cache初始化或被重置的时间
    private long highestSize=0; //历史最高记录数
    private Date highestTime;   //创造历史记录的时间


    /**
     * 通过调用getCache().get(key)来获取数据
     * @return cache
     */
    public LoadingCache<String, List<MeeProductVo>> getCache() {
        if(cache == null){  //使用双重校验锁保证只有一个cache实例
            synchronized (this) {
                if(cache == null){
                    CacheLoader<String, List<MeeProductVo>> loader = new CacheLoader<String, List<MeeProductVo>>() {
                        public List<MeeProductVo> load(String key) throws Exception {
                            logger.info("Loader MeeProduct By Url");
                            return productsService.getMeeProductsByUrl(key);
                        }
                    };

                    cache = CacheBuilder.newBuilder().maximumSize(maximumSize)      //缓存数据的最大条目，也可以使用.maximumWeight(weight)代替
                            .expireAfterWrite(expireAfterWriteDuration, timeUnit)   //数据被创建多久后被移除
                            .recordStats()                                          //启用统计
                            .build(loader);
                    this.resetTime = new Date();
                    this.highestTime = new Date();
                    logger.debug("本地缓存{}初始化成功", this.getClass().getSimpleName());
                }
            }
        }

        return cache;
    }

    public boolean refreshCache() {
        if(cache != null)
            cache = null;

        getCache();
        return true;
    }


    /**
     * 从缓存中获取数据（第一次自动调用fetchData从外部获取数据），并处理异常
     * @param key
     * @return Value
     * @throws ExecutionException
     */
    public List<MeeProductVo> getValue(String key) throws ExecutionException {
        List<MeeProductVo> result = getCache().get(key);
        if(getCache().size() > highestSize){
            highestSize = getCache().size();
            highestTime = new Date();
        }

        return result;
    }

    public long getHighestSize() {
        return highestSize;
    }

    public Date getHighestTime() {
        return highestTime;
    }

    public Date getResetTime() {
        return resetTime;
    }

    public void setResetTime(Date resetTime) {
        this.resetTime = resetTime;
    }

    public int getMaximumSize() {
        return maximumSize;
    }

    public int getExpireAfterWriteDuration() {
        return expireAfterWriteDuration;
    }

    /**
     * 设置最大缓存条数
     * @param maximumSize
     */
    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }

    /**
     * 设置数据存在时长（分钟）
     * @param expireAfterWriteDuration
     */
    public void setExpireAfterWriteDuration(int expireAfterWriteDuration) {
        this.expireAfterWriteDuration = expireAfterWriteDuration;
    }

}
