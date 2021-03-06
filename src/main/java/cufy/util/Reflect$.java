/*
 *	Copyright 2020 Cufyorg
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package cufy.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Utils for dealing with java reflection.
 *
 * @author LSaferSE
 * @version 3 release (13-Jan-2020)
 * @since 19-Nov-2019
 */
final public class Reflect$ {
	/**
	 * This is a utility class and shouldn't be instanced!.
	 *
	 * @throws AssertionError when called
	 */
	private Reflect$() {
		throw new AssertionError("No instance for you!");
	}

	/**
	 * Get an array class of the given class.
	 *
	 * @param klass to get an array class of
	 * @param <C>   the targeted class
	 * @return an array class of the given class
	 * @throws NullPointerException if the given class is null
	 */
	public static <C> Class<C[]> asArrayClass(Class<C> klass) {
		Objects.requireNonNull(klass, "klass");
		return (Class<C[]>) Array.newInstance(klass, 0).getClass();
	}

	/**
	 * Get the class that extends {@link Object} that represent the given class.
	 *
	 * @param klass to get the object class of
	 * @return the class that extends Object class and represent the given class
	 * @throws NullPointerException     if the given class is null
	 * @throws IllegalArgumentException if the given class neither object nor primitive
	 */
	public static Class<?> asObjectClass(Class<?> klass) {
		Objects.requireNonNull(klass, "klass");
		Class<?> comp1, comp = klass.getComponentType();

		if (comp != null && (comp.isPrimitive() || comp.isArray()))
			//Avoiding creating empty arrays using arrayClass()
			return (comp1 = asObjectClass(comp)) == comp ? klass : asArrayClass(comp1);
		else if (klass.isPrimitive())
			if (klass == char.class)
				return Character.class;
			else if (klass == int.class)
				return Integer.class;
			else if (klass == boolean.class)
				return Boolean.class;
			else if (klass == byte.class)
				return Byte.class;
			else if (klass == double.class)
				return Double.class;
			else if (klass == float.class)
				return Float.class;
			else if (klass == long.class)
				return Long.class;
			else if (klass == short.class)
				return Short.class;
			else throw new IllegalArgumentException(klass + " neither object nor primitive");

		return klass;
	}

	/**
	 * Get the class that don't extends {@link Object} from the given class.
	 *
	 * @param klass to get the non-object class of
	 * @return the non-object class of the given class
	 * @throws IllegalArgumentException when the given class don't have a primitive type
	 * @throws NullPointerException     if the given class is null
	 */
	public static Class<?> asPrimitiveClass(Class<?> klass) {
		Objects.requireNonNull(klass, "klass");
		Class<?> comp = klass.getComponentType();
		Class<?> comp1;

		if (comp != null && (!comp.isPrimitive() || comp.isArray()))
			//Avoiding creating empty arrays using arrayClass()
			return (comp1 = asPrimitiveClass(comp)) == comp ? klass : asArrayClass(comp1);
		if (!klass.isPrimitive())
			if (klass == Character.class)
				return char.class;
			else if (klass == Integer.class)
				return int.class;
			else if (klass == Boolean.class)
				return boolean.class;
			else if (klass == Byte.class)
				return byte.class;
			else if (klass == Double.class)
				return double.class;
			else if (klass == Float.class)
				return float.class;
			else if (klass == Long.class)
				return long.class;
			else if (klass == Short.class)
				return short.class;
			else throw new IllegalArgumentException(klass + " don't have a primitive type");

		return klass;
	}

	/**
	 * Get ALL the fields that the given class have. ALL means ALL.
	 *
	 * @param klass the class to get all the fields that it have
	 * @return ALL the fields that the given class have
	 * @throws NullPointerException if the given class is null
	 */
	public static List<Field> getAllFields(Class<?> klass) {
		List<Field> fields = new ArrayList<>(Arrays.asList(klass.getDeclaredFields()));

		for (Class<?> superclass = klass.getSuperclass(); superclass != null; superclass = superclass.getSuperclass())
			fields.addAll(Arrays.asList(superclass.getDeclaredFields()));

		return fields;
	}

	/**
	 * Get all methods that can be invoked to the given class. Overriding methods will cancel the Overridden methods. No duplicated methods will be
	 * returned.
	 *
	 * @param klass the class to get the methods from
	 * @return the list of all methods that can be invoked to the given class. (no overridden methods)
	 * @throws NullPointerException if the given class is null
	 */
	public static List<Method> getAllMethods(Class<?> klass) {
		Objects.requireNonNull(klass, "klass");
		List<Method> methods = new ArrayList<>(Arrays.asList(klass.getDeclaredMethods()));

		//foreach super class of the given class
		for (Class<?> superClass = klass.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
			List<Method> superMethods = new ArrayList<>(10);
			for0:
			for (Method method : superClass.getDeclaredMethods()) {
				//Check if the method have been overridden on any of the subclasses
				for (Method override : methods)
					if (overrides(method, override))
						continue for0;
				superMethods.add(method);
			}
			//dump the non-overridden methods to the final methods list
			methods.addAll(superMethods);
		}

		return methods;
	}

