package com.lamfire.pool;

import com.lamfire.utils.Lists;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对象池
 * User: lamfire
 * Date: 15-1-15
 * Time: 下午1:58
 * 提供对象托管
 */
public abstract class ObjectPool<E>{
    private int maxIdle = 1;
    private PoolableObjectFactory<E> objectFactory;

    private final AtomicInteger instanceCounter = new AtomicInteger();

    private final LinkedList<E> pool = Lists.newLinkedList();

    public ObjectPool(PoolableObjectFactory<E> objectFactory){
        this.objectFactory = objectFactory;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getInstanceSize(){
        return instanceCounter.get();
    }

    public int getIdle(){
        return this.pool.size();
    }

    public abstract void returnObject(E e);

    public abstract E borrowObject();


    void addLast(E e){
        this.pool.addLast(e);
    }

    boolean isEmpty(){
        return this.pool.isEmpty();
    }

    E removeFirst(){
        return this.pool.removeFirst();
    }

    void activate (E e){
        this.objectFactory.activate(e);
    }

    void passivate(E e){
        this.objectFactory.passivate(e);
    }

    boolean validate(E e){
        return this.objectFactory.validate(e);
    }

    synchronized void destroy(E e){
        this.objectFactory.destroy(e);
        instanceCounter.decrementAndGet();
    }


    /**
     * 创建实例
     */
    synchronized void make(){
        E instance = objectFactory.make();
        pool.addLast(instance);
        instanceCounter.incrementAndGet();
    }

    /**
     * 等待实例
     */
    synchronized void waitObjectInstance(){
        while(pool.isEmpty()){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
