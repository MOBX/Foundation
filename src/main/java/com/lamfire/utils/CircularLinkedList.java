package com.lamfire.utils;

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CircularLinkedList<E> extends AbstractSequentialList<E> implements List<E>, java.io.Serializable {
    private final Lock lock = new ReentrantLock();
	transient int size = 0;

	transient Node<E> first;

	transient Node<E> last;

    transient Node<E> current;

	public CircularLinkedList() {
	}

	public CircularLinkedList(Collection<? extends E> c) {
		this();
		addAll(c);
	}

    public E next(){
        lock.lock();
        try{
            if(size == 0){
                throw new NoSuchElementException();
            }
            if(size == 1){
                return first.item;
            }
            if(current == null || current.item == null){
                current = first;
            }else{
                current = current.next;
            }
            return current.item;
        }finally {
            lock.unlock();
        }
    }

    public E previous(){
        lock.lock();
        try{
            if(size == 0){
                throw new NoSuchElementException();
            }
            if(size == 1){
                return first.item;
            }
            if(current == null || current.item == null){
                current = first;
            }else{
                current = current.prev;
            }
            return current.item;
        }finally {
            lock.unlock();
        }
    }

    public void refresh(){
        lock.lock();
        try{
            current = null;
        }finally {
            lock.unlock();
        }
    }

	private void linkFirst(E e) {
        lock.lock();
        try{
            final Node<E> f = first;
            final Node<E> newNode = new Node<E>(last, e, f);
            first = newNode;
            if (f == null){
                last = newNode;
                first = newNode;
            }else{
                f.prev = newNode;
            }
            size++;
            modCount++;
        }finally {
            lock.unlock();
        }
	}

	void linkLast(E e) {
        lock.lock();
        try{
            final Node<E> l = last;
            final Node<E> newNode = new Node<E>(l, e, first);
            last = newNode;
            if (l == null){
                first = newNode;
                last = newNode;
            }else{
                l.next = newNode;
                first.prev = newNode;
            }
            size++;
            modCount++;
        }finally {
            lock.unlock();
        }
	}

	void linkBefore(E e, Node<E> succ) {
        lock.lock();
        try{
            // assert succ != null;
            final Node<E> pred = succ.prev;
            final Node<E> newNode = new Node<E>(pred, e, succ);
            succ.prev = newNode;
            if (pred == last)
                first = newNode;
            else
                pred.next = newNode;
            size++;
            modCount++;
        }finally {
            lock.unlock();
        }
	}

	E unlink(Node<E> x) {
        lock.lock();
        try{
            final E element = x.item;
            final Node<E> next = x.next;
            final Node<E> prev = x.prev;

            prev.next = next;
            next.prev = prev;

            if(x == first){
                first = next;
            }

            if(x == last){
                last = prev;
            }

            x.item = null;
            x.prev = null;
            x.next = null;

            if(--size==0){
                first =null;
                last=null;
            }
            modCount++;
            return element;
        }finally {
            lock.unlock();
        }
	}

	public E getFirst() {
		final Node<E> f = first;
		if (f == null)
			throw new NoSuchElementException();
		return f.item;
	}

	public E getLast() {
		final Node<E> l = last;
		if (l == null)
			throw new NoSuchElementException();
		return l.item;
	}

	public E removeFirst() {
		final Node<E> f = first;
		if (f == null)
			throw new NoSuchElementException();
		return unlink(f);
	}

	public E removeLast() {
		final Node<E> l = last;
		if (l == null)
			throw new NoSuchElementException();
		return unlink(l);
	}

	public void addFirst(E e) {
		linkFirst(e);
	}

	public void addLast(E e) {
		linkLast(e);
	}

	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	public int size() {
		return size;
	}

	public boolean add(E e) {
		linkLast(e);
		return true;
	}

	public boolean remove(Object o) {
        lock.lock();
        try{
            if (o == null) {
                for (Node<E> x = first; x != null; x = x.next) {
                    if (x.item == null) {
                        unlink(x);
                        return true;
                    }
                }
            } else {
                for (Node<E> x = first; x != null; x = x.next) {
                    if (o.equals(x.item)) {
                        unlink(x);
                        return true;
                    }
                }
            }
            return false;
        }finally {
            lock.unlock();
        }
	}

	public boolean addAll(Collection<? extends E> c) {
		return addAll(size, c);
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		checkPositionIndex(index);
        if (c==null || c.isEmpty()){
            return false;
        }

        Object[] array = c.toArray();
		int numNew = array.length;

        Node<E> oNode = node(index);
        Node<E> oNext = null;

        if(oNode != null){
            oNext = oNode.next;
        }

        for(Object o : array){
            E e  = (E)o;
            Node<E> newNode = new Node<E>(oNode,e,oNext);
            if(oNode == null){
                newNode.prev = newNode;
                newNode.next = newNode;
            }else{
                newNode.prev = oNode;
                newNode.next = oNext;
                oNode.next = newNode;
            }
            if(oNext == null){
                oNext = newNode;
            }
            oNode = newNode;

            if(first == null){
                first = oNode;
            }

            if(last == null||last == oNode.prev){
                last = oNode;
            }
        }

		size += numNew;
		modCount++;
		return true;
	}

	public void clear() {
        lock.lock();
        try{
            for (Node<E> x = first; x != null;) {
                Node<E> next = x.next;
                x.item = null;
                x.next = null;
                x.prev = null;
                x = next;
            }
            first = last = null;
            size = 0;
            modCount++;
        }finally {
            lock.unlock();
        }
	}

	public E get(int index) {
		checkElementIndex(index);
		return node(index).item;
	}

	public E set(int index, E element) {
		checkElementIndex(index);
		Node<E> x = node(index);
		E oldVal = x.item;
		x.item = element;
		return oldVal;
	}

	public void add(int index, E element) {
		checkPositionIndex(index);

		if (index == size)
			linkLast(element);
		else
			linkBefore(element, node(index));
	}

	public E remove(int index) {
		//checkElementIndex(index);
        Node<E> node = node(index);
		E e = unlink(node);
        return e;
	}

	
	private boolean isElementIndex(int index) {
		return index >= 0 && index < size;
	}

	private boolean isPositionIndex(int index) {
		return index >= 0 && index <= size;
	}

	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size;
	}

	private void checkElementIndex(int index) {
		if (!isElementIndex(index))
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private void checkPositionIndex(int index) {
		if (!isPositionIndex(index))
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * Returns the (non-null) Node at the specified element index.
	 */
	Node<E> node(int index) {
		// assert isElementIndex(index);
        if(index >= size){
            index &= size();
        }

		if (index < (size >> 1)) {
			Node<E> x = first;
			for (int i = 0; i < index; i++)
				x = x.next;
			return x;
		} else {
			Node<E> x = last;
			for (int i = size - 1; i > index; i--)
				x = x.prev;
			return x;
		}
	}

	public int indexOf(Object o) {
		int index = 0;
		if (o == null) {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item == null){
					return index;
                }
                if(x == last){
                    return -1;
                }
				index++;
			}
		} else {
			for (Node<E> x = first; x != null; x = x.next) {
				if (o.equals(x.item)){
					return index;
                }
                if(x == last){
                    return -1;
                }
				index++;
			}
		}
		return -1;
	}

	public int lastIndexOf(Object o) {
		int index = size;
		if (o == null) {
			for (Node<E> x = last; x != null; x = x.prev) {
				index--;
				if (x.item == null) {
					return index;
                }
                if(x == first){
                    return -1;
                }
			}
		} else {
			for (Node<E> x = last; x != null; x = x.prev) {
				index--;
				if (o.equals(x.item)) {
					return index;
                }
                if(x == first){
                    return -1;
                }
			}
		}
		return -1;
	}

	public ListIterator<E> listIterator(int index) {
		checkPositionIndex(index);
		return new CircularListIterator(index);
	}



	private static class Node<E> {
		E item;
		Node<E> next;
		Node<E> prev;

		Node(Node<E> prev, E element, Node<E> next) {
			this.item = element;
			this.next = next;
			this.prev = prev;
		}
	}

	/**
	 * @since 1.6
	 */
	public Iterator<E> descendingIterator() {
		return new DescendingIterator();
	}



	@SuppressWarnings("unchecked")
	private CircularLinkedList<E> superClone() {
		try {
			return (CircularLinkedList<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}


	public Object clone() {
		CircularLinkedList<E> clone = superClone();

		// Put clone into "virgin" state
		clone.first = clone.last = null;
		clone.size = 0;
		clone.modCount = 0;

		// Initialize clone with our elements
		for (Node<E> x = first; x != null; x = x.next){
			clone.add(x.item);
            if(x == last){
                break;
            }
        }

		return clone;
	}

	public Object[] toArray() {
        lock.lock();
        try{
            Object[] result = new Object[size];
            int i = 0;
            for (Node<E> x = first; x != null; x = x.next){
                result[i++] = x.item;
                if(x == last){
                    break;
                }
            }
            return result;
        }finally {
            lock.unlock();
        }
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < size){
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        }

        lock.lock();
        try{
            int i = 0;
            Object[] result = a;
            for (Node<E> x = first; x != null; x = x.next){
                result[i++] = x.item;
                if(x == last){
                    break;
                }
            }
            if (a.length > size)
                a[size] = null;

            return a;
        }finally {
            lock.unlock();
        }
	}

    private class CircularListIterator implements ListIterator<E> {
        private Node<E> lastReturned = null;
        private Node<E> next;

        CircularListIterator(int index) {
            if(index > 0 && index < size){
                next = node(index);
            }
        }

        public boolean hasNext() {
            return size > 0;
        }

        public E next() {
            if (!hasNext()){
                throw new NoSuchElementException();
            }
            lastReturned = next = (next == null) ? last : next.next;
            if(lastReturned == null){
                lastReturned = first;
            }
            return lastReturned.item;
        }

        public boolean hasPrevious() {
            return size > 0;
        }

        public E previous() {
            if (!hasPrevious()){
                throw new NoSuchElementException();
            }
            lastReturned = next = (next == null) ? last : next.prev;
            if(lastReturned == null){
                lastReturned = last;
            }
            return lastReturned.item;
        }

        @Override
        public int nextIndex() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public int previousIndex() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();

            Node<E> lastNext = lastReturned.next;
            unlink(lastReturned);
            if (next == lastReturned) {
                next = lastNext;
            }
            lastReturned = null;
        }

        public void set(E e) {
            if (lastReturned == null)
                throw new IllegalStateException();
            lastReturned.item = e;
        }

        public void add(E e) {
            lastReturned = null;
            if (next == null)
                linkLast(e);
            else
                linkBefore(e, next);
        }
    }

    /**
     * Adapter to provide descending iterators via ListItr.previous
     */
    private class DescendingIterator implements Iterator<E> {
        private final CircularListIterator it = new CircularListIterator(size());

        public boolean hasNext() {
            return it.hasPrevious();
        }

        public E next() {
            return it.previous();
        }

        public void remove() {
            it.remove();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try{
            Iterator<E> i = iterator();
            if (! i.hasNext())
                return "[]";

            StringBuilder sb = new StringBuilder();
            sb.append('[');
            Object[] array = toArray();
            for (Object o: array) {
                E e = (E)o;
                sb.append(e == this ? "(this Collection)" : e);
                if (e == last.item){
                    return sb.append(']').toString();
                }
                sb.append(", ");
            }
            return sb.toString();
        }finally {
            lock.unlock();
        }
    }
}
