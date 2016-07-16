package com.lamfire.pool;

/**
 * Created with IntelliJ IDEA.
 * User: lamfire
 * Date: 15-2-27
 * Time: ÉÏÎç10:24
 * To change this template use File | Settings | File Templates.
 */
public class CachedObjectPool<E> extends ObjectPool<E> {

    public CachedObjectPool(PoolableObjectFactory<E> objectFactory) {
        super(objectFactory);
    }

    public synchronized void returnObject(E e){
        if(getIdle() >= getMaxIdle()){
            destroy(e);
            return;
        }
        passivate(e);
        addLast(e);
        this.notifyAll();
    }

    public synchronized E borrowObject(){
        E result = null;
        do{
            if(super.isEmpty()){
                make();
            }
            E e = super.removeFirst();
            activate(e);
            if(validate(e)){
                result = e;
            }else{
                destroy(e);
            }
        }while(result == null);
        return result;
    }

    @Override
    public String toString() {
        return "CachedObjectPool{" +
                "maxIdle=" + getMaxIdle() +
                ", instance=" + getInstanceSize() +
                ", idle=" +  getIdle() +
                '}';
    }
}