	/**
	 * Check if the given 'override' method do override the 'base' method or not.
	 *
	 * @param base     the method that expected to be overridden
	 * @param override the method that expected to be overriding the base method
	 * @return true if the given 'override' method do override the 'base' method or not
	 * @throws NullPointerException if ether the given 'base' or 'override' is null
	 */
	public static boolean overrides(Method base, Method override) {
		Objects.requireNonNull(base, "base");
		Objects.requireNonNull(override, "override");

		if (base == override)
			return true;
		if (base.getParameterCount() != override.getParameterCount() ||
			!base.getName().equals(override.getName()) ||
			!base.getDeclaringClass().isAssignableFrom(override.getDeclaringClass()) ||
			!base.getReturnType().isAssignableFrom(override.getReturnType()))
			return false;

		Class<?>[] params0 = base.getParameterTypes();
		Class<?>[] params1 = override.getParameterTypes();
		for (int i = 0; i < params0.length; i++)
			if (params0[i] != params1[i])
				return false;
		return true;
	}

	/**
	 * Cast the given value to the given class. If the given class is primitive. And the given value's class can be casted to that primitive class.
	 * Then the cast will be preformed. Otherwise a {@link ClassCastException} will be thrown.
	 *
	 * @param klass the class to cast the given value to
	 * @param value the value to be casted to the given class
	 * @return the given value casted to the given class. Or null if the given value is null and the given class isn't primitive
	 * @throws NullPointerException if the given 'klass' is null. Or if the given class is primitive and the given value is null
	 * @throws ClassCastException   if the given value can't be casted to the given class
	 */
	public static Object primitiveCast(Class<?> klass, Object value) {
		//DON'T ASK ME WHY! 🤬. This is all Java primitive type's fault.
		//I couldn't came up with a better solution!.
		//Send me if you have a solution you think it's better.
		Objects.requireNonNull(klass, "klass");
		if (value == null)
			if (klass.isPrimitive())
				throw new NullPointerException("value");
			else return null;
		if (klass.isInstance(value))
			return value;

		Class<?> vc = value.getClass();
		if (klass == boolean.class || klass == Boolean.class) {
			if (vc == Boolean.class)
				return value;
		} else if (klass == byte.class || klass == Byte.class) {
			if (vc == Byte.class)
				return value;
			else if (vc == Character.class)
				return (byte) (char) (Character) value;
			else if (vc == Double.class)
				return (byte) (double) (Double) value;
			else if (vc == Float.class)
				return (byte) (float) (Float) value;
			else if (vc == Integer.class)
				return (byte) (int) (Integer) value;
			else if (vc == Long.class)
				return (byte) (long) (Long) value;
			else if (vc == Short.class)
				return (byte) (short) (Short) value;
		} else if (klass == char.class || klass == Character.class) {
			if (vc == Byte.class)
				return (char) (byte) (Byte) value;
			else if (vc == Character.class)
				return value;
			else if (vc == Double.class)
				return (char) (double) (Double) value;
			else if (vc == Float.class)
				return (char) (float) (Float) value;
			else if (vc == Integer.class)
				return (char) (int) (Integer) value;
			else if (vc == Long.class)
				return (char) (long) (Long) value;
			else if (vc == Short.class)
				return (char) (short) (Short) value;
		} else if (klass == double.class || klass == Double.class) {
			if (vc == Byte.class)
				return (double) (byte) (Byte) value;
			else if (vc == Character.class)
				return (double) (char) (Character) value;
			else if (vc == Double.class)
				return value;
			else if (vc == Float.class)
				return (double) (float) (Float) value;
			else if (vc == Integer.class)
				return (double) (int) (Integer) value;
			else if (vc == Long.class)
				return (double) (long) (Long) value;
			else if (vc == Short.class)
				return (double) (short) (Short) value;
		} else if (klass == float.class || klass == Float.class) {
			if (vc == Byte.class)
				return (float) (byte) (Byte) value;
			else if (vc == Character.class)
				return (float) (char) (Character) value;
			else if (vc == Double.class)
				return (float) (double) (Double) value;
			else if (vc == Float.class)
				return value;
			else if (vc == Integer.class)
				return (float) (int) (Integer) value;
			else if (vc == Long.class)
				return (float) (long) (Long) value;
			else if (vc == Short.class)
				return (float) (short) (Short) value;
		} else if (klass == int.class || klass == Integer.class) {
			if (vc == Byte.class)
				return (int) (byte) (Byte) value;
			else if (vc == Character.class)
				return (int) (char) (Character) value;
			else if (vc == Double.class)
				return (int) (double) (Double) value;
			else if (vc == Float.class)
				return (int) (float) (Float) value;
			else if (vc == Integer.class)
				return value;
			else if (vc == Long.class)
				return (int) (long) (Long) value;
			else if (vc == Short.class)
				return (int) (short) (Short) value;
		} else if (klass == long.class || klass == Long.class) {
			if (vc == Byte.class)
				return (long) (byte) (Byte) value;
			else if (vc == Character.class)
				return (long) (char) (Character) value;
			else if (vc == Double.class)
				return (long) (double) (Double) value;
			else if (vc == Float.class)
				return (long) (float) (Float) value;
			else if (vc == Integer.class)
				return (long) (int) (Integer) value;
			else if (vc == Long.class)
				return value;
			else if (vc == Short.class)
				return (long) (short) (Short) value;
		} else if (klass == short.class || klass == Short.class) {
			if (vc == Byte.class)
				return (short) (byte) (Byte) value;
			else if (vc == Character.class)
				return (short) (char) (Character) value;
			else if (vc == Double.class)
				return (short) (double) (Double) value;
			else if (vc == Float.class)
				return (short) (float) (Float) value;
			else if (vc == Integer.class)
				return (short) (int) (Integer) value;
			else if (vc == Long.class)
				return (short) (long) (Long) value;
			else if (vc == Short.class)
				return value;
		}

		throw new ClassCastException("Cant cast " + value.getClass() + " to " + klass);
	}
}
