/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.datastructures.multilevelmap.impl;

import com.raffaeleconforti.datastructures.multilevelmap.MultiLevelMap;
import com.raffaeleconforti.datastructures.multilevelmap.exceptions.LevelNumberOutOfBoundException;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 20/03/2016.
 */
public class MultiLevelHashMap<K, V> implements MultiLevelMap<K, V> {

    private InnerMultiLevelMap<K, V> innerMultiLevelMap;
    private int levels = 0;

    public MultiLevelHashMap(int levels) {
        this.levels = levels;
        innerMultiLevelMap = new InnerMultiLevelMap<>(levels);
    }

    @Override
    public void clear() {
        innerMultiLevelMap.clear();
    }

    @Override
    public boolean containsKeys(K... keys) {
        return innerMultiLevelMap.containsKeys(keys);
    }

    @Override
    public boolean containsValues(V value) {
        return innerMultiLevelMap.containsValue(value);
    }

    @Override
    public V get(K... keys) {
        return innerMultiLevelMap.get(keys);
    }

    @Override
    public void put(V value, K... keys) {
        innerMultiLevelMap.put(value, keys);
    }

    @Override
    public V remove(K... keys) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    private class InnerMultiLevelMap<K, V> {

        private Map<K, InnerMultiLevelMap> innerMultiLevelMaps;
        private Map<K, V> innerMap;
        private int level = 1;

        public InnerMultiLevelMap(int level) {
            this.level = level;
            if(level > 1) {
                innerMultiLevelMaps = new UnifiedMap<>();
            }else {
                innerMap = new UnifiedMap<>();
            }
        }

        public void clear() {
            if(level == 1) {
                innerMap.clear();
            }else {
                for(InnerMultiLevelMap innerMultiLevelMap : innerMultiLevelMaps.values()) {
                    innerMultiLevelMap.clear();
                }
            }
        }

        public boolean containsKeys(K... keys) {
            return get(keys) != null;
        }

        public boolean containsValue(V value) {
            boolean result = false;
            if(level == 1) {
                result = innerMap.containsValue(value);
            }else {
                for(InnerMultiLevelMap innerMultiLevelMap : innerMultiLevelMaps.values()) {
                    result |= innerMultiLevelMap.containsValue(value);
                }
            }
            return result;
        }

        public V get(K... keys) throws LevelNumberOutOfBoundException {
            if(keys.length == level) {
                if(keys.length == 1) {
                    return innerMap.get(keys[0]);
                }else {
                    InnerMultiLevelMap innerMultiLevelMap = null;
                    if ((innerMultiLevelMap = innerMultiLevelMaps.get(keys[0])) != null) {
                        return (V) innerMultiLevelMap.get(Arrays.copyOfRange(keys, 1, keys.length));
                    }
                    return null;
                }
            }else {
                throw new LevelNumberOutOfBoundException();
            }
        }

        public void put(V value, K... keys) throws LevelNumberOutOfBoundException {
            if(keys.length == level) {
                if(keys.length == 1) {
                    innerMap.put(keys[0], value);
                }else {
                    InnerMultiLevelMap innerMultiLevelMap = null;
                    if ((innerMultiLevelMap = innerMultiLevelMaps.get(keys[0])) == null) {
                        innerMultiLevelMap = new InnerMultiLevelMap(level - 1);
                        innerMultiLevelMaps.put(keys[0], innerMultiLevelMap);
                    }
                    innerMultiLevelMap.put(value, Arrays.copyOfRange(keys, 1, keys.length));
                }
            }else {
                throw new LevelNumberOutOfBoundException();
            }
        }

        public V remove(K... keys) throws LevelNumberOutOfBoundException {
            if(keys.length == level) {
                if(keys.length == 1) {
                    return innerMap.remove(keys[0]);
                }else {
                    InnerMultiLevelMap innerMultiLevelMap = null;
                    if ((innerMultiLevelMap = innerMultiLevelMaps.get(keys[0])) == null) {
                        V res = (V) innerMultiLevelMap.remove(Arrays.copyOfRange(keys, 1, keys.length));
                        if(innerMultiLevelMap.size() == 0) {
                            innerMultiLevelMaps.remove(keys[0]);
                        }
                        return res;
                    }
                    return null;
                }
            }else {
                throw new LevelNumberOutOfBoundException();
            }
        }

        public int size() {
            int size = 0;
            if(level == 1) {
                size = innerMap.size();
            }else {
                for(InnerMultiLevelMap innerMultiLevelMap : innerMultiLevelMaps.values()) {
                    size += innerMultiLevelMap.size();
                }
            }
            return size;
        }
    }
}
