/*
 * Copyright 2014-2015 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grayfox.android.app.util;

import java.io.Serializable;

public class Pair<T0, T1> implements Serializable {

    private static final long serialVersionUID = 6944537869484307573L;

    public final T0 _0;
    public final T1 _1;

    public Pair(T0 _0, T1 _1) {
        this._0 = _0;
        this._1 = _1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_0 == null) ? 0 : _0.hashCode());
        result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Pair<T0, T1> other = (Pair<T0, T1>) obj;
        if (_0 == null) {
            if (other._0 != null) return false;
        } else if (!_0.equals(other._0)) return false;
        if (_1 == null) {
            if (other._1 != null) return false;
        } else if (!_1.equals(other._1)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "<" + _0 + ", " + _1 + ">";
    }
}