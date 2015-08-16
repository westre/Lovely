package net.westre.lovely;

import java.util.ArrayList;

public abstract class LovelyManager<T, E> {
    private ArrayList<T> pool = new ArrayList();

    public T get(E instance) {
        for (T poolObject : this.pool) {
            if (!(poolObject instanceof LovelyLinkable)) continue;
            System.out.println("Comparing " + instance + " to " + ((LovelyLinkable)poolObject).getLinkedObject());
            if (!((LovelyLinkable)poolObject).getLinkedObject().equals(instance)) continue;
            return poolObject;
        }
        return null;
    }

    public void remove(E poolObject) {
        T object = this.get(poolObject);
        if (object != null) {
            this.pool.remove(object);
        }
    }

    public void add(T instance) {
        this.pool.add(instance);
    }

    public ArrayList<T> getAll() {
        return this.pool;
    }
}

