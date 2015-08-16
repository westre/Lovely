package net.westre.lovely;

import java.util.ArrayList;

public abstract class LovelyManager<T, E> {

    private ArrayList<T> pool = new ArrayList<T>();

    public T get(E instance) {
        for(T poolObject : pool) {
            if(poolObject instanceof LovelyLinkable) {
                System.out.println("Comparing " + instance + " to " + ((LovelyLinkable) poolObject).getLinkedObject());
                if(((LovelyLinkable) poolObject).getLinkedObject().equals(instance)) {
                    return poolObject;
                }
            }
        }
        return null;
    }

    public void remove(E poolObject) {
        T object = get(poolObject);

        if(object != null) {
            pool.remove(object);

            //System.out.println("REMOVED " + object.getClass().getTypeName() + " FROM " + pool.getClass().getTypeName() + " SIZE " + pool.size());
        }
    }

    public void add(T instance) {
        pool.add(instance);

        //System.out.println("ADDED " + instance.getClass().getTypeName() + " FROM " + pool.getClass().getTypeName() + " SIZE " + pool.size());
    }

    public ArrayList<T> getAll() {
        return pool;
    }
}
