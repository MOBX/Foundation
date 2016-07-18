package com.lamfire.pool;

public interface PoolableObjectFactory<E> {

    public E make();

    public void destroy(E instance);

    public boolean validate(E instance);

    public void activate(E instance);

    public void passivate(E instance);
}
