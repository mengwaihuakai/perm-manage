package com.solid.subscribe.util;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fanyongju
 * @Title: IdWorker
 * @date 2018/10/1219:35
 */
@Component
public class IdWorker {
    private static IdWork idWorker;

    @PostConstruct
    public static void init() {
        idWorker = new IdWork(1,1,0);
    }

    public static Long generateId() {
        return idWorker.nextId();
    }

    private static class IdWork {
        private long workerId;
        private long datacenterId;
        private long sequence;

        public IdWork(long workerId, long datacenterId, long sequence) {
            // sanity check for workerId
            if (workerId > maxWorkerId || workerId < 0) {
                throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
            }
            if (datacenterId > maxDatacenterId || datacenterId < 0) {
                throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
            }
            this.workerId = workerId;
            this.datacenterId = datacenterId;
            this.sequence = sequence;
        }

        private long twepoch = 1539928011000L;//初始毫秒时间戳2018-10-19 13:46:51

        private long workerIdBits = 7L;//机器选择2^7(0-127)
        private long datacenterIdBits = 3L;//机房选择2^3(0-7)
        private long maxWorkerId = -1L ^ (-1L << workerIdBits);//^异或：两个比较的位不同时其结果是1，相同结果为0
        private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
        private long sequenceBits = 12L;

        private long workerIdShift = sequenceBits;
        private long datacenterIdShift = sequenceBits + workerIdBits;
        private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
        private long sequenceMask = -1L ^ (-1L << sequenceBits);

        private long lastTimestamp = -1L;

        public long getWorkerId() {
            return workerId;
        }

        public long getDatacenterId() {
            return datacenterId;
        }

        public long getTimestamp() {
            return System.currentTimeMillis();
        }

        //用同步锁保证线程安全
        public synchronized long nextId() {
            long timestamp = timeGen();
            //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
            if (timestamp < lastTimestamp) {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                        lastTimestamp - timestamp));
            }
            //如果是同一时间生成的，则进行毫秒内序列
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask;
                //sequence等于0说明毫秒内序列已经增长到最大值
                if (sequence == 0) {
                    //阻塞到下一个毫秒,获得新的时间戳
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {//时间戳改变，毫秒内序列重置
                sequence = 0;//这种重置可能会导致id分布不均，后期如果需要的话，建议改为0-4095之间的随机数
            }
            //记录上次生成ID的时间截
            lastTimestamp = timestamp;
        /*移位并通过或运算拼到一起组成64位的ID，将timestamp减去指定的初始时间戳twepoch结果左移timestampLeftShift(22)位
        ，然后与上左移datacenterIdShift(19)的datacenterId，再与上左移workerIdShift(12)位的workerId，再与上sequence。
        注意：此处转换成二进制进行操作*/
            return ((timestamp - twepoch) << timestampLeftShift) |
                    (datacenterId << datacenterIdShift) |
                    (workerId << workerIdShift) |
                    sequence;
        }

        private long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        private long timeGen() {
            return System.currentTimeMillis();
        }
    }
}
