package cufy.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@SuppressWarnings("JavaDoc")
public class Map$Test {
	@Test
	public void listFor() {
		Map map = new HashMap();

		map.put("fake", 0);
		map.put("fake", 1);
		map.put("fake", 2);

		map.put(0, "zero");
		map.put(3, "three");
		map.put(7, "seven");

		List list = Map$.listFor(map);

		list.add("four"); //eight
		list.add(9, "ten"); //nine
		list.add("eleven"); //ten
		list.add(9, "nine");
		list.set(8, "eight");

		Assert.assertEquals("wrong size calc", 12, list.size());

		Assert.assertEquals("Element has changed while it shouldn't", "zero", map.get(0));
		Assert.assertNull("Element should not exist", map.get(1));
		Assert.assertNull("Element should not exist", map.get(2));
		Assert.assertEquals("Element has changed while it shouldn't", "three", map.get(3));
		Assert.assertNull("Element should not exist", map.get(4));
		Assert.assertNull("Element should not exist", map.get(5));
		Assert.assertNull("Element should not exist", map.get(6));
		Assert.assertEquals("Element has changed while it shouldn't", "seven", map.get(7));
		Assert.assertEquals("Element has not changed", "eight", map.get(8));
		Assert.assertEquals("Element has not changed", "nine", map.get(9));
		Assert.assertEquals("Shift indexes error", "ten", map.get(10));
		Assert.assertEquals("Shift indexes error", "eleven", map.get(11));

		Assert.assertEquals("Wrong element", "zero", list.get(0));
		Assert.assertNull("Wrong element", list.get(1));
		Assert.assertNull("Wrong element", list.get(1));
		Assert.assertEquals("Wrong element", "three", list.get(3));
		Assert.assertNull("Wrong element", list.get(4));
		Assert.assertNull("Wrong element", list.get(5));
		Assert.assertNull("Wrong element", list.get(6));
		Assert.assertEquals("Wrong element", "seven", list.get(7));
		Assert.assertEquals("Wrong element", "eight", list.get(8));
		Assert.assertEquals("Wrong element", "nine", list.get(9));
		Assert.assertEquals("Wrong element", "ten", list.get(10));
		Assert.assertEquals("Wrong element", "eleven", list.get(11));

		try {
			list.add(13, null);
			Assert.fail("IndexOutOfBounds not thrown");
		} catch (IndexOutOfBoundsException ignored) {
		}

		try {
			list.remove(12);
			Assert.fail("IndexOutOfBounds not thrown");
		} catch (IndexOutOfBoundsException ignored) {
		}

		try {
			list.set(12, null);
			Assert.fail("IndexOutOfBounds not thrown");
		} catch (IndexOutOfBoundsException ignored) {
		}
	}

	@Test
	public void mapFor() {
		class TestObject {
			final public int fin = 5;
			public int pub = 3;
			protected int pro = 8;
			int pac = 4;
//			private int pri = 11;
		}

		TestObject instance = new TestObject();
		Map<String, Integer> remote = (Map<String, Integer>) (Map) Map$.mapFor(instance);

		Assert.assertEquals("Can't get public fields", (Integer) 3, remote.get("pub"));
		Assert.assertEquals("Can't get package-private fields", (Integer) 4, remote.get("pac"));
		Assert.assertEquals("Can't get protected fields", (Integer) 8, remote.get("pro"));
//		Assert.assertEquals("Can't get private fields", (Integer) 11, remote.get("pri"));

		remote.put("pub", 20);
		remote.put("pro", 30);
		remote.put("pac", 40);
//		remote.put("pri", 50);

		Assert.assertEquals("Can't replace public fields", (Integer) 20, remote.get("pub"));
		Assert.assertEquals("Can't replace package-private fields", (Integer) 30, remote.get("pro"));
		Assert.assertEquals("Can't replace protected fields", (Integer) 40, remote.get("pac"));
//		Assert.assertEquals("Can't replace private fields", (Integer) 50, remote.get("pri"));

		try {
			remote.remove("pac");
			Assert.fail("Remove should not work");
		} catch (UnsupportedOperationException ignored) {
		}
		try {
			remote.clear();
			Assert.fail("Clear should not work");
		} catch (UnsupportedOperationException ignored) {
		}
		try {
			remote.put("x", 1);
			Assert.fail("No such field!");
		} catch (IllegalArgumentException ignored) {
		}
		try {
			((Map) remote).put("pro", "ABCDEF");
			Assert.fail("IllegalArgumentException for different type is expected");
		} catch (IllegalArgumentException ignored) {
		}

		HashSet<Integer> set = new HashSet<>();

		((Map) remote).forEach((k, v) -> {
			//this$0 and other arguments :)
			if (v instanceof Integer)
				set.add((int) v);
		});

		Assert.assertTrue("Missing value", set.contains(20));
		Assert.assertTrue("Missing value", set.contains(30));
		Assert.assertTrue("Missing value", set.contains(40));
//		Assert.assertTrue("Missing value", set.contains(50));
	}
}
