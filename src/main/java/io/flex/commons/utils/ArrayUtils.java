package io.flex.commons.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import io.flex.commons.Nullable;

@SuppressWarnings("unchecked")
public class ArrayUtils {

	@SafeVarargs
	public static <T> boolean contains(T[] array, T... contains) {
		
		if (array == null)
			throw new NullPointerException("array must not be null");
		
		if (contains == null)
			throw new NullPointerException("contains must not be null");
		
		if (contains.length == 0)
			return false;
		
		List<T> check = Arrays.asList(array);
		
		for (T c : contains)
			if (check.contains(c))
				return true;
		
		return false;
		
	}
	
	@SafeVarargs
	public static <T> T[] add(T[] array, T... add) {
		return add(null, array, add);
	}

	@SafeVarargs
	public static <T> T[] remove(T[] array, T... remove) {
		return remove(null, array, remove);
	}

	@SafeVarargs
	public static <T> T[] add(@Nullable Class<T> type, T[] array, T... add) {
		
		if (array == null)
			throw new NullPointerException("array must not be null");
		
		if (add == null || add.length == 0)
			return array;
		
		if (type == null) {
			
			if (array.length > 0)
				type = (Class<T>) array[0].getClass();
			
			else if (add[0] != null)
				type = (Class<T>) add[0].getClass();
			
		}
		
		int length = array.length;
		int size = length + add.length;
		
        T[] arr = (T[]) Array.newInstance(type != null ? type : /* Last resort, may not even work. */ Object.class, size);
		
		for (int i = 0; i < size; i++)
			arr[i] = i < length ? array[i] : add[i - length];
		
		return arr;
		
	}

	@SafeVarargs
	public static <T> T[] remove(@Nullable Class<T> type, T[] array, T... remove) {
		
		if (array == null)
			throw new NullPointerException("array must not be null");
		
		if (remove == null || remove.length == 0)
			return array;
		
		if (type == null) {
			
			if (array.length > 0)
				type = (Class<T>) array[0].getClass();
			
			else if (remove[0] != null)
				type = (Class<T>) remove[0].getClass();
			
		}
		
		int l = 0;
		int length = array.length;
		
        T[] arr = (T[]) Array.newInstance(type != null ? type : /* Last resort, may not even work. */ Object.class, length);
        
		loop: for (int i = 0, k = 0; i < length; i++) {
			
			T o = array[i];
			
			for (int j = 0; j < remove.length; j++) {
				
				T t = remove[j];
				
				if (o == t || o.equals(t)) {
					
					l++;
					continue loop;
					
				}
			}
			
			arr[k++] = o;
			
		}
        
        return Arrays.copyOf(arr, length - l);
		
	}
	
}
