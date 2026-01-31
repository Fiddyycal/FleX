package io.flex.commons.cache.cell;

public class DataCell<A, B> implements BiCell<A, B> {
	
    private static final long serialVersionUID = 9026639648926583645L;
    
    public static <A, B> DataCell<A, B> of(A a, B b) {
        return new DataCell<A, B>(a, b);
    }
    
    private A a;
    private B b;
    
    private DataCell(A a, B b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public A a() {
        return this.a;
    }
    
    @Override
    public B b() {
        return this.b;
    }
    
    public void setA(A a) {
        this.a = a;
    }
    
    public void setB(B b) {
        this.b = b;
    }
    
}
