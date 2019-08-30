package com.solid.subscribe.vo;

/**
 * @author fanyongju
 * @Title: Tuple2
 * @date 2019/8/27 16:54
 */
public class Tuple2<A,B> {
    private A a;
    private B b;

    public Tuple2() {
    }

    public Tuple2(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getT1() {
        return a;
    }

    public B getT2() {
        return b;
    }
}
