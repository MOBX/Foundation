package com.lamfire.pool;

/**
 * Created with IntelliJ IDEA.
 * User: lamfire
 * Date: 15-2-27
 * Time: ионГ9:56
 * To change this template use File | Settings | File Templates.
 */
public interface PoolableObjectFactory<E> {
    public E make();

    public void destroy(E instance);

    public boolean validate(E instance);

    public void activate(E instance);

    public void passivate(E instance);
}
