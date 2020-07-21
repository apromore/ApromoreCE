/*-
 * #%L
 * This file is part of "Apromore Community".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package ee.ut.utilities;

public class Quadruplet<A,B,C,D> implements Comparable{
    A a;
    B b;
    C c;
    D d;

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    public D getD() {
        return d;
    }

    public Quadruplet(A a, B b, C c, D d){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public int compareTo(Object triplet2){
        return ((String)((Quadruplet<A, B, C, D>)triplet2).getA()).compareTo((String) this.a);
    }

    @Override
    public boolean equals(Object triplet2) {
        return ((String) ((Quadruplet<A, B, C, D>) triplet2).getA()).equals((String) this.a);
    }

    public int hashCode(){
        return ((String) (this.getA())).hashCode();
    }

    public String toString(){
        return "<" + a.toString() + ", " + b.toString() + ", " + c.toString() + ", " + d.toString() + ">";
    }
}
