title: 基于链表的map结构实现
date: 2018-03-11 19:40:56
categories: [java]
tags: [java]
---

#  基于链表的map结构实现

## 定义map接口以及key比较器接口
```java
public interface Map<K, V> {
    /**
     * 获取map中元素数量
     *
     * @return
     */
    int size();

    /**
     * map是否为null
     *
     * @return
     */
    boolean isEmpty();

    /**
     * 获取key映射的元素
     *
     * @param key
     * @return
     */
    V get(K key);

    /**
     * 向map中放入映射
     *
     * @param key
     * @param val
     * @return
     */
    V put(K key, V val);

    /**
     * @param key
     * @return
     */
    V remove(K key);

    /**
     * 获取映射集合,使用集合迭代map
     * @return
     */
    Collection<Entry<K,V>> entries();



    interface Entry<K, V> {
        /**
         * 获取关键码
         *
         * @return
         */
        K getKey();

        /**
         * 获取entry值
         *
         * @return
         */
        V getValue();

        /**
         * 更新entry值
         *
         * @return
         */
        V setValue(V val);
    }
}


public interface KeyComparator<K> {
    default boolean equals(K key1,K key2){
        return  Objects.equals(key1,key2);
    }
}
```

## key比较器及map实现类
```java
//使用接口的默认实现
public class NormalKeyComparator<K> implements KeyComparator<K>{
}
```

```java
public class ListBaseMap<K, V> implements Map<K, V> {
    private LinkedList<Entry<K, V>> table;
    private KeyComparator<K> keyComparator;

    public ListBaseMap(LinkedList<Entry<K, V>> table, KeyComparator<K> keyComparator) {
        this.table = table;
        this.keyComparator = keyComparator;
    }

    public ListBaseMap(KeyComparator<K> keyComparator) {
        this.table = new LinkedList<>();
        this.keyComparator = keyComparator;
    }

    public ListBaseMap() {
        this.table = new LinkedList<>();
        this.keyComparator = new NormalKeyComparator<>();
    }


    static class Node<K, V> implements Entry<K, V> {
        private K key;
        private V value;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V val) {
            V old = this.value;
            this.value = val;
            return old;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj instanceof Entry) {
                Entry<?, ?> e = (Entry<?, ?>) obj;
                return Objects.equals(e.getKey(), this.getKey())
                        && Objects.equals(e.getValue(), this.getValue());
            }
            return false;
        }

        @Override
        public String toString() {
            return "ke=["+key+"],value=["+value+"]";
        }
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public boolean isEmpty() {
        return table.isEmpty();
    }

    @Override
    public V get(K key) {
        if (size() > 0) {
            for (Entry<K, V> entry : table) {
                if (Objects.equals(key, entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public V put(K key, V val) {
        Node<K, V> node = new Node<>(key, val);
        if (size() == 0) {
            table.add(node);
        } else {
            for (Entry<K, V> entry : table) {
                if (Objects.equals(key, entry.getKey())) {
                    V old = entry.getValue();
                    entry.setValue(val);
                    return old;
                }
            }
            table.add(node);
        }
        return null;
    }

    @Override
    public V remove(K key) {
        if (size() > 0) {
            int count = 0;
            for (Entry<K, V> entry : table) {
                if (Objects.equals(key, entry.getKey())) {
                    V oldVal = entry.getValue();
                    table.remove(count);
                    return oldVal;
                }
                count++;
            }
        }
        return null;
    }

    @Override
    public Collection<Entry<K, V>> entries() {
        return table;
    }
}
```