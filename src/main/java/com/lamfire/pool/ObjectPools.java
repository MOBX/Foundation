package com.lamfire.pool;

public class ObjectPools {

    public static <E> ObjectPool<E> makeObjectPool(PoolableObjectFactory<E> objectFactory, int maxInstance,
                                                   int idleInstance) {
        FixedObjectPool<E> pool = new FixedObjectPool<E>(objectFactory);
        pool.setMaxInstances(maxInstance);
        pool.setMaxIdle(idleInstance);
        return pool;
    }

    public static <E> ObjectPool<E> makeFixedObjectPool(PoolableObjectFactory<E> objectFactory, int instances) {
        FixedObjectPool<E> pool = new FixedObjectPool<E>(objectFactory);
        pool.setMaxInstances(instances);
        pool.setMaxIdle(instances);
        return pool;
    }

    public static <E> ObjectPool<E> makeSingleObjectPool(PoolableObjectFactory<E> objectFactory) {
        FixedObjectPool<E> pool = new FixedObjectPool<E>(objectFactory);
        pool.setMaxInstances(1);
        pool.setMaxIdle(1);
        return pool;
    }

    public static <E> ObjectPool<E> makeCachedObjectPool(PoolableObjectFactory<E> objectFactory, int idleInstance) {
        CachedObjectPool<E> pool = new CachedObjectPool<E>(objectFactory);
        pool.setMaxIdle(idleInstance);
        return pool;
    }
}
